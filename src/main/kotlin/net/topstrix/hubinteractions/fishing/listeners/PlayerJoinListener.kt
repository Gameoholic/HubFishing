package net.topstrix.hubinteractions.fishing.listeners

import kotlinx.coroutines.*
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.data.PlayerData
import net.topstrix.hubinteractions.fishing.displays.PlayerDisplayManager
import net.topstrix.hubinteractions.fishing.player.FishingPlayerState
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import net.topstrix.hubinteractions.fishing.util.LoggerUtil
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.EquipmentSlot

object PlayerJoinListener: Listener {
    @EventHandler
    fun onPlayerJoinEvent(e: PlayerJoinEvent) = runBlocking {
        //Load player data

        LoggerUtil.debug("Loading player data for ${e.player.uniqueId}")
        val playerData = PlayerData(e.player.uniqueId)

        launch {
            try {
                withTimeout(FishingUtil.fishingConfig.sqlQueryTimeout) {
                    if (playerData.fetchData()) {
                        FishingUtil.playerData.add(playerData)
                        LoggerUtil.debug("Successfully loaded player data for ${e.player.uniqueId}")
                        //Spawn displays
                        FishingUtil.playerDisplayManagers[e.player.uniqueId] = PlayerDisplayManager(e.player.uniqueId).apply { spawnDisplays() }
                    }
                    else {
                        LoggerUtil.debug("Couldn't load player data for ${e.player.uniqueId}")
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