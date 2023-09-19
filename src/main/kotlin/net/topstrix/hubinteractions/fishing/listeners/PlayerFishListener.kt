package net.topstrix.hubinteractions.fishing.listeners

import net.topstrix.hubinteractions.fishing.player.FishingPlayer
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

object PlayerFishListener: Listener {
    @EventHandler
    fun onPlayerFishEvent(e: PlayerFishEvent) {
        val fishLakeManager = FishingUtil.fishLakeManagers
            .firstOrNull { it.allPlayers.any { uuid -> uuid == e.player.uniqueId } } ?: return
        val playerUUID = fishLakeManager.allPlayers
            .first { it == e.player.uniqueId }

        if (e.state == PlayerFishEvent.State.FISHING) {  //todo: custom fish reel event
            e.hook.waitTime = 10000000
            fishLakeManager.fishingPlayers.add(FishingPlayer(fishLakeManager, playerUUID, e.hook, 40))
        }
        if (e.state == PlayerFishEvent.State.REEL_IN) {
            fishLakeManager.fishingPlayers.removeAll { it.uuid == playerUUID }
        }

    }
}