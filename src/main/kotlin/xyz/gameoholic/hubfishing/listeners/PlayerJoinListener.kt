package xyz.gameoholic.hubfishing.listeners

import kotlinx.coroutines.*
import xyz.gameoholic.hubfishing.player.data.PlayerData
import xyz.gameoholic.hubfishing.displays.PlayerDisplayManager
import xyz.gameoholic.hubfishing.util.LoggerUtil
import xyz.gameoholic.hubfishing.coroutines.MinecraftDispatchers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitRunnable
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.player.data.PlayerDataLoader

object PlayerJoinListener: Listener {
    private val scope = CoroutineScope(SupervisorJob() + MinecraftDispatchers.Background)

    private val plugin: HubFishingPlugin by inject()

    @EventHandler
    fun onPlayerJoinEvent(e: PlayerJoinEvent) {
        val uuid = e.player.uniqueId
        // Load player data
        LoggerUtil.debug("Loading player data for $uuid")

        scope.launch {
            try {
                withTimeout(plugin.config.sql.sqlQueryTimeout) {
                    PlayerDataLoader.attemptLoadPlayerData(uuid).onFailure {
                        LoggerUtil.error("Couldn't load player data for $uuid! Cause: ${it.cause} Message: ${it.message}")
                        it.printStackTrace()
                        return@withTimeout
                    }.onSuccess {
                        object: BukkitRunnable() { // Sync with main thread
                            override fun run() {
                                LoggerUtil.debug("Successfully loaded player data for $uuid")
                                //Spawn displays
                                plugin.playerDisplayManagers[e.player.uniqueId] = PlayerDisplayManager(e.player.uniqueId, it)
                                    .apply { spawnDisplays() }
                            }
                        }.runTask(plugin)
                    }
                }
            }
            catch (ex: TimeoutCancellationException) {
                ex.printStackTrace()
                LoggerUtil.debug("Couldn't load player data for $uuid - timed out $ex")
            }
        }
    }
}