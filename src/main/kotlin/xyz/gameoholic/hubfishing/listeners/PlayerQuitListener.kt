package xyz.gameoholic.hubfishing.listeners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import xyz.gameoholic.hubfishing.data.PlayerData
import xyz.gameoholic.hubfishing.util.FishingUtil
import xyz.gameoholic.hubfishing.util.LoggerUtil
import xyz.gameoholic.hubfishing.coroutines.MinecraftDispatchers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object PlayerQuitListener : Listener {
    private val scope = CoroutineScope(MinecraftDispatchers.Background)

    @EventHandler
    fun onPlayerQuitEvent(e: PlayerQuitEvent) {
        //Upload player data
        FishingUtil.playerData.firstOrNull { it.playerUUID == e.player.uniqueId }?.let {
            FishingUtil.playerData.remove(it)
            val playerData = PlayerData(e.player.uniqueId)

            scope.launch {
                try {
                    withTimeout(FishingUtil.fishingConfig.sqlQueryTimeout) {
                        it.uploadData()
                    }
                }
                catch (ex: TimeoutCancellationException) {
                    ex.printStackTrace()
                    LoggerUtil.error("Couldn't upload player data for ${playerData.playerUUID} - timed out $ex")
                }
            }
        }

        //Remove all player displays
        FishingUtil.playerDisplayManagers[e.player.uniqueId]?.let {
            it.removeDisplays()
            FishingUtil.playerDisplayManagers.remove(e.player.uniqueId)
        }
    }
}