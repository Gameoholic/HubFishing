package net.topstrix.hubinteractions.fishing.player.minigame.states

import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.fish.FishRarity
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameManager
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameState
import net.topstrix.hubinteractions.fishing.player.minigame.ui.FishingMinigameGameplayUIRenderer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*


class FishingMinigameGameplayState(val minigameManager: FishingMinigameManager): FishingMinigameState, Listener {

    private val rnd = Random()

    override var stateTicksPassed = 0

    private val uiRenderer: FishingMinigameGameplayUIRenderer = FishingMinigameGameplayUIRenderer(this)

    /** The fish's min position in UI pixels, from the right */
    private val fishMinPosition = 0.0 + uiRenderer.fishCharacterHeight
    /** The fish's max position in UI pixels, from the right */
    private val fishMaxPosition = (uiRenderer.waterCharacterHeight * uiRenderer.waterChunksAmount).toDouble()

    /** The rod box's min position in UI pixels, from the right */
    private val rodBoxMinPosition = 0.0 + uiRenderer.rodBoxCharacterHeight
    /** The rod box's max position in UI pixels, from the right */
    private val rodBoxMaxPosition = (uiRenderer.waterCharacterHeight * uiRenderer.waterChunksAmount).toDouble()

    private var fishSpeed = 1.0
    private val rodBoxSpeed = 0.8
    private val fishHitboxWidth = 5.0
    private enum class FishDirection {LEFT, RIGHT}
    private var fishDirection: FishDirection = FishDirection.LEFT

    /** The amount of ticks the fish is heading in the current direction */
    private var fishDirectionTime = 0
    /** The maximum amount of ticks the fish will head in the current direction */
    private var fishMaxDirectionTime = 0

    /** Whether the rod successfully caught a fish */
    var rodCastSuccess = false
        private set
    /** Whether the rod is currently in the process of being cast */
    private var rodBeingCast = false
    /** The ticks that have passed since the rod being cast */
    var rodBeingCastTicks = 0


    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, HubInteractions.plugin)
        Bukkit.getPluginManager().registerEvents(this, HubInteractions.plugin)

        minigameManager.rodBoxPosition = rodBoxMinPosition + (rodBoxMaxPosition - rodBoxMinPosition) / 2
        minigameManager.fishPosition = fishMinPosition + (fishMaxPosition - fishMinPosition) / 2
    }



    override fun onTick() {
        stateTicksPassed++

        val player = Bukkit.getPlayer(minigameManager.fishingPlayer.uuid)!! //TODO: what if null? Remove !!.

        if (rodBeingCast)
            onRodBeingCastTick()
        determineFishPosition()
        determineRodBoxPosition(player)
        uiRenderer.render()
    }

    /**
     * Determines the fish position based on algorithm
     */
    private fun determineFishPosition() {
        fishDirectionTime++
        if (fishDirectionTime >= fishMaxDirectionTime)
            switchFishDirection()
        moveFishInDirection()

        //Cap fish movement
        if (minigameManager.fishPosition > fishMaxPosition || minigameManager.fishPosition < fishMinPosition) {
            minigameManager.fishPosition = if (minigameManager.fishPosition > fishMaxPosition)
                fishMaxPosition
            else
                fishMinPosition
            switchFishDirection()
            //We don't want there to be a still frame when fish is at the edge, so we move in the new direction
            moveFishInDirection()
        }
    }

    /**
     * Moves fish in its current direction.
     */
    private fun moveFishInDirection() {
        if (fishDirection == FishDirection.RIGHT)
            minigameManager.fishPosition -= fishSpeed
        else
            minigameManager.fishPosition += fishSpeed
    }

    /**
     * Switches the fish's current direction.
     */
    private fun switchFishDirection() {
        fishDirectionTime = 0
        fishMaxDirectionTime = determineFishDirectionDuration(minigameManager.caughtFish.variant.rarity)
        fishDirection = if (fishDirection == FishDirection.LEFT)
            FishDirection.RIGHT
        else
            FishDirection.LEFT
    }

    /**
     * Changes the rod box's position based on the player's movement (A and D buttons)
     */
    private fun determineRodBoxPosition(player: Player) {
        if (rodBeingCast) return
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

    /**
     * Determines the amount of ticks the fish will move in a direction, based on its rarity.
     */
    private fun determineFishDirectionDuration(fishRarity: FishRarity): Int {
        return rnd.nextInt(fishRarity.minigameMinDirectionDuration, fishRarity.minigameMaxDirectionDuration + 1)
    }
    private enum class MoveDirection {LEFT, RIGHT, NEITHER}

    /**
     * Returns the direction the player's moving at.
     * @return MoveDirection.LEFT if player presses A (left), MoveDirection.RIGHT if player presses D (right), MoveDirection.NEITHER otherwise.
     */
    private fun getPlayerMoveDirection(player: Player): MoveDirection {
        val playerRotation = player.location.direction.clone().normalize().apply { this.y = 0.0 } //We want to ignore the y velocity for our calculations
        val playerMovement = player.velocity.clone().normalize().apply { this.y = 0.0 }

        val angle = Math.toDegrees(playerMovement.clone().angle(playerRotation).toDouble())
        if (playerMovement.isZero || angle < 80 || angle > 100) return MoveDirection.NEITHER //Make sure player isn't pressing W/S

        if (playerRotation.x > 0 && playerRotation.z > 0) {
            if (playerMovement.x > 0 && playerMovement.z < 0)
                return MoveDirection.LEFT
        }
        else if (playerRotation.x < 0 && playerRotation.z > 0) {
            if (playerMovement.x > 0 && playerMovement.z > 0)
                return MoveDirection.LEFT
        }
        else if (playerRotation.x < 0 && playerRotation.z < 0) {
            if (playerMovement.x < 0 && playerMovement.z > 0)
                return MoveDirection.LEFT
        }
        else if (playerRotation.x > 0 && playerRotation.z < 0) {
            if (playerMovement.x < 0 && playerMovement.z < 0)
                return MoveDirection.LEFT
        }
        return MoveDirection.RIGHT
    }

    /**
     * Called when the player right clicks
     */
    private fun onRodCast() {
        if (rodCastSuccess || minigameManager.fishingRodUsesLeft <= 0 || rodBeingCast) return
        rodBeingCast = true
    }

    /**
     * Called every tick, after the player's right-clicked and cast the rod
     */
    private fun onRodBeingCastTick() {
        rodBeingCastTicks++
        if (rodBeingCastTicks == 5) {
            if (didRodCatchFish()) {
                rodCastSuccess = true
            }
            else {
                minigameManager.fishingRodUsesLeft--
            }
        }
        if (rodBeingCastTicks >= 25) {
            rodBeingCastTicks = 0
            rodBeingCast = false
        }
    }
    /**
     * @return Whether the rod box is within the fish's hitbox.
     */
    private fun didRodCatchFish(): Boolean {
        val fishCenteredPosition = minigameManager.fishPosition - uiRenderer.fishCharacterHeight / 2
        val rodBoxCenteredPosition = minigameManager.rodBoxPosition - uiRenderer.rodBoxCharacterHeight / 2
        return rodBoxCenteredPosition < fishCenteredPosition + fishHitboxWidth
            && rodBoxCenteredPosition > fishCenteredPosition - fishHitboxWidth
    }

    @EventHandler
    fun onPlayerFish(e: PlayerFishEvent) {
        if (e.isCancelled) return
        e.isCancelled = true
        onRodCast()
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.isCancelled) return
        e.isCancelled = true
        onRodCast()
    }

    override fun onDisable() {
        PlayerFishEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)
    }


}