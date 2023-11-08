package xyz.gameoholic.hubfishing.player.minigame.ui

import net.kyori.adventure.text.Component
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.player.minigame.states.FishingMinigameFailureState


/**
 * Renders water, fish and the rodbox on the player's screen.
 */

class FishingMinigameFailureUIRenderer(override val minigameState: FishingMinigameFailureState):
    FishingMinigameUIRenderer() {
    private val plugin: HubFishingPlugin by inject()

    private var rodBreakAnimationFrame = 0
    private var rodBreakAnimationDelay = 0 // When equal to animation speed, will increment the frame and reset this value to 0

    private var rodLongBreakAnimationFrame = 0
    private var rodLongBreakAnimationDelay = 0

    private var rodLongEndBreakAnimationFrame = 0
    private var rodLongEndBreakAnimationDelay = 0

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
        renderCharacterSeparately(
            title, plugin.config.rodBoxCharacter, minigameState.minigameManager.rodBoxPosition,
            plugin.config.rodBoxCharacterHeight
        )

        if (minigameState.failureReason == FishingMinigameFailureState.FailureReason.RAN_OUT_OF_ATTEMPTS) {
            // FISHING ROD
            renderCharacterSeparately(title, plugin.config.rodBreakCharacters[rodBreakAnimationFrame], plugin.config.bigRodPosition, plugin.config.rodBreakAnimationHeight)
            rodBreakAnimationDelay++
            if (rodBreakAnimationDelay == plugin.config.rodBreakAnimationSpeed && rodBreakAnimationFrame < plugin.config.rodBreakCharacters.size - 1) {
                rodBreakAnimationFrame++
                rodBreakAnimationDelay = 0
            }

            // LONG ROD EXTENSION
            val evenPixelAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition).toInt()
            val unevenPixelAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition)
            for (i in 0 until evenPixelAmount - longRodExtraWidth + 1) { //We don't animate the last X frames based on the longrod extra width. We add +1 so the last frame isn't skipped.
                renderCharacterSeparately(title, plugin.config.rodLongBreakCharacters[rodLongBreakAnimationFrame],
                    minigameState.longRodStartingPosition - i,
                    plugin.config.rodLongBreakAnimationHeight)
            }
            // Because the actual position may be a decimal, we render an extra long rod character to make it extend to the needed position perfectly
            if (unevenPixelAmount > evenPixelAmount) {
                renderCharacterSeparately(title, plugin.config.rodLongBreakCharacters[rodLongBreakAnimationFrame],
                    minigameState.longRodPosition + longRodExtraWidth,
                    plugin.config.rodLongBreakAnimationHeight)
            }
            // We render the end point of the rod line
            renderCharacterSeparately(title, plugin.config.rodLongEndBreakCharacters[rodLongEndBreakAnimationFrame],
                minigameState.longRodPosition + longRodExtraWidth + plugin.config.longRodEndCharacterOffset, // We offset the long end character because it's actually longer and isn't centered.
                plugin.config.rodLongEndBreakAnimationHeight)

            // Handle animation for long rod and rod end point breaking
            rodLongBreakAnimationDelay++
            if (rodLongBreakAnimationDelay == plugin.config.rodLongBreakAnimationSpeed && rodLongBreakAnimationFrame < plugin.config.rodLongBreakCharacters.size - 1) {
                rodLongBreakAnimationFrame++
                rodLongBreakAnimationDelay = 0
            }

            rodLongEndBreakAnimationDelay++
            if (rodLongEndBreakAnimationDelay == plugin.config.rodLongEndBreakAnimationSpeed && rodLongEndBreakAnimationFrame < plugin.config.rodLongEndBreakCharacters.size - 1) {
                rodLongEndBreakAnimationFrame++
                rodLongEndBreakAnimationDelay = 0
            }
        }
        else {
            renderCharacterSeparately(title, plugin.config.bigRodCharacters[0], plugin.config.bigRodPosition, plugin.config.bigRodCharacterHeight)
        }


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

        // INFO BOX
        renderCharacterSeparately(title, plugin.config.infoBoxCharacter, plugin.config.infoBoxPosition, plugin.config.infoBoxCharacterHeight)


        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}