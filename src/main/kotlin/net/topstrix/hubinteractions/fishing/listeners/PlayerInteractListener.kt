package net.topstrix.hubinteractions.fishing.listeners

import net.topstrix.hubinteractions.fishing.player.FishingPlayerState
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

object PlayerInteractListener: Listener {
    @EventHandler
    fun onPlayerFishEvent(e: PlayerInteractEvent) {
        if (!FishingUtil.fishLakeManagers.any { it.fishingPlayers.any {it.fishingState == FishingPlayerState.FISH_CAUGHT} }) return
        if (e.hand != EquipmentSlot.HAND) return
    }
}