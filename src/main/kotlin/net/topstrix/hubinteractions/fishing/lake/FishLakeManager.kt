package net.topstrix.hubinteractions.fishing.lake

import net.topstrix.hubinteractions.fishing.player.FishingPlayer
import net.topstrix.hubinteractions.fishing.fish.Fish
import net.topstrix.hubinteractions.fishing.fish.FishRarity
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import net.topstrix.hubinteractions.fishing.util.LoggerUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.lang.RuntimeException
import java.util.*
import kotlin.math.min
import kotlin.math.pow


/**
 * spawnCorner1: west-north-bottom corner
 * spawnCorner2: south-east-top corner
 * corner1: west-north-bottom corner
 * corner2: south-east-top corner
 */
class FishLakeManager(
    val spawnCorner1: Location,
    val spawnCorner2: Location,
    val corner1: Location,
    val corner2: Location,
    val armorStandYLevel: Double,
    private val fishAmountChances: HashMap<Int, Double>,
    private val maxFishCount: Int
) {

    val allPlayers = mutableListOf<UUID>()
    val fishingPlayers = mutableListOf<FishingPlayer>()
    val rnd = kotlin.random.Random

    var fishes = mutableListOf<Fish>()

    /** The amount of fishes to be spawned, until a rare one can appear. */
    private var rareFishesQueueAmount: Int
    /** The amount of fishes to be spawned, until an epic one can appear. */
    private var epicFishesQueueAmount: Int
    /** The amount of fishes to be spawned, until a legendary one can appear. */
    private var legendaryFishesQueueAmount: Int


    init {
        rareFishesQueueAmount = getFishesQueueAmount(FishRarity.RARE)
        epicFishesQueueAmount = getFishesQueueAmount(FishRarity.EPIC)
        legendaryFishesQueueAmount = getFishesQueueAmount(FishRarity.LEGENDARY)
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

    fun onTick() {
        fishes.toList().forEach {  //fishes can be removed on onTick, so we convert to list
            it.onTick()
        }

        fishingPlayers.forEach {
            it.onTick()
        }
    }

    fun onSecondPassed() {
        attemptFishSpawnCycle()
    }

    /**
     * Attempts to spawn fishes. There's a chance for the fishes to be spawned,
     * after which a random number for them will be determined, as will
     * their rarity and variant.
     */
    fun attemptFishSpawnCycle() {
        if (!shouldSpawnFish() || fishes.size >= maxFishCount) return
        val amount = min(determineAmountOfFishToSpawn(), maxFishCount - fishes.size)
        LoggerUtil.debug("Spawning $amount fishes.")
        for (i in 0 until amount) {
            val location = determineSpawnLocation()
            val fishRarity = determineFishRarity()
            val fishVariant = FishingUtil.fishingConfig.fishVariants.filter { it.rarity == fishRarity }.random(rnd)
            fishes.add(
                Fish(
                    this,
                    location,
                    fishVariant,
                    fishVariant.aliveTimeMin + rnd.nextInt(fishVariant.aliveTimeMax)
                )
            )
        }
    }

    /**
     * Determines whether any fishes should be spawned this cycle, using formula.
     * @return Whether fishes should be spawned.
     */
    private fun shouldSpawnFish(): Boolean {
        //See 1-e^{\left(-a\cdot x^{2}\right)} on Desmos
        val curve = 0.08
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
     * @param fishRarity
     * @return The amount of fishes to be spawned, until the fish of the chosen rarity can spawn.
     */
    private fun getFishesQueueAmount(fishRarity: FishRarity): Int {
        val amount = rnd.nextInt(fishRarity.fishesRequiredToSpawnMin, fishRarity.fishesRequiredToSpawnMax + 1)
        LoggerUtil.debug("Updating fishes queue amount for $fishRarity - $amount")
        return amount
    }
}