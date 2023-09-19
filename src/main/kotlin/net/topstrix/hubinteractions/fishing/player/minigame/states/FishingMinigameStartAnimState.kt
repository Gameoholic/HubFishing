package net.topstrix.hubinteractions.fishing.player.minigame.states

import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.player.FishingPlayer
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameManager
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameState
import org.bukkit.Bukkit

class FishingMinigameStartAnimState(private val minigameManager: FishingMinigameManager): FishingMinigameState {
    override var stateTicksPassed = 0

    override fun onEnable() {
        minigameManager.textDisplay.remove()
    }

    override fun onTick() {
        stateTicksPassed++
    }

    override fun onDisable() {
    }

}