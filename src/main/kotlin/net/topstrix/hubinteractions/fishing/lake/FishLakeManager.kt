package net.topstrix.hubinteractions.fishing.lake

import net.kyori.adventure.text.Component
import net.topstrix.hubinteractions.fishing.player.FishingPlayer
import net.topstrix.hubinteractions.fishing.fish.Fish
import net.topstrix.hubinteractions.fishing.fish.FishRarity
import net.topstrix.hubinteractions.fishing.fish.FishVariant
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
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
    private val maxFishCount: Int
) {

    val allPlayers = mutableListOf<UUID>()
    val fishingPlayers = mutableListOf<FishingPlayer>()
    val rnd = Random()

    var fishes = mutableListOf<Fish>()


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

    fun attemptFishSpawnCycle() {
        if (!shouldSpawnFish() || fishes.size >= maxFishCount) return
        val amount = min(determineAmountOfFishToSpawn(), maxFishCount - fishes.size)
        for (i in 0 until amount) {
            val location = determineSpawnLocation()
            val fishVariant = FishVariant(
                Material.ARROW,
                28,
                0.05,
                FishRarity.COMMON,
                Component.text("Ocelot"),
                100,
                200,
                Vector(1.4, 2.0, 1.4),
                Vector(0.0, 1.0, 0.0)
            )
            fishes.add(
                Fish(
                    this,
                    location,
                    fishVariant,
                    fishVariant.maxAliveTimeMin + rnd.nextInt(fishVariant.maxAliveTimeMax)
                )
            )
        }
    }

    private fun shouldSpawnFish(): Boolean {
        val curve = 0.08 //0.04
        var playersAmount = fishingPlayers.size + 1 //We want fishes to spawn even if 0 players, so we give +1
        val chance = 1 - Math.E.pow(-curve * playersAmount.toDouble().pow(2.0))
        val rand = rnd.nextDouble()
        return rand <= chance
    }

    private fun determineAmountOfFishToSpawn(): Int {
        val rand = rnd.nextDouble()
        if (rand <= 0.9) return 1
        if (rand <= 0.95) return 2
        if (rand <= 0.975) return 3
        return 4
    }

    //Bound in Random#nextDouble must be positive.
    //Generates a random location between spawnCorner1 and spawnCorner2
    private fun determineSpawnLocation(): Location {
        return Location(
            spawnCorner1.world,
            spawnCorner1.x + getSpawnLocationComponent(spawnCorner1.x, spawnCorner2.x),
            spawnCorner1.y + getSpawnLocationComponent(spawnCorner1.y, spawnCorner2.y),
            spawnCorner1.z + getSpawnLocationComponent(spawnCorner1.z, spawnCorner2.z)
        )
    }

    private fun getSpawnLocationComponent(component1: Double, component2: Double): Double {
        if (component2 - component1 > 0)
            return rnd.nextDouble(component2 - component1)
        return 0.0
    }

}