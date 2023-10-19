package net.topstrix.hubinteractions.fishing.player.minigame

import com.github.gameoholic.partigon.Partigon
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.title.TitlePart
import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.crate.CrateUtil
import net.topstrix.hubinteractions.fishing.fish.Fish
import net.topstrix.hubinteractions.fishing.player.FishingPlayer
import net.topstrix.hubinteractions.fishing.player.minigame.states.*
import net.topstrix.hubinteractions.fishing.player.minigame.states.util.FishMovementManager
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import net.topstrix.hubinteractions.shared.particles.LevelUpParticle
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
        FishingUtil.fishingConfig.waterAreaStartPosition + caughtFish.variant.minigameCharacterHeight,
        FishingUtil.fishingConfig.waterAreaStartPosition - caughtFish.variant.minigameCharacterHeight + FishingUtil.fishingConfig.waterAreaLengthPixels,
        caughtFish
    )

    /** The armor stand the player is mounted on */
    private lateinit var armorStand: ArmorStand
    lateinit var textDisplay: TextDisplay

    /** The rod box's min position in UI pixels, from the right */
    private val rodBoxMinPosition = FishingUtil.fishingConfig.waterAreaStartPosition

    /** The rod box's max position in UI pixels, from the right */
    private val rodBoxMaxPosition =
        FishingUtil.fishingConfig.waterAreaStartPosition + FishingUtil.fishingConfig.waterAreaLengthPixels

    /** The rod box's position in UI pixels, from the right */
    var rodBoxPosition = rodBoxMinPosition + (rodBoxMaxPosition - rodBoxMinPosition) / 2

    var fishingRodUsesLeft = FishingUtil.fishingConfig.maxFishingRodUses

    var waterAnimationFrame = 0
    private var waterAnimationDelay = 0 // When reaches the water animation speed (2 for example), will animate the next frame of the water animation and reset to 0.

    var miniFishingRodFrames = mutableListOf<Int>() // The animation frame to be used on each mini fishing rod. If set to -1, will not be animated and instead use the 'unused' mini rod character
    private var miniFishingRodsAnimationDelay = 0

    init {
        val player = Bukkit.getPlayer(fishingPlayer.uuid)
            ?: throw RuntimeException("Player is null on FishingMinigameManager init")

        task = object : BukkitRunnable() {
            override fun run() {
                onTick()
            }
        }.runTaskTimer(HubInteractions.plugin, 1L, 1L)
        state.onEnable()

        for (i in 0 until FishingUtil.fishingConfig.maxFishingRodUses)
            miniFishingRodFrames.add(-1) // Because none of the rods are used, they do not have an animation frame

        spawnAndRideArmorStand(player)
    }

    fun onTick() {
        val player = Bukkit.getPlayer(fishingPlayer.uuid) ?: run {
            endMinigame(MinigameEndReason.PLAYER_LOGGED_OFF)
            return
        }
        // If player left premises of lake, or hook is dead, end game
        if (!fishingPlayer.fishLakeManager.fishingPlayers.any { it.uuid == fishingPlayer.uuid } || fishingPlayer.hook.isDead) {
            endMinigame(MinigameEndReason.PLAYER_LOGGED_OFF)
            return
        }

        // Water animation:
        waterAnimationDelay++
        if (waterAnimationDelay >= FishingUtil.fishingConfig.waterAnimationSpeed) {
            waterAnimationDelay = 0
            waterAnimationFrame++
            if (waterAnimationFrame >= FishingUtil.fishingConfig.waterCharacters.size)
                waterAnimationFrame = 0
        }


        // Mini rods animation: (only one at a time)
        for (i in 0 until FishingUtil.fishingConfig.maxFishingRodUses) {
            if ((FishingUtil.fishingConfig.maxFishingRodUses - i) - 1 == fishingRodUsesLeft) { // We check if fishing rod [i] is used, AND is not on the last frame of the used animation (otherwise we just leave it)
                if (miniFishingRodFrames[i] == FishingUtil.fishingConfig.miniRodUsedCharacters.size - 1) {  // If on last frame, we leave this rod and stop the animation
                    miniFishingRodsAnimationDelay = -1
                    break
                }
                if (miniFishingRodsAnimationDelay == -1 || miniFishingRodsAnimationDelay >= FishingUtil.fishingConfig.miniRodsAnimationSpeed) { // If the rod just broke, OR another frame should be animated, animate it.
                    miniFishingRodFrames[i]++
                    miniFishingRodsAnimationDelay = 0
                }
                if (miniFishingRodsAnimationDelay != -1)
                    miniFishingRodsAnimationDelay++
                break
            }
        }




        //todo: use predicates for this?

        // If player is no longer riding armor stand, end the game.
        if (!armorStand.passengers.contains(player)) {
            armorStand.addPassenger(player)
            state.onDisable()
            state = FishingMinigameFailureState(this, FishingMinigameFailureState.FailureReason.PLAYER_SURRENDERED)
            state.onEnable()
            return
        }
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
            state = FishingMinigameFailureState(this, FishingMinigameFailureState.FailureReason.RAN_OUT_OF_ATTEMPTS)
            state.onEnable()
            return
        }
        if ((state is FishingMinigameSuccessState || state is FishingMinigameFailureState)
            && state.stateTicksPassed >= FishingUtil.fishingConfig.fishingMinigameSuccessAnimationLength) {
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
            return
        }
        if (state is FishingMinigameRodCastState && (state as FishingMinigameRodCastState).fishCaught == false) {
            state.onDisable()
            state =
                FishingMinigameGameplayState( //todo: should every class just use the config statically or should I pass variables like this?
                    this,
                    rodBoxMinPosition,
                    rodBoxMaxPosition,
                    FishingUtil.fishingConfig.rodBoxSpeed
                )
            state.onEnable()
            return
        }
        if (state is FishingMinigameRodCastState && (state as FishingMinigameRodCastState).fishCaught == true) {
            state.onDisable()
            state = FishingMinigameSuccessState(
                (state as FishingMinigameRodCastState).longRodStartingPosition,
                (state as FishingMinigameRodCastState).longRodPosition,
                this
            )
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

        val key =
            NamespacedKey(HubInteractions.plugin, "fishing-removable") //Mark entity, for removal upon server start
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
        PLAYER_LOGGED_OFF,
        /**
         * The player chose to leave the fishing minigame.
         */
        PLAYER_SURRENDERED
    }

    /**
     * Ends the minigame and cleans everything up.
     */
    private fun endMinigame(minigameEndReason: MinigameEndReason) {
        when (minigameEndReason) {
            MinigameEndReason.FISH_CAUGHT -> {
                onSuccessfulFish()
                caughtFish.remove()
            }
            MinigameEndReason.RAN_OUT_OF_ATTEMPTS -> {
                FishingUtil.playerData.firstOrNull { it.playerUUID == fishingPlayer.uuid }?.let {
                    it.increaseFishesUncaught(caughtFish.variant, 1)
                }
                caughtFish.caught = false
            }
            MinigameEndReason.PLAYER_SURRENDERED -> {
                FishingUtil.playerData.firstOrNull { it.playerUUID == fishingPlayer.uuid }?.let {
                    it.increaseFishesUncaught(caughtFish.variant, 1)
                }
                caughtFish.caught = false
            }
            else -> {
                caughtFish.caught = false
            }
        }
        armorStand.remove()
        Bukkit.getPlayer(fishingPlayer.uuid)?.let {
            it.sendTitlePart(
                TitlePart.TITLE,
                text().build()
            )
        }
        fishingPlayer.fishLakeManager.removePlayerFromFishingPlayers(fishingPlayer.uuid)
        state.onDisable()
        task.cancel()
        fishingPlayer.hook.remove()
    }

    /**
     * Called when the player succeeded in the fishing minigame.
     */
    private fun onSuccessfulFish() {
        FishingUtil.playerData.firstOrNull { it.playerUUID == fishingPlayer.uuid }?.let { playerData ->
            playerData.increaseFishesCaught(caughtFish.variant, 1)
            val oldLevel = playerData.levelData!!.level
            playerData.increaseXP(caughtFish.variant.rarity.xp)
            val newLevel = playerData.levelData!!.level
            Bukkit.getPlayer(fishingPlayer.uuid)?.let {
                it.sendActionBar(
                    MiniMessage.miniMessage().deserialize(
                        PlaceholderAPI.setPlaceholders(it, FishingUtil.fishingConfig.XPGainedActionBarMessage),
                        Placeholder.component("xp", text(caughtFish.variant.rarity.xp.toString()))
                    )
                )
                if (newLevel > oldLevel) {
                    it.sendMessage(
                        MiniMessage.miniMessage().deserialize(
                            PlaceholderAPI.setPlaceholders(it, FishingUtil.fishingConfig.levelUpMessage),
                            Placeholder.component("new_level", text(newLevel.toString())),
                            Placeholder.component("old_level", text(oldLevel.toString()))
                        )
                    )

                    it.playSound(FishingUtil.fishingConfig.levelUpSound, Sound.Emitter.self())

                    val particle = LevelUpParticle.getParticle()
                    particle.start()
                    object : BukkitRunnable() {
                        override fun run() {
                            particle.stop()
                            this.cancel()
                        }
                    }.runTaskTimer(Partigon.plugin, 50L, 1L)
                }
            }

        }
        CrateUtil.attemptGiveShard(fishingPlayer.uuid, caughtFish.variant)
    }


}