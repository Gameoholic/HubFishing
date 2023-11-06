package xyz.gameoholic.hubfishing.fishing.player.minigame


/**
 * Represents a fishing minigame state.
 */
interface FishingMinigameState {
    var stateTicksPassed: Int
    fun onEnable()
    fun onTick()
    fun onDisable()
}