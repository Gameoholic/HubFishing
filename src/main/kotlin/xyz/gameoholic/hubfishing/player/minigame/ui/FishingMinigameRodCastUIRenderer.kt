package xyz.gameoholic.hubfishing.player.minigame.ui

import net.kyori.adventure.text.Component
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.player.minigame.states.FishingMinigameRodCastState

/**
 * Renders everything the GameplayUIRenderer does, and also animates
 * the big fishing rod cast animation, and the extension
 * of the rod itself.
 */
class FishingMinigameRodCastUIRenderer(override val minigameState: FishingMinigameRodCastState): FishingMinigameUIRenderer() {
    private val plugin: HubFishingPlugin by inject()

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
        // BIG ROD ANIMATION
        when (minigameState.stateTicksPassed) {
            in 0 until plugin.config.bigRodCharacters.size -> {
                renderCharacterSeparately(title, plugin.config.bigRodCharacters[minigameState.stateTicksPassed], plugin.config.bigRodPosition, plugin.config.bigRodCharacterHeight)
            }
            else -> renderCharacterSeparately(title, plugin.config.bigRodCharacters[plugin.config.bigRodCharacters.size - 1], plugin.config.bigRodPosition, plugin.config.bigRodCharacterHeight)
        }

        // LONG ROD EXTENSION ANIMATION
        if (minigameState.extensionTicksPassed > 0) {
            val evenPixelAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition).toInt()
            val unevenPixelAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition)
            for (i in 0 until evenPixelAmount - longRodExtraWidth + 1) { //We don't animate the last X frames based on the longrod extra width. We add +1 so the last frame isn't skipped.
                    renderCharacterSeparately(title, plugin.config.longRodCharacter,
                        minigameState.longRodStartingPosition - i,
                        plugin.config.longRodCharacterHeight)
            }
            // Because the actual position may be a decimal, we render an extra long rod character to make it extend to the needed position perfectly
            if (unevenPixelAmount > evenPixelAmount) {
                renderCharacterSeparately(title, plugin.config.longRodCharacter,
                    minigameState.longRodPosition + longRodExtraWidth,
                    plugin.config.longRodCharacterHeight)
            }
            // We render the end point of the rod line
            renderCharacterSeparately(title, plugin.config.longRodEndCharacter,
                minigameState.longRodPosition + longRodExtraWidth + plugin.config.longRodEndCharacterOffset, // We offset the long end character because it's actually longer and isn't centered.
                plugin.config.longRodEndCharacterHeight)
        }

        // INFO BOX
        renderCharacterSeparately(title, plugin.config.infoBoxCharacter, plugin.config.infoBoxPosition, plugin.config.infoBoxCharacterHeight)


        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}