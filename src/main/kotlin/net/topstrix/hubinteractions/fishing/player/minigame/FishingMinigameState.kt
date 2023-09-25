package net.topstrix.hubinteractions.fishing.player.minigame


/**
 * Represents a fishing minigame state.
 */
interface FishingMinigameState {
    var stateTicksPassed: Int
    fun onEnable()
    fun onTick()
    fun onDisable()
}