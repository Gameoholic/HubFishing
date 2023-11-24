package xyz.gameoholic.hubfishing.player.minigame.ui

import net.kyori.adventure.text.Component
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.player.minigame.states.FishingMinigameMissState


/**
 * Renders water, fish and the rodbox on the player's screen.
 */
class FishingMinigameMissUIRenderer(override val minigameState: FishingMinigameMissState):
    FishingMinigameUIRenderer() {
    private val plugin: HubFishingPlugin by inject()

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
        renderCharacterSeparately(
            title, plugin.config.fishingMinigame.rodBoxCharacter, minigameState.minigameManager.rodBoxPosition,
            plugin.config.fishingMinigame.rodBoxCharacterHeight
        )

        // BIG ROD
        renderCharacterSeparately(title, plugin.config.fishingMinigame.bigRodCharacters[plugin.config.fishingMinigame.bigRodCharacters.size - 1], plugin.config.fishingMinigame.bigRodPosition, plugin.config.fishingMinigame.bigRodCharacterHeight)



        // LONG ROD EXTENSION
        val evenPixelAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition).toInt()
        val unevenPixelAmount = (minigameState.longRodStartingPosition - minigameState.longRodPosition)
        for (i in 0 until evenPixelAmount - longRodExtraWidth + 1) { //We don't animate the last X frames based on the longrod extra width. We add +1 so the last frame isn't skipped.
            renderCharacterSeparately(title, plugin.config.fishingMinigame.longRodCharacter,
                minigameState.longRodStartingPosition - i,
                plugin.config.fishingMinigame.longRodCharacterHeight)
        }
        // Because the actual position may be a decimal, we render an extra long rod character to make it extend to the needed position perfectly
        if (unevenPixelAmount > evenPixelAmount) {
            renderCharacterSeparately(title, plugin.config.fishingMinigame.longRodCharacter,
                minigameState.longRodPosition + longRodExtraWidth,
                plugin.config.fishingMinigame.longRodCharacterHeight)
        }
        // We render the end point of the rod line
        renderCharacterSeparately(title, plugin.config.fishingMinigame.longRodEndCharacter,
            minigameState.longRodPosition + longRodExtraWidth + plugin.config.fishingMinigame.longRodEndCharacterOffset, // We offset the long end character because it's actually longer and isn't centered.
            plugin.config.fishingMinigame.longRodEndCharacterHeight)

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

        // INFO BOX
        renderCharacterSeparately(title, plugin.config.fishingMinigame.infoBoxCharacter, plugin.config.fishingMinigame.infoBoxPosition, plugin.config.fishingMinigame.infoBoxCharacterHeight)


        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}