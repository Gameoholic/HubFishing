package xyz.gameoholic.hubfishing.player.minigame.states

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.minimessage.MiniMessage
import xyz.gameoholic.hubfishing.player.minigame.FishingMinigameManager
import xyz.gameoholic.hubfishing.player.minigame.FishingMinigameState
import xyz.gameoholic.hubfishing.player.minigame.ui.FishingMinigameFailureUIRenderer
import xyz.gameoholic.hubfishing.player.minigame.ui.FishingMinigameUIRenderer
import xyz.gameoholic.hubfishing.util.FishingUtil
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject

class FishingMinigameFailureState(val minigameManager: FishingMinigameManager, val failureReason: FailureReason): FishingMinigameState, Listener {
    private val plugin: HubFishingPlugin by inject()

    override var stateTicksPassed = 0
    private val uiRenderer: FishingMinigameUIRenderer = FishingMinigameFailureUIRenderer(this)

    val longRodStartingPosition = FishingUtil.fishingConfig.bigRodPosition - FishingUtil.fishingConfig.bigRodCharacterHeight + 4.0 //TODo: I'm not sure why it's 4.0.
    val longRodPosition = minigameManager.rodBoxPosition - FishingUtil.fishingConfig.rodBoxCharacterHeight / 2

    enum class FailureReason {
        /**
         * Player has run out of attempts, and their last action was using the rod
         */
        RAN_OUT_OF_ATTEMPTS,
        /**
         * Player has run out of attempts, and their last attempt was lost because of the timer
         */
        RAN_OUT_OF_TIME,
        /**
         * Player has left the fishing minigame voluntarily.
         */
        PLAYER_SURRENDERED
    }

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, plugin)
        Bukkit.getPlayer(minigameManager.fishingPlayer.uuid)?.let {
            if (failureReason == FailureReason.PLAYER_SURRENDERED)
                it.sendMessage(MiniMessage.miniMessage().deserialize(
                    PlaceholderAPI.setPlaceholders(it, FishingUtil.fishingConfig.minigameLeaveMessage))
                )
            it.playSound(FishingUtil.fishingConfig.minigameFailureSound)
        }

    }

    override fun onTick() {
        stateTicksPassed++

        uiRenderer.render()
    }

    override fun onDisable() {
        PlayerFishEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)
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
}