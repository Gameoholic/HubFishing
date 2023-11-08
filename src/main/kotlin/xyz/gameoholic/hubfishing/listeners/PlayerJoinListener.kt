package xyz.gameoholic.hubfishing.listeners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import xyz.gameoholic.hubfishing.data.PlayerData
import xyz.gameoholic.hubfishing.displays.PlayerDisplayManager
import xyz.gameoholic.hubfishing.util.FishingUtil
import xyz.gameoholic.hubfishing.util.LoggerUtil
import xyz.gameoholic.hubfishing.coroutines.MinecraftDispatchers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitRunnable
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject

object PlayerJoinListener: Listener {
    private val scope = CoroutineScope(MinecraftDispatchers.Background)

    private val plugin: HubFishingPlugin by inject()

    @EventHandler
    fun onPlayerJoinEvent(e: PlayerJoinEvent) {
        //Load player data
        LoggerUtil.debug("Loading player data for ${e.player.uniqueId}")
        val playerData = PlayerData(e.player.uniqueId)

        scope.launch {
            try {
                withTimeout(FishingUtil.fishingConfig.sqlQueryTimeout) {
                    if (playerData.fetchData()) {
                        object: BukkitRunnable() {
                            override fun run() {
                                FishingUtil.playerData.add(playerData)
                                LoggerUtil.debug("Successfully loaded player data for ${e.player.uniqueId}")
                                //Spawn displays
                                FishingUtil.playerDisplayManagers[e.player.uniqueId] = PlayerDisplayManager(e.player.uniqueId).apply { spawnDisplays() }
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