package xyz.gameoholic.hubfishing.player.minigame.states

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.MiniMessage
import xyz.gameoholic.hubfishing.player.minigame.FishingMinigameManager
import xyz.gameoholic.hubfishing.player.minigame.FishingMinigameState
import xyz.gameoholic.hubfishing.player.minigame.ui.FishingMinigameGameplayUIRenderer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject


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
    val minigameManager: FishingMinigameManager
) : FishingMinigameState, Listener {
    private val plugin: HubFishingPlugin by inject()

    override var stateTicksPassed = 0

    private val uiRenderer: FishingMinigameGameplayUIRenderer = FishingMinigameGameplayUIRenderer(this)

    /**
     * Whether the time restriction was passed and the clock should be animated.
     */
    var passedTimeRestriction = false

    /**
     * How many ticks have passed since the last time restriction strike was received.
     */
    private var ticksPassedSinceLastTimeRestriction = 0

    var failedBecauseOfTimeRestriction = false

    /**
     * Whether the player right-clicked. If true, signals to the minigame manager to switch states
     */
    var rodCast = false
        private set

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }


    override fun onTick() {
        stateTicksPassed++
        ticksPassedSinceLastTimeRestriction++

        val player = Bukkit.getPlayer(minigameManager.fishingPlayer.uuid)!! //TODO: what if null? Remove !!.

        minigameManager.fishMovementManager.updateFishPosition()
        determineRodBoxPosition(player)

        failedBecauseOfTimeRestriction = false

        // If player spent too much time without doing anything:
        if (!passedTimeRestriction && ticksPassedSinceLastTimeRestriction > plugin.config.fishingMinigame.timeRestrictionWarningDelay) {
            passedTimeRestriction = true
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                PlaceholderAPI.setPlaceholders(player, plugin.config.strings.passedTimeRestrictionMessage))
            )
            player.playSound(plugin.config.sounds.passedTimeRestrictionSound)
        }
        // If time passed after waning was given, and player still hasn't done anything, remove an attempt
        if (passedTimeRestriction && ticksPassedSinceLastTimeRestriction >
            plugin.config.fishingMinigame.timeRestrictionStrikeDelay + plugin.config.fishingMinigame.timeRestrictionWarningDelay) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                PlaceholderAPI.setPlaceholders(player, plugin.config.strings.timeRestrictionStrikeMessage))
            )
            Bukkit.getPlayer(minigameManager.fishingPlayer.uuid)?.playSound(
                plugin.config.sounds.timeRestrictionStrikeSound, Sound.Emitter.self())
            minigameManager.fishingRodUsesLeft--
            passedTimeRestriction = false
            ticksPassedSinceLastTimeRestriction = 0
            failedBecauseOfTimeRestriction = true
        }

        uiRenderer.render()
    }

    /**
     * Changes the rod box's position based on the player's movement (A and D buttons)
     */
    private fun determineRodBoxPosition(player: Player) {
        if (rodCast) return
        if (getPlayerMoveDirection(player) == MoveDirection.LEFT)
            minigameManager.rodBoxPosition += plugin.config.fishingMinigame.rodBoxSpeed
        else if (getPlayerMoveDirection(player) == MoveDirection.RIGHT)
            minigameManager.rodBoxPosition -= plugin.config.fishingMinigame.rodBoxSpeed
        //Cap rod box movement
        if (minigameManager.rodBoxPosition > minigameManager.rodBoxMaxPosition)
            minigameManager.rodBoxPosition = minigameManager.rodBoxMaxPosition
        else if (minigameManager.rodBoxPosition < minigameManager.rodBoxMinPosition)
            minigameManager.rodBoxPosition = minigameManager.rodBoxMinPosition
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