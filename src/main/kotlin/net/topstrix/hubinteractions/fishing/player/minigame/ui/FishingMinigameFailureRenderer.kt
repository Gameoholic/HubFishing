package net.topstrix.hubinteractions.fishing.player.minigame.ui

import net.kyori.adventure.text.Component
import net.topstrix.hubinteractions.fishing.player.minigame.states.FishingMinigameFailureState
import net.topstrix.hubinteractions.fishing.util.FishingUtil


/**
 * Renders water, fish and the rodbox on the player's screen.
 */
class FishingMinigameFailureRenderer(override val minigameState: FishingMinigameFailureState): FishingMinigameUIRenderer() {
    override fun render() {
        val title = Component.text()
        // WATER
        renderCharacters(title, FishingUtil.fishingConfig.waterCharacter, FishingUtil.fishingConfig.waterAmount)
        // FISH
        renderCharacterSeparately(title, FishingUtil.fishingConfig.fishCharacter, minigameState.minigameManager.fishMovementManager.fishPosition, FishingUtil.fishingConfig.fishCharacterHeight)
        // ROD BOX
        renderCharacterSeparately(title, rodBoxCharacter, minigameState.minigameManager.rodBoxPosition, rodBoxCharacterHeight)
        // FISHING ROD
        renderCharacterSeparately(title, 'F', 80.0, 6)
        // FISHING RODS
        for (i in 0 until minigameState.minigameManager.maxFishingRodUses) { //3,1
            if ((minigameState.minigameManager.maxFishingRodUses - i) > minigameState.minigameManager.fishingRodUsesLeft)
                renderCharacterSeparately(title, miniRodUsedCharacter,
                    miniFishingRodsPosition + minFishingRodsOffset * i, miniRodCharacterHeight)
            else
                renderCharacterSeparately(title, miniRodCharacter,
                    miniFishingRodsPosition + minFishingRodsOffset * i, miniRodUsedCharacterHeight)
        }

        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}