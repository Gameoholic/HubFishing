package net.topstrix.hubinteractions.fishing.player.minigame

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.title.TitlePart
import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.fish.Fish
import net.topstrix.hubinteractions.fishing.player.FishingPlayer
import net.topstrix.hubinteractions.fishing.player.minigame.states.*
import net.topstrix.hubinteractions.fishing.player.minigame.states.util.FishMovementManager
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.lang.RuntimeException


/**
 * Manages a fishing minigame session for a player.
 *
 * @param fishingPlayer The player this minigame manager's for
 * @param caughtFish The fish that was caught
 */
class FishingMinigameManager(val fishingPlayer: FishingPlayer, val caughtFish: Fish) {
    private var state: FishingMinigameState = FishingMinigameFishFoundState(this)
    private val task: BukkitTask
    val fishMovementManager = FishMovementManager(
        0.0 + FishingUtil.fishingConfig.fishCharacterHeight,
        (FishingUtil.fishingConfig.waterCharacterHeight * FishingUtil.fishingConfig.waterAmount).toDouble(),
        caughtFish
    )

    /** The armor stand the player is mounted on */
    private lateinit var armorStand: ArmorStand
    lateinit var textDisplay: TextDisplay

    /** The rod box's min position in UI pixels, from the right */
    private val rodBoxMinPosition = 0.0 + FishingUtil.fishingConfig.rodBoxCharacterHeight

    /** The rod box's max position in UI pixels, from the right */
    private val rodBoxMaxPosition =
        (FishingUtil.fishingConfig.waterCharacterHeight * FishingUtil.fishingConfig.waterAmount).toDouble()

    /** The rod box's position in UI pixels, from the right */
    var rodBoxPosition = rodBoxMinPosition + (rodBoxMaxPosition - rodBoxMinPosition) / 2

    var fishingRodUsesLeft = FishingUtil.fishingConfig.maxFishingRodUses

    init {
        val player = Bukkit.getPlayer(fishingPlayer.uuid) ?: throw RuntimeException("Player is null on FishingMinigameManager init")

        task = object : BukkitRunnable() {
            override fun run() {
                onTick()
            }
        }.runTaskTimer(HubInteractions.plugin, 1L, 1L)
        state.onEnable()

        spawnAndRideArmorStand(player)
    }

    fun onTick() {
        val player = Bukkit.getPlayer(fishingPlayer.uuid) ?: run {
            endMinigame(MinigameEndReason.PLAYER_LEFT)
            return
        }
        //todo: if player not on premises, or if rod is dead, end game and return

        if (!armorStand.passengers.contains(player))
            armorStand.addPassenger(player)

        //todo: use predicates for this?
        if (state.stateTicksPassed >= 20 && state is FishingMinigameFishFoundState) {
            state.onDisable()
            state = FishingMinigameStartAnimState(this)
            state.onEnable()
            return
        }
        if (state.stateTicksPassed >= 1 && state is FishingMinigameStartAnimState) {
            state.onDisable()
            state = FishingMinigameGameplayState(
                this,
                rodBoxMinPosition,
                rodBoxMaxPosition,
                FishingUtil.fishingConfig.rodBoxSpeed
            )
            state.onEnable()
            return
        }
        if (state is FishingMinigameGameplayState && fishingRodUsesLeft == 0) {
            state.onDisable()
            state = FishingMinigameFailureState(this)
            state.onEnable()
            return
        }
        if ((state is FishingMinigameSuccessState || state is FishingMinigameFailureState) && state.stateTicksPassed >= 10) {
            state.onDisable()
            if (state is FishingMinigameSuccessState)
                endMinigame(MinigameEndReason.FISH_CAUGHT)
            else
                endMinigame(MinigameEndReason.RAN_OUT_OF_ATTEMPTS)
            return
        }
        if (state is FishingMinigameGameplayState && (state as FishingMinigameGameplayState).rodCast) {
            state.onDisable()
            state = FishingMinigameRodCastState(this)
            state.onEnable()
        }
        if (state is FishingMinigameRodCastState && (state as FishingMinigameRodCastState).fishCaught == false) {
            state.onDisable()
            state = FishingMinigameGameplayState( //todo: should every class just use the config statically or should I pass variables like this?
                this,
                rodBoxMinPosition,
                rodBoxMaxPosition,
                FishingUtil.fishingConfig.rodBoxSpeed
            )
            state.onEnable()
        }
        if (state is FishingMinigameRodCastState && (state as FishingMinigameRodCastState).fishCaught == true) {
            state.onDisable()
            state = FishingMinigameSuccessState(this)
            state.onEnable()
            return
        }

        state.onTick()
    }


    /**
     * Spawns an invisible armor stand and makes the player ride it
     */
    private fun spawnAndRideArmorStand(player: Player) {
        armorStand = player.location.world.spawnEntity(
            player.location.apply { this.y -= 1.13125 }, //exact height when you ride an armor stand
            EntityType.ARMOR_STAND
        ) as ArmorStand

        val key = NamespacedKey(HubInteractions.plugin, "fishing-removable") //Mark entity, for removal upon server start
        armorStand.persistentDataContainer.set(key, PersistentDataType.BOOLEAN, true)

        armorStand.isInvisible = true
        armorStand.setAI(false)
        armorStand.setGravity(false)
        armorStand.isInvulnerable = true
        armorStand.addPassenger(player)
    }

    /**
     * The reason the minigame ended.
     */
    enum class MinigameEndReason {
        /**
         * The fish was caught successfully.
         */
        FISH_CAUGHT,

        /**
         * The minigame ended in failure because the player ran out of attempts.
         */
        RAN_OUT_OF_ATTEMPTS,

        /**
         * The player left the server.
         */
        PLAYER_LEFT
    }

    /**
     * Ends the minigame and cleans everything up.
     */
    private fun endMinigame(minigameEndReason: MinigameEndReason) {
        if (minigameEndReason == MinigameEndReason.FISH_CAUGHT) {
            FishingUtil.playerData.firstOrNull { it.playerUUID == fishingPlayer.uuid }?.let {
                it.increaseFishesCaught(caughtFish.variant, 1)
                it.increaseXP(caughtFish.variant.rarity.xp)
            }
        }
        else if (minigameEndReason == MinigameEndReason.RAN_OUT_OF_ATTEMPTS) {
            FishingUtil.playerData.firstOrNull { it.playerUUID == fishingPlayer.uuid }?.let {
                it.increaseFishesUncaught(caughtFish.variant, 1)
            }
        }
        caughtFish.resumeMovement()
        armorStand.remove()
        fishingPlayer.hook.remove()
        Bukkit.getPlayer(fishingPlayer.uuid)?.let {
            it.sendTitlePart(
                TitlePart.TITLE,
                text().build()
            )
        }
        fishingPlayer.fishLakeManager.removePlayerFromFishingPlayers(fishingPlayer.uuid)
        state.onDisable()
        task.cancel()
    }
}