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
        renderCharacter(title, plugin.config.waterCharacters[minigameState.minigameManager.waterAnimationFrame], false)
        // FISH
        renderCharacterSeparately(
            title, minigameState.minigameManager.caughtFish.variant.minigameCharacter,
            minigameState.minigameManager.fishMovementManager.fishPosition,
            minigameState.minigameManager.caughtFish.variant.minigameCharacterHeight
        )
        // ROD BOX
        renderCharacterSeparately(title, plugin.config.rodBoxCharacter, minigameState.minigameManager.rodBoxPosition, plugin.config.rodBoxCharacterHeight)
        // MINI RODS
        for (i in 0 until plugin.config.maxFishingRodUses) {
            val miniRodFrame = minigameState.minigameManager.miniFishingRodFrames[i]

            val miniRodCharacter = if (miniRodFrame != -1) // If rod is used and has an animation frame
                plugin.config.miniRodUsedCharacters[miniRodFrame]
            else
                plugin.config.miniRodCharacter

            renderCharacterSeparately(
                title, miniRodCharacter,
                plugin.config.miniRodsCharactersPosition + minFishingRodsOffset * i, plugin.config.miniRodCharacterHeight
            )
        }
        // BIG ROD
        renderCharacterSeparately(title, plugin.config.bigRodCharacters[0], plugin.config.bigRodPosition, plugin.config.bigRodCharacterHeight)

        // INFO BOX
        renderCharacterSeparately(title, plugin.config.infoBoxCharacter, plugin.config.infoBoxPosition, plugin.config.infoBoxCharacterHeight)

        // CLOCK
        if (minigameState.passedTimeRestriction) {
            renderCharacterSeparately(title, plugin.config.clockCharacters[clockAnimationFrame], plugin.config.clockPosition, plugin.config.clockCharacterHeight)

            clockAnimationDelay++
            if (clockAnimationDelay == plugin.config.clockAnimationSpeed) {
                clockAnimationFrame++
                if (clockAnimationFrame >= plugin.config.clockCharacters.size)
                    clockAnimationFrame = 0
                clockAnimationDelay = 0
            }
        }


        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}