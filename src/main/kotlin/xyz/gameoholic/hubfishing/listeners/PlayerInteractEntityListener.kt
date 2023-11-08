package xyz.gameoholic.hubfishing.listeners

import xyz.gameoholic.hubfishing.util.FishingUtil
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

object PlayerInteractEntityListener: Listener {
    @EventHandler
    fun onPlayerInteractEntityEvent(e: PlayerInteractEntityEvent) {
        val armorStand = e.rightClicked as? ArmorStand ?: return
        if (FishingUtil.fishLakeManagers.any { it.fishes.any { fish -> fish.armorStand == armorStand } })
            e.isCancelled = true
    }
}