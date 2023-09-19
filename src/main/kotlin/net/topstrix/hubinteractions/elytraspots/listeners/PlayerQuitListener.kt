package net.topstrix.hubinteractions.elytraspots.listeners

import net.topstrix.hubinteractions.elytraspots.ElytraSpotsUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object PlayerQuitListener: Listener {
    @EventHandler
    fun onPlayerQuitEvent(e: PlayerQuitEvent) {
        ElytraSpotsUtil.playersWithElytra.remove(e.player)
    }
}