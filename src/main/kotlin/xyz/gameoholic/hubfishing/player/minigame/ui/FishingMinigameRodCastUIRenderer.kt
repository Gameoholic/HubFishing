package xyz.gameoholic.hubfishing.player.minigame.ui

import net.kyori.adventure.text.Component
import xyz.gameoholic.hubfishing.player.minigame.states.FishingMinigameRodCastState
import xyz.gameoholic.hubfishing.util.FishingUtil

/**
 * Renders everything the GameplayUIRenderer does, and also animates
 * the big fishing rod cast animation, and the extension
 * of the rod itself.
 */
class FishingMinigameRodCastUIRenderer(override val minigameState: FishingMinigameRodCastState): FishingMinigameUIRenderer() {
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
        // BIG ROD ANIMATION
        when (minigameState.stateTicksPassed) {
            in 0 until FishingUtil.fishingConfig.bigRodCharacters.size -> {
                renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[minigameState.stateTicksPassed], FishingUtil.fishingConfig.bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)
            }
            else -> renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[FishingUtil.fishingConfig.bigRodCharacters.size - 1], FishingUtil.fishingConfig.bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)
        }

        // LONG ROD EXTENSION ANIMATION
        if (minigameState.extensionTicksPassed > 0) {
            val evenPixelAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition).toInt()
            val unevenPixelAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition)
            for (i in 0 until evenPixelAmount - longRodExtraWidth + 1) { //We don't animate the last X frames based on the longrod extra width. We add +1 so the last frame isn't skipped.
                    renderCharacterSeparately(title, FishingUtil.fishingConfig.longRodCharacter,
                        minigameState.longRodStartingPosition - i,
                        FishingUtil.fishingConfig.longRodCharacterHeight)
            }
            // Because the actual position may be a decimal, we render an extra long rod character to make it extend to the needed position perfectly
            if (unevenPixelAmount > evenPixelAmount) {
                renderCharacterSeparately(title, FishingUtil.fishingConfig.longRodCharacter,
                    minigameState.longRodPosition + longRodExtraWidth,
                    FishingUtil.fishingConfig.longRodCharacterHeight)
            }
            // We render the end point of the rod line
            renderCharacterSeparately(title, FishingUtil.fishingConfig.longRodEndCharacter,
                minigameState.longRodPosition + longRodExtraWidth + FishingUtil.fishingConfig.longRodEndCharacterOffset, // We offset the long end character because it's actually longer and isn't centered.
                FishingUtil.fishingConfig.longRodEndCharacterHeight)
        }

        // INFO BOX
        renderCharacterSeparately(title, FishingUtil.fishingConfig.infoBoxCharacter, FishingUtil.fishingConfig.infoBoxPosition, FishingUtil.fishingConfig.infoBoxCharacterHeight)


        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}