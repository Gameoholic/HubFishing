package net.topstrix.hubinteractions.fishing.player

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.topstrix.hubinteractions.fishing.fish.Fish
import net.topstrix.hubinteractions.fishing.lake.FishLakeManager
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameManager
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import net.topstrix.hubinteractions.fishing.util.LoggerUtil
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.entity.FishHook
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
        displayHookParticles()

        //Fish catch detection
        val caughtFishes = fishLakeManager.fishes.filter { !it.caught && it.checkHitboxCollision(hook.location) }

        //If multiple fish are caught, get the one with the rarest rarity
        if (caughtFishes.isNotEmpty()) {
            val caughtFish = caughtFishes
                .first { it.variant.rarity.value == caughtFishes.maxOf { caughtFish -> caughtFish.variant.rarity.value } }
            onCatchFish(caughtFish)
            return
        }
    }

    private fun onCatchFish(caughtFish: Fish) {
        LoggerUtil.debug("Player $uuid caught fish $caughtFish")
        fishingState = FishingPlayerState.FISH_CAUGHT
        // Send message & play sound:
        Bukkit.getPlayer(uuid)?.let {
            it.sendMessage(MiniMessage.miniMessage().deserialize(
                PlaceholderAPI.setPlaceholders(it, FishingUtil.fishingConfig.fishCatchMessage),
                Placeholder.component("rarity",
                    MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(it, caughtFish.variant.rarity.displayName))
                ))
            )
            it.playSound(FishingUtil.fishingConfig.fishCatchSound, Sound.Emitter.self())
        }
        caughtFish.caught = true
        FishingMinigameManager(this, caughtFish)
    }

    /**
     * Displays the fishing rod particles, when the rod is ready.
     */
    private fun displayHookParticles() {
        hook.world.spawnParticle(
            Particle.END_ROD,
            hook.location,
            1,
            0.1,
            0.1,
            0.1,
            0.1
        )
    }


}
