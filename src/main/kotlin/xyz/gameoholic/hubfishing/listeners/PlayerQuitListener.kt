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
        val uuid = e.player.uniqueId
        // Upload player data
        plugin.playerData[uuid]?.let {
            plugin.playerData.remove(uuid)

            scope.launch {
                try {
                    withTimeout(plugin.config.sql.sqlQueryTimeout) {
                        it.uploadData(uuid).onFailure {
                            LoggerUtil.error("Couldn't upload player data for player $uuid. Cause: ${it.cause} Message: ${it.message}")
                            it.printStackTrace()
                        }.onSuccess {
                            LoggerUtil.debug("Successfully uploaded player data for player $uuid")
                        }
                    }
                }
                catch (ex: TimeoutCancellationException) {
                    LoggerUtil.error("Couldn't upload player data for ${e.player.uniqueId} - timed out $ex")
                    ex.printStackTrace()
                }
            }
        }

        // Remove all player displays
        plugin.playerDisplayManagers[e.player.uniqueId]?.let {
            it.removeDisplays()
            plugin.playerDisplayManagers.remove(e.player.uniqueId)
        }
    }
}