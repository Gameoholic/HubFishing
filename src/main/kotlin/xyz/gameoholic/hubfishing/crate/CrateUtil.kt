package xyz.gameoholic.hubfishing.crate

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import xyz.gameoholic.hubfishing.fish.FishVariant
import xyz.gameoholic.hubfishing.util.FishingUtil
import org.bukkit.Bukkit
import java.lang.RuntimeException
import java.util.UUID
import kotlin.random.Random
object CrateUtil {

    val rnd = Random

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
            Bukkit.getPlayer(uuid)?.let {
                it.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                        PlaceholderAPI.setPlaceholders(it, FishingUtil.fishingConfig.crateCraftMessage),
                        Placeholder.component(
                            "crate", MiniMessage.miniMessage().deserialize(
                                PlaceholderAPI.setPlaceholders(it, crate.displayName)
                            )
                        )
                    )
                )
                it.playSound(FishingUtil.fishingConfig.crateCraftSound, Sound.Emitter.self())
            }
        } else {
            Bukkit.getPlayer(uuid)?.let {
                it.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                        PlaceholderAPI.setPlaceholders(it, FishingUtil.fishingConfig.shardReceiveMessage),
                        Placeholder.component(
                            "shard", MiniMessage.miniMessage().deserialize(
                                PlaceholderAPI.setPlaceholders(it, crate.displayName)
                            )
                        )
                    )
                )
                it.playSound(FishingUtil.fishingConfig.shardReceiveSound, Sound.Emitter.self())
            }
        }

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