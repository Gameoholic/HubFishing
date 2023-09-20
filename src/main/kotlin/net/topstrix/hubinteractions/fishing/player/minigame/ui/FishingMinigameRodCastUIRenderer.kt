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
        renderCharacterSeparately(title, rodBoxCharacter, minigameState.minigameManager.rodBoxPosition, rodBoxCharacterHeight)
        // FISHING RODS
        for (i in 0 until minigameState.minigameManager.maxFishingRodUses) { //3,1
            if ((minigameState.minigameManager.maxFishingRodUses - i) > minigameState.minigameManager.fishingRodUsesLeft)
                renderCharacterSeparately(title, miniRodUsedCharacter,
                    miniFishingRodsPosition + minFishingRodsOffset * i, miniRodCharacterHeight)
            else
                renderCharacterSeparately(title, miniRodCharacter,
                    miniFishingRodsPosition + minFishingRodsOffset * i, miniRodUsedCharacterHeight)
        }
        // ROD ANIMATION
        when (minigameState.stateTicksPassed) {
            0 -> renderCharacterSeparately(title, bigRodCharacters[0], bigRodPosition, bigRodCharacterHeight)
            in 1..4 -> {
                renderCharacterSeparately(title, bigRodCharacters[minigameState.stateTicksPassed], bigRodPosition, bigRodCharacterHeight)
            }
            else -> renderCharacterSeparately(title, bigRodCharacters[5], bigRodPosition, bigRodCharacterHeight)
        }

        if (minigameState.extensionTicksPassed > 0) {
            val evenAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition).toInt()
            val unEvenAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition)
            for (i in 0 until evenAmount - longRodExtraWidth + 1) { //We don't animate the last X frames based on the longrod extra width. We add +1 so the last frame isn't skipped.
                renderCharacterSeparately(title, longRodCharacter,
                    minigameState.longRodStartingPosition - i,
                    longRodCharacterHeight)
            }
            //handle uneven pixels: //todo: doc this shit
            if (unEvenAmount > evenAmount) {
                renderCharacterSeparately(title, longRodCharacter,
                    minigameState.longRodPosition + longRodExtraWidth,
                    longRodCharacterHeight)
            }
        }


        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}