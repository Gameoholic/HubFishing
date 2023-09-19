package net.topstrix.hubinteractions.fishing.player.minigame

import net.topstrix.hubinteractions.fishing.player.FishingPlayer

interface FishingMinigameState {
    var stateTicksPassed: Int
    fun onEnable()
    fun onTick()
    fun onDisable()
}