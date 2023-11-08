package xyz.gameoholic.hubfishing.player.minigame.states

import xyz.gameoholic.hubfishing.player.minigame.FishingMinigameManager
import xyz.gameoholic.hubfishing.player.minigame.FishingMinigameState
import xyz.gameoholic.hubfishing.player.minigame.ui.FishingMinigameMissUIRenderer
import xyz.gameoholic.hubfishing.player.minigame.ui.FishingMinigameUIRenderer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject

class FishingMinigameMissState(
    val minigameManager: FishingMinigameManager
) : FishingMinigameState, Listener {
    private val plugin: HubFishingPlugin by inject()

    override var stateTicksPassed = 0
    private val uiRenderer: FishingMinigameUIRenderer = FishingMinigameMissUIRenderer(this)

    val longRodStartingPosition = plugin.config.bigRodPosition - plugin.config.bigRodCharacterHeight + 4.0 //TODo: I'm not sure why it's 4.0.
    val longRodPosition = minigameManager.rodBoxPosition - plugin.config.rodBoxCharacterHeight / 2

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, plugin)


    }

    override fun onTick() {
        stateTicksPassed++

        minigameManager.fishMovementManager.updateFishPosition()

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