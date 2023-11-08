package xyz.gameoholic.hubfishing.listeners

import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject

object PlayerInteractEntityListener: Listener {
    private val plugin: HubFishingPlugin by inject()

    @EventHandler
    fun onPlayerInteractEntityEvent(e: PlayerInteractEntityEvent) {
        val armorStand = e.rightClicked as? ArmorStand ?: return
        if (plugin.fishLakeManagers.any { it.fishes.any { fish -> fish.armorStand == armorStand } })
            e.isCancelled = true
    }
}