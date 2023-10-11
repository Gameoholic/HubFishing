package net.topstrix.hubinteractions.fishing.crate

import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.fish.FishVariant
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.Bukkit
import java.lang.RuntimeException
import java.util.UUID

object CrateUtil {

    val rnd = kotlin.random.Random

    /**
     * Attempts to give a crate shard to the player.
     * @param uuid The uuid of the player
     * @param fishVariant The variant of the fish caught
     */
    fun attemptGiveShard(uuid: UUID, fishVariant: FishVariant) {
        if (!determineWhetherToGiveShard(fishVariant.rarity.crateShardChance)) return

        val crate = determineCrate()

        val playerData = FishingUtil.playerData.firstOrNull { it.playerUUID == uuid } ?: return
        playerData.increaseCrateShards(crate, 1)
        if (playerData.crateShards!![crate]!! >= crate.amountOfShardsToCraft) {
            playerData.resetCrateShards(crate)
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), crate.commandToCraft)
            Bukkit.broadcastMessage("spawning crate ${crate.displayName}")
        }

        Bukkit.broadcastMessage("giving crate shard ${crate.displayName}")
    }

    /**
     * Determines whether to give a shard to the player.
     * @param chance The chance of getting a shard from the fish
     * @return Whether to give a shard to the player
     */
    private fun determineWhetherToGiveShard(chance: Double): Boolean {
        val rand = rnd.nextDouble()
        return rand <= chance
    }

    /**
     * Determines the crate shard to give to the player.
     * @return A random crate.
     */
    private fun determineCrate(): Crate {
        val rand = rnd.nextDouble()
        var updatedRand = rand
        for (crate in FishingUtil.fishingConfig.crates.toSortedSet(compareByDescending { it.chance })) {
            if (updatedRand <= crate.chance)
                return crate
            updatedRand -= crate.chance
        }
        throw RuntimeException("Invalid crate chances provided.")
    }

}