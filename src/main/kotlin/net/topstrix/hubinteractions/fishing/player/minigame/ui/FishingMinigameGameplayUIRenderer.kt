package net.topstrix.hubinteractions.fishing.player.minigame.ui

import net.kyori.adventure.text.Component
import net.topstrix.hubinteractions.fishing.player.minigame.states.FishingMinigameGameplayState
import net.topstrix.hubinteractions.fishing.util.FishingUtil


/**
 * Renders water, fish and the rodbox on the player's screen.
 */
class FishingMinigameGameplayUIRenderer(override val minigameState: FishingMinigameGameplayState): FishingMinigameUIRenderer() {
    override fun render() {
        val title = Component.text()
        // WATER
        renderCharacters(title, FishingUtil.fishingConfig.waterCharacter, FishingUtil.fishingConfig.waterAmount)
        // FISH
        renderCharacterSeparately(title, FishingUtil.fishingConfig.fishCharacter, minigameState.minigameManager.fishMovementManager.fishPosition, FishingUtil.fishingConfig.fishCharacterHeight)
        // ROD BOX
        renderCharacterSeparately(title, FishingUtil.fishingConfig.rodBoxCharacter, minigameState.minigameManager.rodBoxPosition, FishingUtil.fishingConfig.rodBoxCharacterHeight)
        // FISHING RODS
        for (i in 0 until FishingUtil.fishingConfig.maxFishingRodUses) { //3,1
            if ((FishingUtil.fishingConfig.maxFishingRodUses - i) > minigameState.minigameManager.fishingRodUsesLeft)
                renderCharacterSeparately(title, FishingUtil.fishingConfig.miniRodUsedCharacter,
                    miniFishingRodsPosition + minFishingRodsOffset * i, FishingUtil.fishingConfig.miniRodCharacterHeight)
            else
                renderCharacterSeparately(title, FishingUtil.fishingConfig.miniRodCharacter,
                    miniFishingRodsPosition + minFishingRodsOffset * i, FishingUtil.fishingConfig.miniRodUsedCharacterHeight)
        }
        // BIG ROD
        renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[0], bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)

        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}