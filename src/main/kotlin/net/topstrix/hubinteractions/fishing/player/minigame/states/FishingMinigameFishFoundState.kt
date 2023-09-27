package net.topstrix.hubinteractions.fishing.player.minigame.states

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameManager
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameState
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay

/**
 * In this state, the fish was lured and collided with the fishing rod's hitbox.
 * Displays particles, lowers the bobber (just like in vanilla minecraft), and
 * spawns an exclamation mark display.
 */
class FishingMinigameFishFoundState(private val minigameManager: FishingMinigameManager): FishingMinigameState {

    override var stateTicksPassed = 0
    override fun onEnable() {
        minigameManager.fishingPlayer.hook.velocity = minigameManager.fishingPlayer.hook.velocity.apply { this.y -= 0.15 } //Emulate bobber going down
        displayFishCaughtParticles()
        spawnExclamationMarkDisplay()
    }

    override fun onTick() {
        stateTicksPassed++
    }

    /**
     * Spawns ! text display, which will be visible only to this player.
     */
    private fun spawnExclamationMarkDisplay() {
        val textDisplayLoc = minigameManager.fishingPlayer.hook.location.clone().apply { this.y += 0.25 }
        minigameManager.textDisplay = minigameManager.fishingPlayer.hook.location.world
            .spawnEntity(textDisplayLoc, EntityType.TEXT_DISPLAY) as TextDisplay
        minigameManager.textDisplay.isVisibleByDefault = false
        minigameManager.textDisplay.text(
            text().content("!").color(TextColor.color(0xFD0707)).decorate(TextDecoration.BOLD).build()
        )
        minigameManager.textDisplay.alignment = TextDisplay.TextAlignment.CENTER
        minigameManager.textDisplay.billboard = Display.Billboard.CENTER
        minigameManager.textDisplay.backgroundColor = Color.fromARGB(0, 0, 0, 0)


        Bukkit.getPlayer(minigameManager.fishingPlayer.uuid)?.showEntity(HubInteractions.plugin, minigameManager.textDisplay)
    }


    private fun displayFishCaughtParticles() {
        minigameManager.fishingPlayer.hook.apply { this.world.spawnParticle(
            Particle.END_ROD,
            this.location,
            5,
            0.1,
            0.1,
            0.1,
            1.0
        ) }
    }

    override fun onDisable() {

    }


}