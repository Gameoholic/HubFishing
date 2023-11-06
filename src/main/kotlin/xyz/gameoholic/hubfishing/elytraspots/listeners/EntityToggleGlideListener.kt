package xyz.gameoholic.hubfishing.elytraspots.listeners

import xyz.gameoholic.hubfishing.elytraspots.ElytraSpotsUtil
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityToggleGlideEvent

object EntityToggleGlideListener: Listener {
    @EventHandler
    fun onEntityToggleGlideEvent(e: EntityToggleGlideEvent) {
        val player = e.entity as? Player ?: return
        if (e.isGliding || !ElytraSpotsUtil.playersWithElytra.contains(player)) return

        ElytraSpotsUtil.deactivateElytra(player)
    }

}