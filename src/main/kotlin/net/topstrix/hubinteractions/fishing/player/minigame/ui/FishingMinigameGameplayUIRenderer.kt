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
        when (minigameState.rodBeingCastTicks) {
            0 -> renderCharacterSeparately(title, bigRodCharacters[0], bigRodPosition, bigRodCharacterHeight)
            in 1..4 -> {
                renderCharacterSeparately(title, bigRodCharacters[minigameState.rodBeingCastTicks], bigRodPosition, bigRodCharacterHeight)
            }
            else -> renderCharacterSeparately(title, bigRodCharacters[5], bigRodPosition, bigRodCharacterHeight)
        }

        if (minigameState.rodBeingCastTicks > 4) {
            val targetPos = minigameState.minigameManager.rodBoxPosition - rodBoxCharacterHeight / 2 //center of rod box
            val startingPos = bigRodPosition - bigRodCharacterHeight
            val speed = 3
            val fishingRodLongPartExtraWidth = 1
            val ticksPassed = minigameState.rodBeingCastTicks - 4 //todo
            for (i in 0 until ticksPassed) { //For every tick that has passed:
                for (j in 0 until speed) {  //We animate it X (speed) times
                    val currentPos = startingPos - i * speed - j + fishingRodLongPartExtraWidth
                    if (currentPos <= targetPos) break //todo: this is shit
                    renderCharacterSeparately(title, longRodCharacter,
                        currentPos,
                        longRodCharacterHeight)
                }
            }

        }

        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}