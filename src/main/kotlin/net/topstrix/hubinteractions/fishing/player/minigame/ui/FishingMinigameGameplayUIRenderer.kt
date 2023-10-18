package net.topstrix.hubinteractions.fishing.player.minigame.ui

import net.kyori.adventure.text.Component
import net.topstrix.hubinteractions.fishing.player.minigame.states.FishingMinigameGameplayState
import net.topstrix.hubinteractions.fishing.util.FishingUtil


/**
 * Renders water, fish and the rodbox on the player's screen.
 */
class FishingMinigameGameplayUIRenderer(override val minigameState: FishingMinigameGameplayState): FishingMinigameUIRenderer() {

    private var clockAnimationFrame = 0
    private var clockAnimationDelay = 0 // When equal to animation speed, will increment the frame and reset this value to 0
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
        for (i in 0 until FishingUtil.fishingConfig.maxFishingRodUses) {
            val miniRodFrame = minigameState.minigameManager.miniFishingRodFrames[i]

            val miniRodCharacter = if (miniRodFrame != -1) // If rod is used and has an animation frame
                FishingUtil.fishingConfig.miniRodUsedCharacters[miniRodFrame]
            else
                FishingUtil.fishingConfig.miniRodCharacter

            renderCharacterSeparately(
                title, miniRodCharacter,
                FishingUtil.fishingConfig.miniRodsCharactersPosition + minFishingRodsOffset * i, FishingUtil.fishingConfig.miniRodCharacterHeight
            )
        }
        // BIG ROD
        renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[0], FishingUtil.fishingConfig.bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)

        // INFO BOX
        renderCharacterSeparately(title, FishingUtil.fishingConfig.infoBoxCharacter, FishingUtil.fishingConfig.infoBoxPosition, FishingUtil.fishingConfig.infoBoxCharacterHeight)

        // CLOCK
        if (minigameState.passedTimeRestriction) {
            renderCharacterSeparately(title, FishingUtil.fishingConfig.clockCharacters[clockAnimationFrame], FishingUtil.fishingConfig.clockPosition, FishingUtil.fishingConfig.clockCharacterHeight)

            clockAnimationDelay++
            if (clockAnimationDelay == FishingUtil.fishingConfig.clockAnimationSpeed) {
                clockAnimationFrame++
                if (clockAnimationFrame >= FishingUtil.fishingConfig.clockCharacters.size)
                    clockAnimationFrame = 0
                clockAnimationDelay = 0
            }
        }


        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}