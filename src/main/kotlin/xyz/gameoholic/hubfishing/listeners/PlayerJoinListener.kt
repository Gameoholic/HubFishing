package xyz.gameoholic.hubfishing.listeners

import kotlinx.coroutines.*
import xyz.gameoholic.hubfishing.data.PlayerData
import xyz.gameoholic.hubfishing.displays.PlayerDisplayManager
import xyz.gameoholic.hubfishing.util.LoggerUtil
import xyz.gameoholic.hubfishing.coroutines.MinecraftDispatchers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitRunnable
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject

object PlayerJoinListener: Listener {
    private val scope = CoroutineScope(SupervisorJob() + MinecraftDispatchers.Background)

    private val plugin: HubFishingPlugin by inject()

    @EventHandler
    fun onPlayerJoinEvent(e: PlayerJoinEvent) {
        //Load player data
        LoggerUtil.debug("Loading player data for ${e.player.uniqueId}")
        val playerData = PlayerData(e.player.uniqueId)

        scope.launch {
            try {
                withTimeout(plugin.config.sql.sqlQueryTimeout) {
                    if (playerData.fetchData()) {
                        object: BukkitRunnable() {
                            override fun run() {
                                plugin.playerData.add(playerData)
                                LoggerUtil.debug("Successfully loaded player data for ${e.player.uniqueId}")
                                //Spawn displays
                                plugin.playerDisplayManagers[e.player.uniqueId] = PlayerDisplayManager(e.player.uniqueId).apply { spawnDisplays() }
                            }
                        }.runTask(plugin)
                    }
                    else {
                        LoggerUtil.error("Couldn't load player data for ${e.player.uniqueId}")
                    }
                }
            }
            catch (ex: TimeoutCancellationException) {
                ex.printStackTrace()
                LoggerUtil.debug("Couldn't load player data for ${playerData.playerUUID} - timed out $ex")
            }
        }
    }
}