package net.topstrix.hubinteractions.fishing.player.minigame.states

import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameManager
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameState
import net.topstrix.hubinteractions.fishing.player.minigame.ui.FishingMinigameRodCastUIRenderer
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent

class FishingMinigameRodCastState(val minigameManager: FishingMinigameManager): FishingMinigameState, Listener {
    override var stateTicksPassed = 0

    private val uiRenderer = FishingMinigameRodCastUIRenderer(this)

    /** The speed in pixels, at which the rod will extend per tick */
    val rodExtendingSpeed = 3 //todo: from config. the -1 extra width too please
    /** The target (max) position of the long rod, in UI pixels to the left*/
    val longRodTargetPosition = minigameManager.rodBoxPosition - FishingUtil.fishingConfig.rodBoxCharacterHeight / 2 //Center of rodbox
    /** The starting position of the long rod, in UI pixels to the left*/
    val longRodStartingPosition = uiRenderer.bigRodPosition - FishingUtil.fishingConfig.bigRodCharacterHeight
    /** The position of right-most pixel of the long rod, in UI pixels to the left*/
    var longRodPosition: Double = longRodStartingPosition
        private set
    /** How many ticks passed since the extension of the long rod started */
    var extensionTicksPassed = 0
        private set
    /** Whether the fish was caught. Set to null if yet to be determined */
    var fishCaught: Boolean? = null

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, HubInteractions.plugin)
        Bukkit.getPluginManager().registerEvents(this, HubInteractions.plugin)
    }

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
        //If bigrod cast animation finished, we start longrod extend animation
        if (stateTicksPassed >= FishingUtil.fishingConfig.bigRodCharacters.size && longRodPosition > longRodTargetPosition) {
            extensionTicksPassed = stateTicksPassed - FishingUtil.fishingConfig.bigRodCharacters.size + 1 //How many ticks passed, since extension
            longRodPosition = longRodStartingPosition - extensionTicksPassed * rodExtendingSpeed
            if (longRodPosition < longRodTargetPosition) //Cap position, if speed is too high, and it passes target pos.
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
                fishCaught = true
            }
            else {
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
        val rodBoxCenteredPosition = minigameManager.rodBoxPosition - FishingUtil.fishingConfig.rodBoxCharacterHeight / 2
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