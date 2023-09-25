package net.topstrix.hubinteractions.fishing.player.minigame.ui

import net.kyori.adventure.text.Component
import net.topstrix.hubinteractions.fishing.player.minigame.states.FishingMinigameRodCastState
import net.topstrix.hubinteractions.fishing.util.FishingUtil

/**
 * Renders everything the GameplayUIRenderer does, and also animates
 * the big fishing rod cast animation, and the extension
 * of the rod itself.
 */
class FishingMinigameRodCastUIRenderer(override val minigameState: FishingMinigameRodCastState): FishingMinigameUIRenderer() {
    override fun render() {
        val title = Component.text()
        // WATER
        renderCharacters(title, FishingUtil.fishingConfig.waterCharacter, FishingUtil.fishingConfig.waterAmount)
        // FISH
        renderCharacterSeparately(title, FishingUtil.fishingConfig.fishCharacter, minigameState.minigameManager.fishMovementManager.fishPosition, FishingUtil.fishingConfig.fishCharacterHeight)
        // ROD BOX
        renderCharacterSeparately(title, FishingUtil.fishingConfig.rodBoxCharacter, minigameState.minigameManager.rodBoxPosition, FishingUtil.fishingConfig.rodBoxCharacterHeight)
        // FISHING RODS
        for (i in 0 until FishingUtil.fishingConfig.maxFishingRodUses) {
            if ((FishingUtil.fishingConfig.maxFishingRodUses - i) > minigameState.minigameManager.fishingRodUsesLeft)
                renderCharacterSeparately(title, FishingUtil.fishingConfig.miniRodUsedCharacter,
                    miniFishingRodsPosition + minFishingRodsOffset * i, FishingUtil.fishingConfig.miniRodCharacterHeight)
            else
                renderCharacterSeparately(title, FishingUtil.fishingConfig.miniRodCharacter,
                    miniFishingRodsPosition + minFishingRodsOffset * i, FishingUtil.fishingConfig.miniRodUsedCharacterHeight)
        }
        // BIG ROD ANIMATION
        when (minigameState.stateTicksPassed) {
            0 -> renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[0], bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)
            in 1..4 -> {
                renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[minigameState.stateTicksPassed], bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)
            }
            else -> renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[5], bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)
        }

        //LONG ROD EXTENSION ANIMATION
        if (minigameState.extensionTicksPassed > 0) {
            val evenPixelAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition).toInt()
            val unevenPixelAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition)
            for (i in 0 until evenPixelAmount - longRodExtraWidth + 1) { //We don't animate the last X frames based on the longrod extra width. We add +1 so the last frame isn't skipped.
                renderCharacterSeparately(title, FishingUtil.fishingConfig.longRodCharacter,
                    minigameState.longRodStartingPosition - i,
                    FishingUtil.fishingConfig.longRodCharacterHeight)
            }
            //Because the actual position may be a decimal, we render an extra long rod character to make it extend to the needed position perfectly
            if (unevenPixelAmount > evenPixelAmount) {
                renderCharacterSeparately(title, FishingUtil.fishingConfig.longRodCharacter,
                    minigameState.longRodPosition + longRodExtraWidth,
                    FishingUtil.fishingConfig.longRodCharacterHeight)
            }
        }


        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}