package net.topstrix.hubinteractions.fishing.player.minigame.states

import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameManager
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameState
import net.topstrix.hubinteractions.fishing.player.minigame.ui.FishingMinigameGameplayUIRenderer
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent


/**
 * Minigame has started and player can control the rod box freely, in
 * order to catch the fish.
 *
 * @param minigameManager The minigame manager
 * @param rodBoxMinPosition The rod box's min position in UI pixels, from the right
 * @param rodBoxMaxPosition The rod box's max position in UI pixels, from the right
 * @param rodBoxSpeed The speed of the rod box
 */
class FishingMinigameGameplayState(
    val minigameManager: FishingMinigameManager,
    private val rodBoxMinPosition: Double,
    private val rodBoxMaxPosition: Double,
    private val rodBoxSpeed: Double
) : FishingMinigameState, Listener {


    override var stateTicksPassed = 0

    private val uiRenderer: FishingMinigameGameplayUIRenderer = FishingMinigameGameplayUIRenderer(this)


    /** Whether the player right-clicked. If true, signals to the minigame manager to switch states */
    var rodCast = false
        private set

    /** The ticks that have passed since the rod being cast */
    var rodBeingCastTicks = 0


    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, HubInteractions.plugin)
    }


    override fun onTick() {
        stateTicksPassed++

        val player = Bukkit.getPlayer(minigameManager.fishingPlayer.uuid)!! //TODO: what if null? Remove !!.

        minigameManager.fishMovementManager.updateFishPosition()
        determineRodBoxPosition(player)
        uiRenderer.render()
    }

    /**
     * Changes the rod box's position based on the player's movement (A and D buttons)
     */
    private fun determineRodBoxPosition(player: Player) {
        if (rodCast) return
        if (getPlayerMoveDirection(player) == MoveDirection.LEFT)
            minigameManager.rodBoxPosition += rodBoxSpeed
        else if (getPlayerMoveDirection(player) == MoveDirection.RIGHT)
            minigameManager.rodBoxPosition -= rodBoxSpeed
        //Cap rod box movement
        if (minigameManager.rodBoxPosition > rodBoxMaxPosition)
            minigameManager.rodBoxPosition = rodBoxMaxPosition
        else if (minigameManager.rodBoxPosition < rodBoxMinPosition)
            minigameManager.rodBoxPosition = rodBoxMinPosition
    }

    private enum class MoveDirection { LEFT, RIGHT, NEITHER }

    /**
     * Returns the direction the player's moving at.
     * @return MoveDirection.LEFT if player presses A (left), MoveDirection.RIGHT if player presses D (right), MoveDirection.NEITHER otherwise.
     */
    private fun getPlayerMoveDirection(player: Player): MoveDirection {
        val playerRotation = player.location.direction.clone().normalize()
            .apply { this.y = 0.0 } //We want to ignore the y velocity for our calculations
        val playerMovement = player.velocity.clone().normalize().apply { this.y = 0.0 }

        val angle = Math.toDegrees(playerMovement.clone().angle(playerRotation).toDouble())
        if (playerMovement.isZero || angle < 80 || angle > 100) return MoveDirection.NEITHER //Make sure player isn't pressing W/S

        if (playerRotation.x > 0 && playerRotation.z > 0) {
            if (playerMovement.x > 0 && playerMovement.z < 0)
                return MoveDirection.LEFT
        } else if (playerRotation.x < 0 && playerRotation.z > 0) {
            if (playerMovement.x > 0 && playerMovement.z > 0)
                return MoveDirection.LEFT
        } else if (playerRotation.x < 0 && playerRotation.z < 0) {
            if (playerMovement.x < 0 && playerMovement.z > 0)
                return MoveDirection.LEFT
        } else if (playerRotation.x > 0 && playerRotation.z < 0) {
            if (playerMovement.x < 0 && playerMovement.z < 0)
                return MoveDirection.LEFT
        }
        return MoveDirection.RIGHT
    }

    /**
     * Called when the player right clicks.
     * Will set property rodCast to true, which will make the minigame manager switch states.
     */
    private fun onRodCast() {
        rodCast = true
    }

    @EventHandler
    fun onPlayerFish(e: PlayerFishEvent) {
        if (e.player.uniqueId != minigameManager.fishingPlayer.uuid) return

        if (e.isCancelled) return
        e.isCancelled = true
        onRodCast()
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.player.uniqueId != minigameManager.fishingPlayer.uuid) return

        if (e.isCancelled) return
        e.isCancelled = true
        onRodCast()
    }

    override fun onDisable() {
        PlayerFishEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)
    }


}