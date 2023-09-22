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
        renderCharacterSeparately(title, FishingUtil.fishingConfig.rodBoxCharacter, minigameState.minigameManager.rodBoxPosition, FishingUtil.fishingConfig.rodBoxCharacterHeight)
        // FISHING RODS
        for (i in 0 until FishingUtil.fishingConfig.maxFishingRodUses) { //3,1
            if ((FishingUtil.fishingConfig.maxFishingRodUses - i) > minigameState.minigameManager.fishingRodUsesLeft)
                renderCharacterSeparately(title, FishingUtil.fishingConfig.miniRodUsedCharacter,
                    miniFishingRodsPosition + minFishingRodsOffset * i, FishingUtil.fishingConfig.miniRodCharacterHeight)
            else
                renderCharacterSeparately(title, FishingUtil.fishingConfig.miniRodCharacter,
                    miniFishingRodsPosition + minFishingRodsOffset * i, FishingUtil.fishingConfig.miniRodUsedCharacterHeight)
        }
        // ROD ANIMATION
        when (minigameState.rodBeingCastTicks) {
            0 -> renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[0], bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)
            in 1..4 -> {
                renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[minigameState.rodBeingCastTicks], bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)
            }
            else -> renderCharacterSeparately(title, FishingUtil.fishingConfig.bigRodCharacters[5], bigRodPosition, FishingUtil.fishingConfig.bigRodCharacterHeight)
        }

        if (minigameState.rodBeingCastTicks > 4) {
            val targetPos = minigameState.minigameManager.rodBoxPosition - FishingUtil.fishingConfig.rodBoxCharacterHeight / 2 //center of rod box
            val startingPos = bigRodPosition - FishingUtil.fishingConfig.bigRodCharacterHeight
            val speed = 3
            val fishingRodLongPartExtraWidth = 1
            val ticksPassed = minigameState.rodBeingCastTicks - 4 //todo
            for (i in 0 until ticksPassed) { //For every tick that has passed:
                for (j in 0 until speed) {  //We animate it X (speed) times
                    val currentPos = startingPos - i * speed - j + fishingRodLongPartExtraWidth
                    if (currentPos <= targetPos) break //todo: this is shit
                    renderCharacterSeparately(title, FishingUtil.fishingConfig.longRodCharacter,
                        currentPos,
                        FishingUtil.fishingConfig.longRodCharacterHeight)
                }
            }

        }

        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}