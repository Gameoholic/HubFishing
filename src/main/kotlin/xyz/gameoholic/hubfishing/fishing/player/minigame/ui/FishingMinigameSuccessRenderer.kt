package xyz.gameoholic.hubfishing.fishing.player.minigame.ui

import net.kyori.adventure.text.Component
import xyz.gameoholic.hubfishing.fishing.player.minigame.states.FishingMinigameSuccessState
import xyz.gameoholic.hubfishing.fishing.util.FishingUtil


/**
 * Renders water, fish and the rodbox on the player's screen.
 */
class FishingMinigameSuccessRenderer(override val minigameState: FishingMinigameSuccessState): FishingMinigameUIRenderer() {

    private var bucketAnimationFrame = 0
    private var bucketAnimationDelay = 0 // When equal to animation speed, will increment the frame and reset this value to 0
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
        renderCharacterSeparately(title, FishingUtil.fishingConfig.bucketCharacters[bucketAnimationFrame],
             fishPosition + fishHeight / 2.0 + FishingUtil.fishingConfig.bucketOffset, //We make the bucket centered on fish
            FishingUtil.fishingConfig.bucketCharacterHeight)

        if (bucketAnimationFrame < FishingUtil.fishingConfig.bucketCharacters.size - 1) {
            bucketAnimationDelay++
            if (bucketAnimationDelay == FishingUtil.fishingConfig.fishingMinigameSuccessAnimationSpeed) {
                bucketAnimationFrame++
                bucketAnimationDelay = 0
            }
        }

        // INFO BOX
        renderCharacterSeparately(title, FishingUtil.fishingConfig.infoBoxCharacter, FishingUtil.fishingConfig.infoBoxPosition, FishingUtil.fishingConfig.infoBoxCharacterHeight)

        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}