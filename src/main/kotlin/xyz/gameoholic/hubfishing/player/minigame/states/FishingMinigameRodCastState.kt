package xyz.gameoholic.hubfishing.player.minigame.states

import xyz.gameoholic.hubfishing.player.minigame.FishingMinigameManager
import xyz.gameoholic.hubfishing.player.minigame.FishingMinigameState
import xyz.gameoholic.hubfishing.player.minigame.ui.FishingMinigameRodCastUIRenderer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import net.kyori.adventure.sound.Sound
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject

class FishingMinigameRodCastState(val minigameManager: FishingMinigameManager): FishingMinigameState, Listener {
    private val plugin: HubFishingPlugin by inject()

    override var stateTicksPassed = 0

    private val uiRenderer = FishingMinigameRodCastUIRenderer(this)

    /** The speed in pixels, at which the rod will extend per tick */
    private val rodExtendingSpeed = 3 //todo: from config. the -1 extra width too please
    /** The target (max) position of the long rod, in UI pixels to the left*/
    private val longRodTargetPosition = minigameManager.rodBoxPosition - plugin.config.fishingMinigame.rodBoxCharacterHeight / 2 //Center of rodbox
    /** The starting position of the long rod, in UI pixels to the left*/
    val longRodStartingPosition = plugin.config.fishingMinigame.bigRodPosition - plugin.config.fishingMinigame.bigRodCharacterHeight + 4.0 //TODo: I'm not sure why it's 4.0.
    /** The position of right-most pixel of the long rod, in UI pixels to the left*/
    var longRodPosition: Double = longRodStartingPosition
        private set
    /** How many ticks passed since the extension of the long rod started */
    var extensionTicksPassed = 0
        private set
    /** Whether the fish was caught. Set to null if yet to be determined */
    var fishCaught: Boolean? = null

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, plugin)
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    var a = 0
    override fun onTick() {
        stateTicksPassed++

        minigameManager.fishMovementManager.updateFishPosition()

        playLongRodAnimation()
        checkIfRodExtendAnimationFinished()


        uiRenderer.render()
    }

    /**
     * Plays the animation for the long rod extending.
     */
    private fun playLongRodAnimation() {
        val extensionAnimationDelay = plugin.config.fishingMinigame.extensionAnimationDelay // By how many ticks to delay the extension. Use negative value to make it start earlier
        //If bigrod cast animation finished, we start longrod extend animation
        if (stateTicksPassed - extensionAnimationDelay >= plugin.config.fishingMinigame.bigRodCharacters.size &&
            longRodPosition > longRodTargetPosition) {
            extensionTicksPassed = (stateTicksPassed - extensionAnimationDelay) -
                plugin.config.fishingMinigame.bigRodCharacters.size + 1 // How many ticks passed, since extension
            longRodPosition = longRodStartingPosition - extensionTicksPassed * rodExtendingSpeed
            if (longRodPosition < longRodTargetPosition) // Cap position, if speed is too high, and it passes target pos.
                longRodPosition = longRodTargetPosition
        }
    }

    /**
     * Checks and handles if the rod extending animation has finished.
     * If it did, handles it.
     */
    private fun checkIfRodExtendAnimationFinished() {
        //rod cast animation finished
        if (longRodPosition <= longRodTargetPosition) {
            if (didRodCatchFish()) {
                Bukkit.getPlayer(minigameManager.fishingPlayer.uuid)?.playSound(
                    plugin.config.sounds.fishingMinigameCatchSound, Sound.Emitter.self())
                fishCaught = true
            }
            else {
                Bukkit.getPlayer(minigameManager.fishingPlayer.uuid)?.playSound(
                    plugin.config.sounds.fishingMinigameMissSound, Sound.Emitter.self())
                fishCaught = false
                minigameManager.fishingRodUsesLeft--
            }
        }
    }

    /**
     * @return Whether the rod box is within the fish's hitbox.
     */
    private fun didRodCatchFish(): Boolean {
        val fishHitboxWidth = minigameManager.caughtFish.variant.minigameHitboxWidth
        val fishCenteredPosition = minigameManager.fishMovementManager.fishPosition -
            minigameManager.caughtFish.variant.minigameCharacterHeight / 2
        val rodBoxCenteredPosition = minigameManager.rodBoxPosition - plugin.config.fishingMinigame.rodBoxCharacterHeight / 2
        return rodBoxCenteredPosition < fishCenteredPosition + fishHitboxWidth
            && rodBoxCenteredPosition > fishCenteredPosition - fishHitboxWidth
    }

    @EventHandler
    fun onPlayerFish(e: PlayerFishEvent) {
        if (e.player.uniqueId != minigameManager.fishingPlayer.uuid) return

        if (e.isCancelled) return
        e.isCancelled = true
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.player.uniqueId != minigameManager.fishingPlayer.uuid) return

        if (e.isCancelled) return
        e.isCancelled = true
    }
    override fun onDisable() {
        PlayerFishEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)
    }

}