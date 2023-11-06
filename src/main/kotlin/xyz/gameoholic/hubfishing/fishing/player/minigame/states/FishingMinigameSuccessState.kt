package xyz.gameoholic.hubfishing.fishing.player.minigame.states

import xyz.gameoholic.hubfishing.HubFishing
import xyz.gameoholic.hubfishing.fishing.player.minigame.FishingMinigameManager
import xyz.gameoholic.hubfishing.fishing.player.minigame.FishingMinigameState
import xyz.gameoholic.hubfishing.fishing.player.minigame.ui.FishingMinigameSuccessRenderer
import xyz.gameoholic.hubfishing.fishing.player.minigame.ui.FishingMinigameUIRenderer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent

class FishingMinigameSuccessState(
    val longRodStartingPosition: Double, // needed for the renderer
    val longRodPosition: Double, // needed for the renderer
    val minigameManager: FishingMinigameManager
) : FishingMinigameState, Listener {
    override var stateTicksPassed: Int = 0
    private val uiRenderer: FishingMinigameUIRenderer = FishingMinigameSuccessRenderer(this)

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, HubFishing.plugin)
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