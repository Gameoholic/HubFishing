package net.topstrix.hubinteractions.fishing.player.minigame.ui

import net.kyori.adventure.text.Component
import net.topstrix.hubinteractions.fishing.player.minigame.states.FishingMinigameFailureState
import net.topstrix.hubinteractions.fishing.util.FishingUtil


/**
 * Renders water, fish and the rodbox on the player's screen.
 */

class FishingMinigameFailureUIRenderer(override val minigameState: FishingMinigameFailureState) :
    FishingMinigameUIRenderer() {
    private var rodBreakAnimationFrame = 0
    private var rodBreakAnimationDelay = 0 // When equal to animation speed, will increment the frame and reset this value to 0

    private var rodLongBreakAnimationFrame = 0
    private var rodLongBreakAnimationDelay = 0

    private var rodLongEndBreakAnimationFrame = 0
    private var rodLongEndBreakAnimationDelay = 0

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
        renderCharacterSeparately(
            title, FishingUtil.fishingConfig.rodBoxCharacter, minigameState.minigameManager.rodBoxPosition,
            FishingUtil.fishingConfig.rodBoxCharacterHeight
        )

        // FISHING ROD
        renderCharacterSeparately(title, FishingUtil.fishingConfig.rodBreakCharacters[rodBreakAnimationFrame], FishingUtil.fishingConfig.bigRodPosition, FishingUtil.fishingConfig.rodBreakAnimationHeight)
        rodBreakAnimationDelay++
        if (rodBreakAnimationDelay == FishingUtil.fishingConfig.rodBreakAnimationSpeed && rodBreakAnimationFrame < FishingUtil.fishingConfig.rodBreakCharacters.size - 1) {
            rodBreakAnimationFrame++
            rodBreakAnimationDelay = 0
        }

        // LONG ROD EXTENSION
        val evenPixelAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition).toInt()
        val unevenPixelAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition)
        for (i in 0 until evenPixelAmount - longRodExtraWidth + 1) { //We don't animate the last X frames based on the longrod extra width. We add +1 so the last frame isn't skipped.
            renderCharacterSeparately(title, FishingUtil.fishingConfig.rodLongBreakCharacters[rodLongBreakAnimationFrame],
                minigameState.longRodStartingPosition - i,
                FishingUtil.fishingConfig.rodLongBreakAnimationHeight)
        }
        // Because the actual position may be a decimal, we render an extra long rod character to make it extend to the needed position perfectly
        if (unevenPixelAmount > evenPixelAmount) {
            renderCharacterSeparately(title, FishingUtil.fishingConfig.rodLongBreakCharacters[rodLongBreakAnimationFrame],
                minigameState.longRodPosition + longRodExtraWidth,
                FishingUtil.fishingConfig.rodLongBreakAnimationHeight)
        }
        // We render the end point of the rod line
        renderCharacterSeparately(title, FishingUtil.fishingConfig.rodLongEndBreakCharacters[rodLongEndBreakAnimationFrame],
            minigameState.longRodPosition + longRodExtraWidth + FishingUtil.fishingConfig.longRodEndCharacterOffset, // We offset the long end character because it's actually longer and isn't centered.
            FishingUtil.fishingConfig.rodLongEndBreakAnimationHeight)

        // Handle animation for long rod and rod end point breaking
        rodLongBreakAnimationDelay++
        if (rodLongBreakAnimationDelay == FishingUtil.fishingConfig.rodLongBreakAnimationSpeed && rodLongBreakAnimationFrame < FishingUtil.fishingConfig.rodLongBreakCharacters.size - 1) {
            rodLongBreakAnimationFrame++
            rodLongBreakAnimationDelay = 0
        }

        rodLongEndBreakAnimationDelay++
        if (rodLongEndBreakAnimationDelay == FishingUtil.fishingConfig.rodLongEndBreakAnimationSpeed && rodLongEndBreakAnimationFrame < FishingUtil.fishingConfig.rodLongEndBreakCharacters.size - 1) {
            rodLongEndBreakAnimationFrame++
            rodLongEndBreakAnimationDelay = 0
        }

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

        // INFO BOX
        renderCharacterSeparately(title, FishingUtil.fishingConfig.infoBoxCharacter, FishingUtil.fishingConfig.infoBoxPosition, FishingUtil.fishingConfig.infoBoxCharacterHeight)


        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}