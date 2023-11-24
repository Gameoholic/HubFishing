package xyz.gameoholic.hubfishing.player.minigame.ui

import net.kyori.adventure.text.Component
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.player.minigame.states.FishingMinigameGameplayState


/**
 * Renders water, fish and the rodbox on the player's screen.
 */
class FishingMinigameGameplayUIRenderer(override val minigameState: FishingMinigameGameplayState): FishingMinigameUIRenderer() {
    private val plugin: HubFishingPlugin by inject()


    private var clockAnimationFrame = 0
    private var clockAnimationDelay = 0 // When equal to animation speed, will increment the frame and reset this value to 0
    override fun render() {
        val title = Component.text()
        // WATER
        renderCharacter(title, plugin.config.fishingMinigame.waterCharacters[minigameState.minigameManager.waterAnimationFrame])
        // FISH
        renderCharacterSeparately(
            title, minigameState.minigameManager.caughtFish.variant.minigameCharacter,
            minigameState.minigameManager.fishMovementManager.fishPosition,
            minigameState.minigameManager.caughtFish.variant.minigameCharacterHeight
        )
        // ROD BOX
        renderCharacterSeparately(title, plugin.config.fishingMinigame.rodBoxCharacter, minigameState.minigameManager.rodBoxPosition, plugin.config.fishingMinigame.rodBoxCharacterHeight)
        // MINI RODS
        for (i in 0 until plugin.config.fishingMinigame.maxFishingRodUses) {
            val miniRodFrame = minigameState.minigameManager.miniFishingRodFrames[i]

            val miniRodCharacter = if (miniRodFrame != -1) // If rod is used and has an animation frame
                plugin.config.fishingMinigame.miniRodUsedCharacters[miniRodFrame]
            else
                plugin.config.fishingMinigame.miniRodCharacter

            renderCharacterSeparately(
                title, miniRodCharacter,
                plugin.config.fishingMinigame.miniRodsCharactersPosition + minFishingRodsOffset * i, plugin.config.fishingMinigame.miniRodCharacterHeight
            )
        }
        // BIG ROD
        renderCharacterSeparately(title, plugin.config.fishingMinigame.bigRodCharacters[0], plugin.config.fishingMinigame.bigRodPosition, plugin.config.fishingMinigame.bigRodCharacterHeight)

        // INFO BOX
        renderCharacterSeparately(title, plugin.config.fishingMinigame.infoBoxCharacter, plugin.config.fishingMinigame.infoBoxPosition, plugin.config.fishingMinigame.infoBoxCharacterHeight)

        // CLOCK
        if (minigameState.passedTimeRestriction) {
            renderCharacterSeparately(title, plugin.config.fishingMinigame.clockCharacters[clockAnimationFrame], plugin.config.fishingMinigame.clockPosition, plugin.config.fishingMinigame.clockCharacterHeight)

            clockAnimationDelay++
            if (clockAnimationDelay == plugin.config.fishingMinigame.clockAnimationSpeed) {
                clockAnimationFrame++
                if (clockAnimationFrame >= plugin.config.fishingMinigame.clockCharacters.size)
                    clockAnimationFrame = 0
                clockAnimationDelay = 0
            }
        }


        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}