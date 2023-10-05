package net.topstrix.hubinteractions.fishing.fish

import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.lake.FishLakeManager
import net.topstrix.hubinteractions.fishing.util.LoggerUtil
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector


/**
 * Represents a fish currently in the lake.
 *
 * @param fishLakeManager: The lake where the fish will spawn
 * @param hitboxLocation: The location of the fish's hitbox without accounting for offset, also used for the X and Z values for the armor stand location.
 * @param variant: The fish's properties.
 * @param maxAliveTime: The max amount of ticks after which the fish will be removed.
 */
class Fish(
    private val fishLakeManager: FishLakeManager,
    private var hitboxLocation: Location,
    val variant: FishVariant,
    private val maxAliveTime: Int
) {
    private val armorStand: ArmorStand
    private var aliveTime = 0
    private lateinit var nextLocation: Location

    /**
     * Whether the fish was caught and stopped in place.
     * Settings this to true stops its movement and makes it become uncatchable,
     * and setting back to false resumes its movement and makes it catchable.
     */
    var caught = false

    init {
        val armorStandLoc = hitboxLocation.clone().apply { this.y = fishLakeManager.armorStandYLevel }
        LoggerUtil.debug("Spawning fish $variant at $armorStandLoc")
        armorStand = armorStandLoc.world.spawnEntity(armorStandLoc, EntityType.ARMOR_STAND) as ArmorStand
        val key = NamespacedKey(HubInteractions.plugin, "fishing-removable") //Mark entity for removal upon server start
        armorStand.persistentDataContainer.set(key, PersistentDataType.BOOLEAN, true)
        val itemStack = ItemStack(variant.material)
        val meta = itemStack.itemMeta
        meta.setCustomModelData(variant.modelData)
        itemStack.itemMeta = meta
        armorStand.isInvisible = true
        armorStand.isInvulnerable = true
        //Armor stand must have gravity, otherwise we can't give it velocity
        armorStand.equipment.setItem(EquipmentSlot.HEAD, itemStack)

        generateNextLocation()
        rotateArmorStandInDirection()
    }

    fun onTick() {
        if (caught) {
            //Because armor stand has no gravity, it will sink the moment it stops moving. We must maintain its position.
            armorStand.teleport(
                Location(armorStand.world, armorStand.x, fishLakeManager.armorStandYLevel, armorStand.z)
            )
            return
        }
        aliveTime++
        if (aliveTime > maxAliveTime) {
            remove()
            return
        }

        moveFish()
        //Update hitbox location to match armorstand's loc
        hitboxLocation.x = armorStand.location.x
        hitboxLocation.z = armorStand.location.z
    }

    /**
     * Removes the fish from its fish lake, and removes the armor stand that holds it.
     */
    fun remove() {
        fishLakeManager.fishes.remove(this)
        armorStand.remove()
    }

    /**
     * Generates a new location for the fish to move to if needed,
     * rotates the armor stand in the direction of movement path,
     * and gives it velocity in the direction of its rotation.
     */
    private fun moveFish() {
        val currentLocation = armorStand.location

        if (currentLocation.clone().distanceSquared(nextLocation) <= 0.2) {
            generateNextLocation()
            rotateArmorStandInDirection()
        }

        val direction = nextLocation.clone().subtract(currentLocation).toVector().normalize()
        val velocity = direction.multiply(variant.speed)
        armorStand.velocity = velocity
    }

    /**
     * Rotates the armor stand in the direction of its path.
     */
    private fun rotateArmorStandInDirection() {
        val currentLocation = armorStand.location

        var direction = nextLocation.clone().subtract(currentLocation).toVector().normalize()
        val xAngleRadians = direction.angle(Vector(0, 0, 1))
        var xAngleDegrees = Math.toDegrees(xAngleRadians.toDouble()).toFloat()

        //Because it calculates angle to (0, 0, 1), the angle is always between 0-180 and positive, it's mirrored. Therefore, we manually check if the vector's x component is positive, and negate the angle so it's pointed at the correct side.
        var yaw = xAngleDegrees
        if (direction.x > 0)
            yaw *= -1

        armorStand.teleport(
            Location(
                currentLocation.world,
                currentLocation.x,
                fishLakeManager.armorStandYLevel, //water effects Y level, we teleport to the wanted Y level
                currentLocation.z,
                yaw,
                0f
            )
        )
    }

    /**
     * Generates a new location for the fish to move to. Location will be
     * at least 1 block further away from its current one.
     */
    private fun generateNextLocation() {
        val currentLocation = armorStand.location
        do {
            val x = fishLakeManager.spawnCorner1.blockX +
                fishLakeManager.rnd.nextInt(fishLakeManager.spawnCorner2.blockX - fishLakeManager.spawnCorner1.blockX + 1)
            val z = fishLakeManager.spawnCorner1.blockZ +
                fishLakeManager.rnd.nextInt(fishLakeManager.spawnCorner2.blockZ - fishLakeManager.spawnCorner1.blockZ + 1)
            nextLocation = Location(currentLocation.world, x.toDouble(), currentLocation.y, z.toDouble())
        } while (currentLocation.clone().distanceSquared(nextLocation) <= 1.0)
    }

    /**
     * @return Whether the provided location is colliding with the fish's hitbox.
     */
    fun checkHitboxCollision(location: Location): Boolean {
        val offsettedHitboxLocation = hitboxLocation.clone().add(variant.hitboxOffset) //move this up maybe
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