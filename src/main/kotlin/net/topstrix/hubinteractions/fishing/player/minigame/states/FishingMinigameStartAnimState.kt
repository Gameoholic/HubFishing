package net.topstrix.hubinteractions.fishing.player.minigame.states

import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.player.FishingPlayer
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameManager
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameState
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent

class FishingMinigameStartAnimState(private val minigameManager: FishingMinigameManager): FishingMinigameState,
    Listener {
    override var stateTicksPassed = 0

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, HubInteractions.plugin)

        minigameManager.textDisplay.remove()
    }

    override fun onTick() {
        stateTicksPassed++
    }

    override fun onDisable() {
        PlayerFishEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)
    }

    @EventHandler
    fun onPlayerFish(e: PlayerFishEvent) {
        if (e.player.uniqueId != minigameManager.fishingPlayer.uuid) return
        if (e.isCancelled) return

        e.isCancelled = true
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.player.uniqueId != minigameManager.fishingPlayer.uuid) return
        if (e.isCancelled) return

        e.isCancelled = true
    }

}