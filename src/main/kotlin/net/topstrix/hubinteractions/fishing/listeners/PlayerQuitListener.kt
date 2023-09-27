package net.topstrix.hubinteractions.fishing.listeners

import net.topstrix.hubinteractions.fishing.data.PlayerData
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerQuitListener : Listener {
    @EventHandler
    fun onPlayerQuitEvent(e: PlayerQuitEvent) {
        //Upload player data
        FishingUtil.playerData.firstOrNull { it.playerUUID == e.player.uniqueId }?.let {
            FishingUtil.playerData.remove(it)
            it.uploadData()
        }

        //Remove all player displays
        FishingUtil.playerDisplayManagers[e.player.uniqueId]?.let {
            it.removeDisplays()
            FishingUtil.playerDisplayManagers.remove(e.player.uniqueId)
        }
    }
}