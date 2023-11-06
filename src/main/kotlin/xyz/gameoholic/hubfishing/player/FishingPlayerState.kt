package xyz.gameoholic.hubfishing.player

/**
 * The state of the player, who's currently fishing.
 */
enum class FishingPlayerState {
    /**
     * The rod is currently on cooldown
     */
    ROD_WAITING,

    /**
     * The rod is ready to catch a fish, waiting for fish
     */
    ROD_READY,
    /**
     * Fish has been caught, minigame has started
     */
    FISH_CAUGHT
}
