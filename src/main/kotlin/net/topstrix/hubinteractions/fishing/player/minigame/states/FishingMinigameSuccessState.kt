package net.topstrix.hubinteractions.fishing.player.minigame.states

import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameManager
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameState
import net.topstrix.hubinteractions.fishing.player.minigame.ui.FishingMinigameSuccessRenderer
import net.topstrix.hubinteractions.fishing.player.minigame.ui.FishingMinigameUIRenderer
import org.bukkit.event.Listener

class FishingMinigameSuccessState(val minigameManager: FishingMinigameManager): FishingMinigameState, Listener {
    override var stateTicksPassed: Int = 0
    private val uiRenderer: FishingMinigameUIRenderer = FishingMinigameSuccessRenderer(this)

    override fun onEnable() {

    }

    override fun onTick() {
        stateTicksPassed++

        uiRenderer.render()
    }

    override fun onDisable() {
    }
}