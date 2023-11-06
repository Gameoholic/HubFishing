package xyz.gameoholic.hubfishing.player

import xyz.gameoholic.partigon.particle.PartigonParticle
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import xyz.gameoholic.hubfishing.HubFishing
import xyz.gameoholic.hubfishing.fishing.fish.Fish
import xyz.gameoholic.hubfishing.fishing.lake.FishLakeManager
import xyz.gameoholic.hubfishing.fishing.player.minigame.FishingMinigameManager
import xyz.gameoholic.hubfishing.fishing.util.FishingUtil
import xyz.gameoholic.hubfishing.fishing.util.LoggerUtil
import xyz.gameoholic.hubfishing.shared.particles.RodCatchParticle
import xyz.gameoholic.hubfishing.shared.particles.RodWaitingParticle
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.entity.FishHook
import org.bukkit.scheduler.BukkitRunnable
import xyz.gameoholic.hubfishing.fishing.player.FishingPlayerState
import java.util.*

/**
 * Represents a player who is in a fish lake, and is currently fishing with a rod, either
 * waiting for a fish to be caught, or is in the fishing minigame
 * @param fishLakeManager The lake manager where the player is at
 * @param uuid The uuid of the player
 * @param hook The hook of the fishing rod, that the player has cast
 * @param hookCooldown How many ticks to wait before the fishing rod can catch fishes
 */
class FishingPlayer(
    val fishLakeManager: FishLakeManager,
    val uuid: UUID,
    val hook: FishHook,
    private var hookCooldown: Int
) {
    var fishingState: FishingPlayerState = FishingPlayerState.ROD_WAITING
        private set

    /** How many ticks have passed since last second. When it gets to 20, resets to 0 and updates playtime by 1 */
    private var ticksPassedSinceLastSecond = 0

    private var hookReadyParticle: PartigonParticle? = null

    fun onTick() {
        //Update fishing playtime, if rod is out
        ticksPassedSinceLastSecond++
        if (ticksPassedSinceLastSecond >= 20) {
            FishingUtil.playerData.first {it.playerUUID == uuid}.increasePlaytime(1)
            ticksPassedSinceLastSecond = 0
        }

        if (hook.hookedEntity != null) hook.hookedEntity = null
        if (hook.state != FishHook.HookState.BOBBING) return
        if (hookCooldown > 0) {
            hookCooldown--
            return
        }
        //If hook is ready:
        if (fishingState == FishingPlayerState.ROD_WAITING)
            fishingState = FishingPlayerState.ROD_READY
        //We only proceed if fishing rod is ready, and hasn't caught any fish
        if (fishingState != FishingPlayerState.ROD_READY)
            return
        if (hookReadyParticle == null) {
            hookReadyParticle = RodWaitingParticle.getParticle(hook)
            hookReadyParticle?.start()
        }

        //Fish find detection
        val foundFishes = fishLakeManager.fishes.filter { !it.caught && it.checkHitboxCollision(hook.location) }

        //If multiple fish are found, get the one with the rarest rarity
        if (foundFishes.isNotEmpty()) {
            val caughtFish = foundFishes
                .first { it.variant.rarity.value == foundFishes.maxOf { caughtFish -> caughtFish.variant.rarity.value } }
            onFindFish(caughtFish)
            return
        }
    }

    private fun onFindFish(caughtFish: Fish) {
        LoggerUtil.debug("Player $uuid caught fish $caughtFish")
        fishingState = FishingPlayerState.FISH_CAUGHT
        // Send message & play sound:
        Bukkit.getPlayer(uuid)?.let {
            it.sendMessage(MiniMessage.miniMessage().deserialize(
                PlaceholderAPI.setPlaceholders(it, FishingUtil.fishingConfig.fishFoundMessage),
                Placeholder.component("rarity",
                    MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(it, caughtFish.variant.rarity.displayName))
                ))
            )
            it.playSound(FishingUtil.fishingConfig.fishFoundSound, Sound.Emitter.self())
        }

        hookReadyParticle?.stop()
        hookReadyParticle = null

        caughtFish.caught = true
        val lakePlayer = fishLakeManager.allPlayers.first { it.uuid == uuid }
        lakePlayer.minigameManager = FishingMinigameManager(this, lakePlayer, caughtFish)

        val catchParticle = RodCatchParticle.getParticle(hook.location.apply { this.y += 0.25 })
        catchParticle.start()
        object: BukkitRunnable() {
            override fun run() {
                catchParticle.stop()
            }
        }.runTask(HubFishing.plugin)
    }

    /**
     * Called when the fishing player is removed.
     */
    fun onRemove() {
        hookReadyParticle?.stop()
        hookReadyParticle = null
    }


}
