package net.topstrix.hubinteractions.fishing.player.minigame.ui

import net.kyori.adventure.text.Component
import net.topstrix.hubinteractions.fishing.player.minigame.states.FishingMinigameSuccessState
import net.topstrix.hubinteractions.fishing.util.FishingUtil


/**
 * Renders water, fish and the rodbox on the player's screen.
 */
class FishingMinigameSuccessRenderer(override val minigameState: FishingMinigameSuccessState): FishingMinigameUIRenderer() {

    var a = 0
    var m = 0
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

        // BIG ROD - display last frame
        renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[FishingUtil.fishingConfig.bigRodCharacters.size - 1], FishingUtil.fishingConfig.bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)

        // LONG ROD EXTENSION ANIMATION
        val longRodStartingPosition = minigameState.longRodStartingPosition
        val longRodPosition = minigameState.longRodPosition
        val evenPixelAmount = (longRodStartingPosition - longRodPosition).toInt()
        val unevenPixelAmount = (longRodStartingPosition - longRodPosition)
        for (i in 0 until evenPixelAmount - longRodExtraWidth + 1) { //We don't animate the last X frames based on the longrod extra width. We add +1 so the last frame isn't skipped.
            renderCharacterSeparately(title, FishingUtil.fishingConfig.longRodCharacter,
                longRodStartingPosition - i,
                FishingUtil.fishingConfig.longRodCharacterHeight)
        }
        // Because the actual position may be a decimal, we render an extra long rod character to make it extend to the needed position perfectly
        if (unevenPixelAmount > evenPixelAmount) {
            renderCharacterSeparately(title, FishingUtil.fishingConfig.longRodCharacter,
                longRodPosition + longRodExtraWidth,
                FishingUtil.fishingConfig.longRodCharacterHeight)
        }
        // We render the end point of the rod line
        renderCharacterSeparately(title, FishingUtil.fishingConfig.longRodEndCharacter,
            longRodPosition + longRodExtraWidth + FishingUtil.fishingConfig.longRodEndCharacterOffset, // We offset the long end character because it's actually longer and isn't centered.
            FishingUtil.fishingConfig.longRodEndCharacterHeight)


        // BUCKET ANIM
        val fishPosition = minigameState.minigameManager.fishMovementManager.fishPosition
        val fishHeight = minigameState.minigameManager.caughtFish.variant.minigameCharacterHeight
        renderCharacterSeparately(title, FishingUtil.fishingConfig.bucketCharacters[a],
             fishPosition + fishHeight / 2.0, //We make the bucket centered on fish
            FishingUtil.fishingConfig.bucketCharacterHeight)

        if (a < FishingUtil.fishingConfig.bucketCharacters.size - 1) {
            m++
            if (m == FishingUtil.fishingConfig.fishingMinigameSuccessAnimationSpeed) {
                a++
                m = 0
            }
        }

        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}