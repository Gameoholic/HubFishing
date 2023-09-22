package net.topstrix.hubinteractions.fishing.player.minigame.ui

import net.kyori.adventure.text.Component
import net.topstrix.hubinteractions.fishing.player.minigame.states.FishingMinigameRodCastState
import net.topstrix.hubinteractions.fishing.util.FishingUtil


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
        // ROD ANIMATION
        when (minigameState.stateTicksPassed) {
            0 -> renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[0], bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)
            in 1..4 -> {
                renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[minigameState.stateTicksPassed], bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)
            }
            else -> renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[5], bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)
        }

        if (minigameState.extensionTicksPassed > 0) {
            val evenAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition).toInt()
            val unEvenAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition)
            for (i in 0 until evenAmount - longRodExtraWidth + 1) { //We don't animate the last X frames based on the longrod extra width. We add +1 so the last frame isn't skipped.
                renderCharacterSeparately(title, FishingUtil.fishingConfig.longRodCharacter,
                    minigameState.longRodStartingPosition - i,
                    FishingUtil.fishingConfig.longRodCharacterHeight)
            }
            //handle uneven pixels: //todo: doc this shit
            if (unEvenAmount > evenAmount) {
                renderCharacterSeparately(title, FishingUtil.fishingConfig.longRodCharacter,
                    minigameState.longRodPosition + longRodExtraWidth,
                    FishingUtil.fishingConfig.longRodCharacterHeight)
            }
        }


        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}