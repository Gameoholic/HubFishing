package xyz.gameoholic.hubfishing.lake

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import xyz.gameoholic.hubfishing.fish.Fish
import xyz.gameoholic.hubfishing.fish.FishRarity
import xyz.gameoholic.hubfishing.fish.FishVariant
import xyz.gameoholic.hubfishing.player.FishingPlayerState
import xyz.gameoholic.hubfishing.player.LakePlayer
import xyz.gameoholic.hubfishing.util.LoggerUtil
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.player.FishingPlayer
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random


/**
 * Represents an area where hub fishing can occur.
 *
 * @param spawnCorner1 West-north-bottom corner where fishes spawn.
 * @param spawnCorner2 South-east-top corner where fishes spawn.
 * @param corner1 West-north-bottom corner where the lake's region is.
 * @param corner2 South-east-top corner where the lake's region is.
 * @param armorStandYLevel The Y level where armor stands of fishes spawn.
 * @param fishAmountChances The chances (between 0 and 1), mapped to the amount of fishes that will spawn in a spawning cycle.
 * @param maxFishCount The max amount of fish that can exist in the lake at a time.
 */
class FishLakeManager(
    val spawnCorner1: Location,
    val spawnCorner2: Location,
    val corner1: Location,
    val corner2: Location,
    val armorStandYLevel: Double,
    private val fishAmountChances: HashMap<Int, Double>,
    private val maxFishCount: Int,
    val statsDisplayLocation: Location,
    val permissionRequiredToEnter: String,
    private val fishSpawningAlgorithmCurve: Double, //todo: doc all these
    rankBoostDisplayLocation: Location,
    val surfaceYLevel: Double,
) : Listener {
    private val plugin: HubFishingPlugin by inject()

    /** Players that are in the lake's region */
    val lakePlayers = mutableListOf<LakePlayer>()

    /** Players that are currently fishing */
    val fishingPlayers = mutableListOf<FishingPlayer>()

    /** All fishes currently in the lake */
    var fishes = mutableListOf<Fish>()

    /** The amount of fishes to be spawned, until a rare one can appear. */
    private var rareFishesQueueAmount: Double

    /** The amount of fishes to be spawned, until an epic one can appear. */
    private var epicFishesQueueAmount: Double

    /** The amount of fishes to be spawned, until a legendary one can appear. */
    private var legendaryFishesQueueAmount: Double

    /** The text display that shows whether this lake is boosted. */
    private val rankBoostDisplay: TextDisplay

    /** Whether this current lake's fish chances are boosted by someone with a rank. */
    private var isBoosted: Boolean =
        true //We set to true, so changing it to false in init() would set the text immediately
    private var queueDecreaseAmount = 1.0

    init {
        //Initialize queue amounts for the fish rarities for the first time
        rareFishesQueueAmount = getFishesQueueAmount(FishRarity.RARE).toDouble()
        epicFishesQueueAmount = getFishesQueueAmount(FishRarity.EPIC).toDouble()
        legendaryFishesQueueAmount = getFishesQueueAmount(FishRarity.LEGENDARY).toDouble()

        Bukkit.getPluginManager().registerEvents(this, plugin)

        rankBoostDisplay = rankBoostDisplayLocation.world
            .spawnEntity(rankBoostDisplayLocation, EntityType.TEXT_DISPLAY) as TextDisplay
        val key =
            NamespacedKey(plugin, "fishing-removable") //Mark entity, for removal upon server start
        rankBoostDisplay.persistentDataContainer.set(key, PersistentDataType.BOOLEAN, true)
        rankBoostDisplay.alignment = TextDisplay.TextAlignment.CENTER
        rankBoostDisplay.billboard = Display.Billboard.CENTER
        setIsBoosted(false)
    }

    /**
     * Adds a player to the lake, only to allPlayers.
     * Should be called when a player enters its premises.
     */
    fun addPlayer(uuid: UUID) {
        lakePlayers.add(LakePlayer(uuid))

        val item = ItemStack(Material.FISHING_ROD)
        val meta = item.itemMeta
        meta.isUnbreakable = true
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        item.itemMeta = meta

        Bukkit.getPlayer(uuid)?.let {
            it.inventory.addItem(item)
            if (it.hasPermission(plugin.config.rankBoostPermission)) {
                setIsBoosted(true, it.name)
            }
        }
    }

    /**
     * Updates the isBoosted value.
     * If set to true, the lake fish spawn chances will be boosted and the boosted player's
     * name will be displayed. Otherwise, it'll go back to normal.
     */
    private fun setIsBoosted(value: Boolean, boosterName: String? = null) {
        if (value == isBoosted) return //We only do stuff if the value for isBoosted has changed
        if (!value) {
            rankBoostDisplay.text(
                MiniMessage.miniMessage().deserialize(plugin.config.rankBoostDisplayNoneContent)
            )
            queueDecreaseAmount = 1.0
        } else {
            rankBoostDisplay.text(
                MiniMessage.miniMessage().deserialize(
                    plugin.config.rankBoostDisplayBoostedContent, Placeholder.component(
                        "player",
                        text(boosterName ?: "?")
                    )
                )
            )
            queueDecreaseAmount = plugin.config.rankBoostAmount
        }
        isBoosted = value
    }

    /**
     * Removes a player from the lake completely, both from allPlayers and fishingPlayers.
     * Should be called when player leaves its premises.
     */
    fun removePlayer(uuid: UUID) {
        fishingPlayers.firstOrNull { it.uuid == uuid }?.let {
            it.onRemove()
        }
        fishingPlayers.removeAll { it.uuid == uuid }
        lakePlayers.removeAll { it.uuid == uuid }

        Bukkit.getPlayer(uuid)?.let {
            it.inventory.remove(Material.FISHING_ROD)
        }

        //Check if there are still any boosters in the lake
        setIsBoosted(false)
        lakePlayers.mapNotNull { Bukkit.getPlayer(it.uuid) }.forEach {
            if (it.hasPermission(plugin.config.rankBoostPermission)) {
                setIsBoosted(true, it.name)
                return@forEach
            }
        }
    }

    /**
     * Removes a player only from fishingPlayers.
     * Should be called when a player finishes the minigame or cancels their rod.
     */
    fun removePlayerFromFishingPlayers(uuid: UUID) {
        fishingPlayers.firstOrNull { it.uuid == uuid }?.let {
            it.onRemove()
        }
        fishingPlayers.removeAll { it.uuid == uuid }
    }

    fun onTick() {
        fishes.forEach {  // fishes can be removed on onTick, so we convert to list
            it.onTick()
        }

        fishingPlayers.forEach { // fishing players can be removed, so we convert to list
            if (it.hook.isDead) {  // If hook is no longer alive for some reason, remove from fishing players
                removePlayerFromFishingPlayers(it.uuid)
                lakePlayers.first { lakePlayer -> lakePlayer.uuid == it.uuid }.minigameManager?.onRodDeath()
            } else
                it.onTick()
        }
    }

    fun onSecondPassed() {
        // Add/remove players from fish lake managers, handle logic
        Bukkit.getOnlinePlayers().forEach {
            // Remove player if no longer in this lake's area
            val playerInFishingArea = it.location.x > corner1.x && it.location.x < corner2.x &&
                it.location.y > corner1.y && it.location.y < corner2.y &&
                it.location.z > corner1.z && it.location.z < corner2.z
            if (lakePlayers.any { lakePlayer ->
                    lakePlayer.uuid == it.uniqueId
                } && !playerInFishingArea)
                removePlayer(it.uniqueId)

            // Add player if in lake's area
            else if (!lakePlayers.any { lakePlayer ->
                    lakePlayer.uuid == it.uniqueId
                } && playerInFishingArea && plugin.playerData.any { playerData ->
                    playerData.playerUUID == it.uniqueId
                } // Make sure player has their data loaded
            ) {
                if (it.hasPermission(permissionRequiredToEnter)) {
                    addPlayer(it.uniqueId)
                }
                // If player doesn't have permission to enter lake, launch them away
                else {
                    val rnd = java.util.Random()
                    val velocity = Vector(rnd.nextDouble(-2.0, 2.0), 2.0, rnd.nextDouble(-2.0, 2.0))
                    it.velocity = velocity
                }
            }
        }

        attemptFishSpawnCycle()
    }

    /**
     * Attempts to spawn fishes. There's a chance for the fishes to be spawned,
     * after which a random amount of them will be determined, as will
     * their rarity and variant.
     */
    fun attemptFishSpawnCycle() {
        if (!shouldSpawnFish() || fishes.size >= maxFishCount) return
        val amount = min(determineAmountOfFishToSpawn(), maxFishCount - fishes.size)
        LoggerUtil.debug("Spawning $amount fishes.")
        repeat(amount) {
            spawnFish()
        }
    }

    /**
     * Spawns a fish of a random rarity, variant and at a random location in the lake,
     * with a random alive time.
     * If parameters are provided, they'll be used instead.
     *
     * @param location Location for the fish to spawn at
     * @param fishRarity The rarity of the fish
     * @param fishVariant The variant of the fish
     * @param fishAliveTime The amount of ticks the fish will be alive for
     */
    fun spawnFish(
        location: Location = determineSpawnLocation(),
        fishRarity: FishRarity = determineFishRarity(),
        fishVariant: FishVariant = plugin.config.fishVariants.filter { it.rarity == fishRarity }
            .random(Random),
        fishAliveTime: Int = fishRarity.aliveTimeMin + Random.nextInt(fishRarity.aliveTimeMax)
    ) {
        // Custom spawn message & sound for legendary fish
        if (fishRarity == FishRarity.LEGENDARY) {
            lakePlayers.forEach { lakePlayer ->
                Bukkit.getPlayer(lakePlayer.uuid)?.let {
                    it.sendMessage(
                        MiniMessage.miniMessage().deserialize(
                            PlaceholderAPI.setPlaceholders(it, plugin.config.legendaryFishSpawnMessage)
                        )
                    )
                    it.playSound(plugin.config.legendaryFishSpawnSound, location.x, location.y, location.z)
                }
            }
        }
        fishes.add(
            Fish(
                this,
                location,
                fishVariant,
                fishAliveTime
            )
        )
    }

    /**
     * Determines whether any fishes should be spawned this cycle, using formula.
     * @return Whether fishes should be spawned.
     */
    private fun shouldSpawnFish(): Boolean {
        //See 1-e^{\left(-a\cdot x^{2}\right)} on Desmos
        val curve = fishSpawningAlgorithmCurve
        var playersAmount = fishingPlayers.size + 1 //We want fishes to spawn even if 0 players, so we give +1
        val chance = 1 - Math.E.pow(-curve * playersAmount.toDouble().pow(2.0))
        val rand = Random.nextDouble()
        return rand <= chance
    }

    /**
     * @return The amount of fish to spawn.
     * @throws RuntimeException If fish chances configured are invalid.
     */
    private fun determineAmountOfFishToSpawn(): Int {
        val rand = Random.nextDouble()

        /**
         * Fish chances are provided from 0.0 to 1.0, with the lower it is, the more rare it is.
         * We generate a random double from 0.0 to 1.0. The higher it is, the more rare the
         * result will be.
         * For every fish amount entry, we check if the random number is lower
         * than or equal to the chance. If not, we subtract it from the chance.
         * For example, for {1: 0.90, 2: 0.05, 3:0.05}, and for the random double: 0.92:
         * 0.92 <= 0.90: False
         * 0.92 - 0.90 = 0.02. 0.02 <= 0.05: True. Return 2
         */
        var updatedRand = rand
        for ((amount, chance) in fishAmountChances.iterator()) {
            if (updatedRand <= chance)
                return amount
            updatedRand -= chance
        }
        throw RuntimeException("Invalid fish amount chances provided.")
    }

    /**
     * @return A random location between spawnCorner1 and spawnCorner2.
     */
    private fun determineSpawnLocation(): Location {
        return Location(
            spawnCorner1.world,
            spawnCorner1.x + getSpawnLocationComponent(spawnCorner1.x, spawnCorner2.x),
            spawnCorner1.y + getSpawnLocationComponent(spawnCorner1.y, spawnCorner2.y),
            spawnCorner1.z + getSpawnLocationComponent(spawnCorner1.z, spawnCorner2.z)
        )
    }

    /**
     * @Return a random value between component1 and component2.
     */
    private fun getSpawnLocationComponent(component1: Double, component2: Double): Double {
        //Bound in Random#nextDouble must be positive.
        if (component2 - component1 > 0)
            return Random.nextDouble(component2 - component1)
        return 0.0
    }

    /**
     * Determines the fish rarity based on queue, and updates the queue amount
     * for every rarity.
     * @return The fish rarity of the fish to be spawned
     */
    private fun determineFishRarity(): FishRarity {
        //Decrease the queue amount for every rarity, every time we spawn any fish
        rareFishesQueueAmount -= queueDecreaseAmount
        epicFishesQueueAmount -= queueDecreaseAmount
        legendaryFishesQueueAmount -= queueDecreaseAmount

        LoggerUtil.debug("Current queue amounts: Rare: $rareFishesQueueAmount, Epic: $epicFishesQueueAmount, Leg: $legendaryFishesQueueAmount")
        return when {
            legendaryFishesQueueAmount <= 0 -> FishRarity.LEGENDARY
            epicFishesQueueAmount <= 0 -> FishRarity.EPIC
            rareFishesQueueAmount <= 0 -> FishRarity.RARE
            else -> FishRarity.COMMON
        }
    }

    /**
     * Randomly picks and updates the queue amounts for fishes of a certain rarity to spawn.
     * @param fishRarity The rarity of the fish, to update the queue for.
     * @return The amount of fishes to be spawned, until the fish of the chosen rarity can spawn.
     */
    private fun getFishesQueueAmount(fishRarity: FishRarity): Int {
        val amount = Random.nextInt(fishRarity.fishesRequiredToSpawnMin, fishRarity.fishesRequiredToSpawnMax + 1)
        LoggerUtil.debug("Updating fishes queue amount for $fishRarity - $amount")
        return amount
    }


    @EventHandler
    fun onPlayerFishEvent(e: PlayerFishEvent) {
        if (e.isCancelled) return

        val lakePlayer = lakePlayers.firstOrNull { it.uuid == e.player.uniqueId } ?: return


        //We don't care what happens in this event, if the player is in the middle of a fishing minigame
        if (fishingPlayers.firstOrNull
            { it.uuid == e.player.uniqueId && it.fishingState == FishingPlayerState.FISH_CAUGHT } != null
        ) return

        if (e.state == PlayerFishEvent.State.FISHING) {
            e.hook.waitTime = 10000000
            fishingPlayers.add(FishingPlayer(this, lakePlayer.uuid, e.hook, plugin.config.hookCooldown))
        }
        if (e.state == PlayerFishEvent.State.REEL_IN) {
            removePlayerFromFishingPlayers(e.player.uniqueId)
        }
    }

    @EventHandler
    fun onPlayerItemHeldEvent(e: InventoryClickEvent) {
        lakePlayers.firstOrNull { it.uuid == e.whoClicked.uniqueId } ?: return
        if (e.isCancelled) return

        if (e.click == ClickType.NUMBER_KEY) {
            // This covers for cases where you try to move the fishing rod from the hotbar to the upper inventory using number keys
            if (e.whoClicked.inventory.getItem(e.hotbarButton)?.type == Material.FISHING_ROD)
                e.isCancelled = true
        }
        if (e.currentItem?.type == Material.FISHING_ROD)
            e.isCancelled = true
    }

    @EventHandler
    fun onPlayerItemHeldEvent(e: PlayerSwapHandItemsEvent) {
        lakePlayers.firstOrNull { it.uuid == e.player.uniqueId } ?: return
        if (e.isCancelled) return
        if (e.mainHandItem?.type == Material.FISHING_ROD || e.offHandItem?.type == Material.FISHING_ROD)
            e.isCancelled = true
    }

    @EventHandler
    fun onPlayerItemHeldEvent(e: PlayerDropItemEvent) {
        lakePlayers.firstOrNull { it.uuid == e.player.uniqueId } ?: return
        if (e.isCancelled) return
        if (e.itemDrop.itemStack.type == Material.FISHING_ROD)
            e.isCancelled = true
    }

    @EventHandler
    fun onPlayerQuitEvent(e: PlayerQuitEvent) {
        // If player leaves server, remove from collections nad clean up managers
        val lakePlayer = lakePlayers.firstOrNull { it.uuid == e.player.uniqueId } ?: return
        removePlayer(e.player.uniqueId)
        lakePlayer.minigameManager?.onPlayerLogOff(e.player)
    }

}