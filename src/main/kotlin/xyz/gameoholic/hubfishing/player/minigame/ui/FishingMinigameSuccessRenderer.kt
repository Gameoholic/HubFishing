package xyz.gameoholic.hubfishing.player.minigame.ui

import net.kyori.adventure.text.Component
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.player.minigame.states.FishingMinigameSuccessState


/**
 * Renders water, fish and the rodbox on the player's screen.
 */
class FishingMinigameSuccessRenderer(override val minigameState: FishingMinigameSuccessState): FishingMinigameUIRenderer() {
    private val plugin: HubFishingPlugin by inject()


    private var bucketAnimationFrame = 0
    private var bucketAnimationDelay = 0 // When equal to animation speed, will increment the frame and reset this value to 0
    override fun render() {
        val title = Component.text()
        // WATER
        renderCharacter(title, plugin.config.fishingMinigame.waterCharacters[minigameState.minigameManager.waterAnimationFrame], false)
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

        // BIG ROD - display last frame
        renderCharacterSeparately(title, plugin.config.fishingMinigame.bigRodCharacters[plugin.config.fishingMinigame.bigRodCharacters.size - 1], plugin.config.fishingMinigame.bigRodPosition, plugin.config.fishingMinigame.bigRodCharacterHeight)

        // LONG ROD EXTENSION ANIMATION
        val longRodStartingPosition = minigameState.longRodStartingPosition
        val longRodPosition = minigameState.longRodPosition
        val evenPixelAmount = (longRodStartingPosition - longRodPosition).toInt()
        val unevenPixelAmount = (longRodStartingPosition - longRodPosition)
        for (i in 0 until evenPixelAmount - longRodExtraWidth + 1) { //We don't animate the last X frames based on the longrod extra width. We add +1 so the last frame isn't skipped.
            renderCharacterSeparately(title, plugin.config.fishingMinigame.longRodCharacter,
                longRodStartingPosition - i,
                plugin.config.fishingMinigame.longRodCharacterHeight)
        }
        // Because the actual position may be a decimal, we render an extra long rod character to make it extend to the needed position perfectly
        if (unevenPixelAmount > evenPixelAmount) {
            renderCharacterSeparately(title, plugin.config.fishingMinigame.longRodCharacter,
                longRodPosition + longRodExtraWidth,
                plugin.config.fishingMinigame.longRodCharacterHeight)
        }
        // We render the end point of the rod line
        renderCharacterSeparately(title, plugin.config.fishingMinigame.longRodEndCharacter,
            longRodPosition + longRodExtraWidth + plugin.config.fishingMinigame.longRodEndCharacterOffset, // We offset the long end character because it's actually longer and isn't centered.
            plugin.config.fishingMinigame.longRodEndCharacterHeight)


        // BUCKET ANIM
        val fishPosition = minigameState.minigameManager.fishMovementManager.fishPosition
        val fishHeight = minigameState.minigameManager.caughtFish.variant.minigameCharacterHeight
        renderCharacterSeparately(title, plugin.config.fishingMinigame.bucketCharacters[bucketAnimationFrame],
             fishPosition + fishHeight / 2.0 + plugin.config.fishingMinigame.bucketOffset, //We make the bucket centered on fish
            plugin.config.fishingMinigame.bucketCharacterHeight)

        if (bucketAnimationFrame < plugin.config.fishingMinigame.bucketCharacters.size - 1) {
            bucketAnimationDelay++
            if (bucketAnimationDelay == plugin.config.fishingMinigame.fishingMinigameSuccessAnimationSpeed) {
                bucketAnimationFrame++
                bucketAnimationDelay = 0
            }
        }

        // INFO BOX
        renderCharacterSeparately(title, plugin.config.fishingMinigame.infoBoxCharacter, plugin.config.fishingMinigame.infoBoxPosition, plugin.config.fishingMinigame.infoBoxCharacterHeight)

        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}