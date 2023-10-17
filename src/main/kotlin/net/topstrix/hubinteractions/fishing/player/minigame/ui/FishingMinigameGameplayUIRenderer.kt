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
        renderCharacter(title, FishingUtil.fishingConfig.waterCharacters[minigameState.minigameManager.waterAnimationFrame], false)
        // FISH
        renderCharacterSeparately(
            title, minigameState.minigameManager.caughtFish.variant.minigameCharacter,
            minigameState.minigameManager.fishMovementManager.fishPosition,
            minigameState.minigameManager.caughtFish.variant.minigameCharacterHeight
        )
        // ROD BOX
        renderCharacterSeparately(title, FishingUtil.fishingConfig.rodBoxCharacter, minigameState.minigameManager.rodBoxPosition, FishingUtil.fishingConfig.rodBoxCharacterHeight)
        // MINI RODS
        for (i in 0 until FishingUtil.fishingConfig.maxFishingRodUses) { //3,1
            if ((FishingUtil.fishingConfig.maxFishingRodUses - i) > minigameState.minigameManager.fishingRodUsesLeft)
                renderCharacterSeparately(title, FishingUtil.fishingConfig.miniRodUsedCharacter,
                    miniFishingRodsPosition + minFishingRodsOffset * i, FishingUtil.fishingConfig.miniRodCharacterHeight)
            else
                renderCharacterSeparately(title, FishingUtil.fishingConfig.miniRodCharacter,
                    miniFishingRodsPosition + minFishingRodsOffset * i, FishingUtil.fishingConfig.miniRodUsedCharacterHeight)
        }
        // BIG ROD
        renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[0], FishingUtil.fishingConfig.bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)

        // INFO BOX
        renderCharacterSeparately(title, FishingUtil.fishingConfig.infoBoxCharacter, FishingUtil.fishingConfig.infoBoxPosition, FishingUtil.fishingConfig.infoBoxCharacterHeight)


        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}