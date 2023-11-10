package xyz.gameoholic.hubfishing.player.data

import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.util.LevelUtil
import xyz.gameoholic.hubfishing.util.LoggerUtil
import java.util.UUID

object PlayerDataLoader {
    private val plugin: HubFishingPlugin by inject()

    /**
     * Attempts to load the player data from the database given a player's UUID.
     * @return Null if player data failed to load, otherwise, returns the data.
     */
    fun attemptLoadPlayerData(uuid: UUID): PlayerData? {
        val playerData = plugin.sqlManager.fetchPlayerData(uuid) ?: return null

        if (playerData.xp == -1 ||
            playerData.playtime == -1 ||
            playerData.fishesCaught.size != plugin.config.fishVariants.variants.size ||
            playerData.fishesUncaught.size != plugin.config.fishVariants.variants.size) {
            LoggerUtil.error("Player data for $uuid is invalid ($playerData)")
            return null
        }
        return playerData
    }

}