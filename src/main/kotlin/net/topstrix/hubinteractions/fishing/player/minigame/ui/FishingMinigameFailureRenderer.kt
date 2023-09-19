package net.topstrix.hubinteractions.fishing.player.minigame.ui

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import net.topstrix.hubinteractions.fishing.player.minigame.states.FishingMinigameFailureState
import net.topstrix.hubinteractions.fishing.player.minigame.states.FishingMinigameSuccessState
import org.bukkit.Bukkit
import java.time.Duration


/**
 * Renders water, fish and the rodbox on the player's screen.
 */
class FishingMinigameFailureRenderer(override val minigameState: FishingMinigameFailureState): FishingMinigameUIRenderer() {
    override fun render() {
        val title = Component.text()
        // WATER
        renderCharacters(title, waterCharacter, waterChunksAmount)
        // FISH
        renderCharacterSeparately(title, fishCharacter, minigameState.minigameManager.fishPosition, fishCharacterHeight)
        // ROD BOX
        renderCharacterSeparately(title, rodBoxCharacter, minigameState.minigameManager.rodBoxPosition, rodBoxCharacterHeight)
        // FISHING ROD
        renderCharacterSeparately(title, 'F', 80.0, 6)
        // FISHING RODS
        for (i in 0 until minigameState.minigameManager.maxFishingRodUses) { //3,1
            if ((minigameState.minigameManager.maxFishingRodUses - i) > minigameState.minigameManager.fishingRodUsesLeft)
                renderCharacterSeparately(title, fishingRodUsedCharacter,
                    fishingRodsPosition + fishingRodOffsets * i, fishingRodCharacterHeight)
            else
                renderCharacterSeparately(title, fishingRodCharacter,
                    fishingRodsPosition + fishingRodOffsets * i, fishingRodUsedCharacterHeight)
        }

        display(minigameState.minigameManager.fishingPlayer.uuid, title)
    }
}