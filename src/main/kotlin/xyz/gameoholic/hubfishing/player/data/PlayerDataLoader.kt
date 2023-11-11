package xyz.gameoholic.hubfishing.player.data

import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.listeners.PlayerJoinListener
import xyz.gameoholic.hubfishing.util.LoggerUtil
import java.lang.RuntimeException
import java.util.UUID

object PlayerDataLoader {
    private val plugin: HubFishingPlugin by inject()

    /**
     * Attempts to load the player data from the database given a player's UUID, and creates it if it doesn't exist.
     * If successful, adds to the playerData map in HubFishingPlugin.
     */
    fun attemptLoadPlayerData(uuid: UUID): Result<PlayerData> {
        val playerDataResult = plugin.sqlManager.fetchPlayerData(uuid).onSuccess {
            if (it.xp == -1 ||
                it.playtime == -1 ||
                it.fishesCaught.size != plugin.config.fishVariants.variants.size ||
                it.fishesUncaught.size != plugin.config.fishVariants.variants.size
            ) {
                return Result.failure(RuntimeException("Player data loaded for $uuid is invalid ($it). Fish variants size: ${plugin.config.fishVariants.variants.size}"))
            }
            plugin.playerData[uuid] = it
            return Result.success(it)
        }
        return playerDataResult
    }

}