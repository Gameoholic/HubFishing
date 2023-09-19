package net.topstrix.hubinteractions.fishing.player.minigame.states

import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameManager
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameState
import net.topstrix.hubinteractions.fishing.player.minigame.ui.FishingMinigameFailureRenderer
import net.topstrix.hubinteractions.fishing.player.minigame.ui.FishingMinigameSuccessRenderer
import net.topstrix.hubinteractions.fishing.player.minigame.ui.FishingMinigameUIRenderer

class FishingMinigameFailureState(val minigameManager: FishingMinigameManager): FishingMinigameState {
    override var stateTicksPassed = 0
    private val uiRenderer: FishingMinigameUIRenderer = FishingMinigameFailureRenderer(this)

    override fun onEnable() {
    }

    override fun onTick() {
        stateTicksPassed++

        uiRenderer.render()
    }

    override fun onDisable() {
    }
}