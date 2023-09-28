package net.topstrix.hubinteractions.fishing.lake

import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.player.FishingPlayer
import net.topstrix.hubinteractions.fishing.fish.Fish
import net.topstrix.hubinteractions.fishing.fish.FishRarity
import net.topstrix.hubinteractions.fishing.fish.FishVariant
import net.topstrix.hubinteractions.fishing.player.FishingPlayerState
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import net.topstrix.hubinteractions.fishing.util.LoggerUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack
import java.lang.RuntimeException
import java.util.*
import kotlin.math.min
import kotlin.math.pow


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
    private val fishSpawningAlgorithmCurve: Double, //todo: doc
): Listener {

    /** Players that are in the lake's region */
    val allPlayers = mutableListOf<UUID>()
    /** Players that are currently fishing */
    val fishingPlayers = mutableListOf<FishingPlayer>()

    val rnd = kotlin.random.Random

    /** All fishes currently in the lake */
    var fishes = mutableListOf<Fish>()

    /** The amount of fishes to be spawned, until a rare one can appear. */
    private var rareFishesQueueAmount: Int
    /** The amount of fishes to be spawned, until an epic one can appear. */
    private var epicFishesQueueAmount: Int
    /** The amount of fishes to be spawned, until a legendary one can appear. */
    private var legendaryFishesQueueAmount: Int


    init {
        //Initialize queue amounts for the fish rarities for the first time
        rareFishesQueueAmount = getFishesQueueAmount(FishRarity.RARE)
        epicFishesQueueAmount = getFishesQueueAmount(FishRarity.EPIC)
        legendaryFishesQueueAmount = getFishesQueueAmount(FishRarity.LEGENDARY)

        Bukkit.getPluginManager().registerEvents(this, HubInteractions.plugin)
    }

    /**
     * Adds a player to the lake, only to allPlayers.
     * Should be called when a player enters its premises.
     */
    fun addPlayer(uuid: UUID) {
        allPlayers.add(uuid)
        val item = ItemStack(Material.FISHING_ROD)
        Bukkit.getPlayer(uuid)?.let {
            it.inventory.addItem(item)
        }
    }

    /**
     * Removes a player from the lake completely, both from allPlayers and fishingPlayers.
     * Should be called when player leaves its premises.
     */
    fun removePlayer(uuid: UUID) {
        fishingPlayers.removeAll { it.uuid == uuid }
        allPlayers.removeAll { it == uuid }

        Bukkit.getPlayer(uuid)?.let {
            it.inventory.remove(Material.FISHING_ROD)
        }
    }
    /**
     * Removes a player only from fishingPlayers.
     * Should be called when a player finishes the minigame or cancels their rod.
     */
    fun removePlayerFromFishingPlayers(uuid: UUID) {
        fishingPlayers.removeAll { it.uuid == uuid }
    }

    fun onTick() {
        fishes.toList().forEach {  //fishes can be removed on onTick, so we convert to list
            it.onTick()
        }

        fishingPlayers.toList().forEach { //fishing players can be removed, so we convert to list
            if (Bukkit.getPlayer(it.uuid) == null) //If player logged off, remove from collections
                removePlayer(it.uuid)
            else
                it.onTick()
        }
    }

    fun onSecondPassed() {
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
        for (i in 0 until amount) {
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
        fishVariant: FishVariant = FishingUtil.fishingConfig.fishVariants.filter { it.rarity == fishRarity }.random(rnd),
        fishAliveTime: Int = fishRarity.aliveTimeMin + rnd.nextInt(fishRarity.aliveTimeMax)
    ) {
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
        val rand = rnd.nextDouble()
        return rand <= chance
    }

    /**
     * @return The amount of fish to spawn.
     * @throws RuntimeException If fish chances configured are invalid.
     */
    private fun determineAmountOfFishToSpawn(): Int {
        val rand = rnd.nextDouble()

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
            return rnd.nextDouble(component2 - component1)
        return 0.0
    }

    /**
     * Determines the fish rarity based on queue, and updates the queue amount
     * for every rarity.
     * @return The fish rarity of the fish to be spawned
     */
    private fun determineFishRarity(): FishRarity {
        //Decrease the queue amount for every rarity, every time we spawn any fish
        rareFishesQueueAmount--
        epicFishesQueueAmount--
        legendaryFishesQueueAmount--

        if (legendaryFishesQueueAmount <= 0) {
            legendaryFishesQueueAmount = getFishesQueueAmount(FishRarity.LEGENDARY)
            return FishRarity.LEGENDARY
        }
        else if (epicFishesQueueAmount <= 0) {
            epicFishesQueueAmount = getFishesQueueAmount(FishRarity.EPIC)
            return FishRarity.EPIC
        }
        else if (rareFishesQueueAmount <= 0) {
            rareFishesQueueAmount = getFishesQueueAmount(FishRarity.RARE)
            return FishRarity.RARE
        }
        return FishRarity.COMMON
    }

    /**
     * Randomly picks and updates the queue amounts for fishes of a certain rarity to spawn.
     * @param fishRarity The rarity of the fish, to update the queue for.
     * @return The amount of fishes to be spawned, until the fish of the chosen rarity can spawn.
     */
    private fun getFishesQueueAmount(fishRarity: FishRarity): Int {
        val amount = rnd.nextInt(fishRarity.fishesRequiredToSpawnMin, fishRarity.fishesRequiredToSpawnMax + 1)
        LoggerUtil.debug("Updating fishes queue amount for $fishRarity - $amount")
        return amount
    }


    @EventHandler
    fun onPlayerFishEvent(e: PlayerFishEvent) {
        val playerUUID = this.allPlayers.first { it == e.player.uniqueId }

        //We don't care what happens in this event, if the player is in the middle of a fishing minigame
        if (fishingPlayers.firstOrNull
            { it.uuid == e.player.uniqueId && it.fishingState == FishingPlayerState.FISH_CAUGHT} != null) return

        if (e.state == PlayerFishEvent.State.FISHING) {
            e.hook.waitTime = 10000000
            fishingPlayers.add(FishingPlayer(this, playerUUID, e.hook, 40))
        }
        if (e.state == PlayerFishEvent.State.REEL_IN) {
            removePlayerFromFishingPlayers(e.player.uniqueId)
        }
    }
}