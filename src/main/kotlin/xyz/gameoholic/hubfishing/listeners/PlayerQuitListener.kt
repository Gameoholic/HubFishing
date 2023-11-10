package xyz.gameoholic.hubfishing.listeners

import kotlinx.coroutines.*
import xyz.gameoholic.hubfishing.player.data.PlayerData
import xyz.gameoholic.hubfishing.util.LoggerUtil
import xyz.gameoholic.hubfishing.coroutines.MinecraftDispatchers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject

object PlayerQuitListener : Listener {
    private val plugin: HubFishingPlugin by inject()

    private val scope = CoroutineScope(SupervisorJob() + MinecraftDispatchers.Background)

    @EventHandler
    fun onPlayerQuitEvent(e: PlayerQuitEvent) {
        //Upload player data
        plugin.playerData.firstOrNull { it.playerUUID == e.player.uniqueId }?.let {
            plugin.playerData.remove(it)

            scope.launch {
                try {
                    withTimeout(plugin.config.sql.sqlQueryTimeout) {
                        if (it.uploadData())
                            LoggerUtil.debug("Successfully uploaded player data for player ${it.playerUUID}")
                        else
                            LoggerUtil.error("Couldn't upload player data for player ${it.playerUUID}")
                    }
                }
                catch (ex: TimeoutCancellationException) {
                    ex.printStackTrace()
                    LoggerUtil.error("Couldn't upload player data for ${e.player.uniqueId} - timed out $ex")
                }
            }
        }

        //Remove all player displays
        plugin.playerDisplayManagers[e.player.uniqueId]?.let {
            it.removeDisplays()
            plugin.playerDisplayManagers.remove(e.player.uniqueId)
        }
    }
}