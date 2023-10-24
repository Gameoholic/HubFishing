package net.topstrix.hubinteractions.fishing.player

import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameManager
import java.util.UUID

/**
 * Represents a player who's in a lake, may or may not be fishing, or in a fishing minigame.
 */
data class LakePlayer(val uuid: UUID, var minigameManager: FishingMinigameManager? = null)
// We store minigameManager here and not in FishingPlayer, because player might've quit the fishing minigame by changing hotbar slot
// but their minigame still needs to be updated and displayed. So they do not have their rod out but they are in the game technically.
