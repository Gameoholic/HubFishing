package xyz.gameoholic.hubfishing.fish

import xyz.gameoholic.partigon.particle.PartigonParticle
import xyz.gameoholic.hubfishing.lake.FishLakeManager
import xyz.gameoholic.hubfishing.util.LoggerUtil
import xyz.gameoholic.hubfishing.particles.EpicFishParticle
import xyz.gameoholic.hubfishing.particles.FishCatchParticle
import xyz.gameoholic.hubfishing.particles.LegendaryFishParticle
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.particles.FishParticleType
import xyz.gameoholic.hubfishing.util.FishingUtil
import xyz.gameoholic.hubfishing.util.FishingUtil.getRarity


/**
 * Represents a fish currently in the lake.
 *
 * @param fishLakeManager: The lake where the fish will spawn
 * @param hitboxLocation: The location of the fish's hitbox without accounting for offset, also used for the X and Z values for the armor stand location.
 * @param variant: The fish's properties.
 * @param maxAliveTime: The max amount of ticks after which the fish will be removed.
 */
class Fish(
    val fishLakeManager: FishLakeManager,
    private var hitboxLocation: Location,
    val variant: FishVariant,
    private val maxAliveTime: Int
) {
    private val plugin: HubFishingPlugin by inject()

    val armorStand: ArmorStand
    private var aliveTime = 0

    /**
     * Whether the fish was caught and stopped in place.
     * Settings this to true stops its movement and makes it become uncatchable,
     * and setting back to false resumes its movement and makes it catchable.
     */
    var caught = false

    private val particles: List<PartigonParticle>

    private val AIManager: FishAIManager

    init {
        val armorStandLoc = hitboxLocation.clone().apply { this.y = fishLakeManager.armorStandYLevel }
        LoggerUtil.debug("Spawning fish $variant at $armorStandLoc")
        armorStand = armorStandLoc.world.spawnEntity(armorStandLoc, EntityType.ARMOR_STAND) as ArmorStand
        val key = NamespacedKey(plugin, "fishing-removable") //Mark entity for removal upon server start
        armorStand.persistentDataContainer.set(key, PersistentDataType.BOOLEAN, true)
        val itemStack = ItemStack(variant.material)
        val meta = itemStack.itemMeta
        meta.setCustomModelData(variant.modelData)
        itemStack.itemMeta = meta
        armorStand.isInvisible = true
        armorStand.isInvulnerable = true
        //Armor stand must have gravity, otherwise we can't give it velocity
        armorStand.equipment.setItem(EquipmentSlot.HEAD, itemStack)

        AIManager = FishAIManager(this)
        particles = when (getRarity(variant.rarityId).fishParticleType) {
            FishParticleType.EPIC -> listOf(
                LegendaryFishParticle.getParticle1(armorStand.location.apply {
                    this.y = fishLakeManager.surfaceYLevel
                }),
                LegendaryFishParticle.getParticle2(armorStand.location.apply { this.y = fishLakeManager.surfaceYLevel })
            )
            FishParticleType.LEGENDARY -> listOf(EpicFishParticle.getParticle(armorStand.location.apply { this.y = fishLakeManager.surfaceYLevel }))
            else -> listOf()
        }
        particles.forEach { it.start() }
    }

    fun onTick() {
        if (caught) {
            //Because armor stand has no gravity, it will sink the moment it stops moving. We must maintain its position.

            armorStand.teleport(
                Location(
                    armorStand.world,
                    armorStand.x,
                    fishLakeManager.armorStandYLevel,
                    armorStand.z,
                    armorStand.yaw,
                    armorStand.pitch
                )
            )
            return
        }
        aliveTime++
        if (aliveTime > maxAliveTime) {
            remove()
            return
        }

        particles.forEach {
            it.originLocation = armorStand.location.apply { this.y = fishLakeManager.surfaceYLevel }
        }


        AIManager.onTick()
        //Update hitbox location to match armorstand's loc
        hitboxLocation.x = armorStand.location.x
        hitboxLocation.z = armorStand.location.z
    }

    /**
     * Removes the fish from its fish lake, and removes the armor stand that holds it.
     * @param withEffect Whether to spawn a particle effect
     */
    fun remove(withEffect: Boolean = false) {
        fishLakeManager.fishes.remove(this)
        armorStand.remove()
        particles?.forEach { it.stop() }

        if (withEffect) {
            val catchParticle =
                FishCatchParticle.getParticle(hitboxLocation.clone().apply { this.y = fishLakeManager.surfaceYLevel })
            catchParticle.start()
            object : BukkitRunnable() {
                override fun run() {
                    catchParticle.stop()
                }
            }.runTask(plugin)
        }
    }


    /**
     * @return Whether the provided location is colliding with the fish's hitbox.
     */
    fun checkHitboxCollision(location: Location): Boolean {
        val offsettedHitboxLocation = hitboxLocation.clone().add(variant.hitboxOffset) //todo: move this up maybe
        val cornerXNeg = offsettedHitboxLocation.x - variant.hitboxSize.x
        val cornerXPos = offsettedHitboxLocation.x + variant.hitboxSize.x
        val cornerYNeg = offsettedHitboxLocation.y - variant.hitboxSize.y
        val cornerYPos = offsettedHitboxLocation.y + variant.hitboxSize.y
        val cornerZNeg = offsettedHitboxLocation.z - variant.hitboxSize.z
        val cornerZPos = offsettedHitboxLocation.z + variant.hitboxSize.z
        return (location.x in cornerXNeg..cornerXPos &&
            location.y in cornerYNeg..cornerYPos &&
            location.z in cornerZNeg..cornerZPos)
    }
}