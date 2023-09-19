package net.topstrix.hubinteractions.fishing.player.minigame.ui

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import net.topstrix.hubinteractions.fishing.player.minigame.states.FishingMinigameGameplayState
import net.topstrix.hubinteractions.fishing.util.LoggerUtil
import org.bukkit.Bukkit
import java.time.Duration


/**
 * Renders water, fish and the rodbox on the player's screen.
 */
class FishingMinigameGameplayUIRenderer(override val minigameState: FishingMinigameGameplayState): FishingMinigameUIRenderer() {
    override fun render() {
        val title = Component.text()
        // WATER
        renderCharacters(title, waterCharacter, waterChunksAmount)
        // FISH
        renderCharacterSeparately(title, fishCharacter, minigameState.minigameManager.fishPosition, fishCharacterHeight)
        // ROD BOX
        renderCharacterSeparately(title, rodBoxCharacter, minigameState.minigameManager.rodBoxPosition, rodBoxCharacterHeight)
        // FISHING RODS
        for (i in 0 until minigameState.minigameManager.maxFishingRodUses) { //3,1
            if ((minigameState.minigameManager.maxFishingRodUses - i) > minigameState.minigameManager.fishingRodUsesLeft)
                renderCharacterSeparately(title, fishingRodUsedCharacter,
                    fishingRodsPosition + fishingRodOffsets * i, fishingRodCharacterHeight)
            else
                renderCharacterSeparately(title, fishingRodCharacter,
                    fishingRodsPosition + fishingRodOffsets * i, fishingRodUsedCharacterHeight)
        }
        // ROD ANIMATION
        when (minigameState.rodBeingCastTicks) {
            0 -> renderCharacterSeparately(title, fishingRodAnimCharacters[0], 60.0, fishingRodAnimCharacterHeight)
            in 1..4 -> {
                renderCharacterSeparately(title, fishingRodAnimCharacters[minigameState.rodBeingCastTicks], 60.0, fishingRodAnimCharacterHeight)
            }
            else -> renderCharacterSeparately(title, fishingRodAnimCharacters[5], 60.0, fishingRodAnimCharacterHeight)
        }

        if (minigameState.rodBeingCastTicks > 4) {
            val targetPos = minigameState.minigameManager.rodBoxPosition - rodBoxCharacterHeight / 2 //center of rod box
            val startingPos = 50
            val speed = 3
            val ticksPassed = minigameState.rodBeingCastTicks - 4 //todo
            for (i in 0 until ticksPassed) { //For every tick that has passed:
                for (j in 0 until speed) {  //We animate it X (speed) times
                    val currentPos = startingPos - i * speed - j
                    if (currentPos <= targetPos) break //todo: this is shit
                    renderCharacterSeparately(title, fishingRodLongPartCharacter,
                        (currentPos).toDouble(),
                        fishingRodLongPartCharacterHeight)
                }
            }

        }


        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}