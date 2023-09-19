package net.topstrix.hubinteractions.fishing.player

import net.topstrix.hubinteractions.fishing.fish.Fish
import net.topstrix.hubinteractions.fishing.lake.FishLakeManager
import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameManager
import org.bukkit.Particle
import org.bukkit.entity.FishHook
import java.util.*

class FishingPlayer(
    val fishLakeManager: FishLakeManager,
    val uuid: UUID,
    val hook: FishHook,
    private var hookCooldown: Int
) {

    var fishingState: FishingPlayerState = FishingPlayerState.ROD_WAITING
        private set
    fun onTick() {
        if (hook.hookedEntity != null) hook.hookedEntity = null
        if (hook.state != FishHook.HookState.BOBBING) return //TODO: isn't this laggy though?
        if (hookCooldown > 0) {
            hookCooldown--
            return
        }
        //If hook is ready:
        if (fishingState == FishingPlayerState.ROD_WAITING)
            fishingState = FishingPlayerState.ROD_READY
        //If fish was caught and is in the middle of an animation, we don't want to do anything with the rod yet:
        if (fishingState != FishingPlayerState.ROD_READY)
            return

        val caughtFishes = fishLakeManager.fishes.filter { !it.caught && it.checkHitboxCollision(hook.location) }

        //If multiple fish are caught, get the one with the rarest rarity
        if (caughtFishes.isNotEmpty()) {
            val caughtFish = caughtFishes
                .first { it.variant.rarity.value == caughtFishes.maxOf { caughtFish -> caughtFish.variant.rarity.value } }
            onCatchFish(caughtFish)
            return
        }
        displayHookParticles()
    }

    private fun onCatchFish(caughtFish: Fish) {
        fishingState = FishingPlayerState.FISH_CAUGHT
        caughtFish.onCatch()
        FishingMinigameManager(this, caughtFish)
    }

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
