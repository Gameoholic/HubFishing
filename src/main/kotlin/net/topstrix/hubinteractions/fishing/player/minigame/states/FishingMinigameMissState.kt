package net.topstrix.hubinteractions.fishing.player.minigame.states

import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameManager
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameState
import net.topstrix.hubinteractions.fishing.player.minigame.ui.FishingMinigameMissUIRenderer
import net.topstrix.hubinteractions.fishing.player.minigame.ui.FishingMinigameUIRenderer
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent

class FishingMinigameMissState(
    val minigameManager: FishingMinigameManager
) : FishingMinigameState, Listener {
    override var stateTicksPassed = 0
    private val uiRenderer: FishingMinigameUIRenderer = FishingMinigameMissUIRenderer(this)

    val longRodStartingPosition = FishingUtil.fishingConfig.bigRodPosition - FishingUtil.fishingConfig.bigRodCharacterHeight + 4.0 //TODo: I'm not sure why it's 4.0.
    val longRodPosition = minigameManager.rodBoxPosition - FishingUtil.fishingConfig.rodBoxCharacterHeight / 2

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, HubInteractions.plugin)


    }

    override fun onTick() {
        stateTicksPassed++

        minigameManager.fishMovementManager.updateFishPosition()

        uiRenderer.render()
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