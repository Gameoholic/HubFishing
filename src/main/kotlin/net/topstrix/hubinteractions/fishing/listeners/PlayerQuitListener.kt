package net.topstrix.hubinteractions.fishing.listeners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.data.PlayerData
import net.topstrix.hubinteractions.fishing.displays.PlayerDisplayManager
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import net.topstrix.hubinteractions.fishing.util.LoggerUtil
import net.topstrix.hubinteractions.shared.coroutines.MinecraftDispatchers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable

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
                    LoggerUtil.debug("Couldn't upload player data for ${playerData.playerUUID} - timed out $ex")
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