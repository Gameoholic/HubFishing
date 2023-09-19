package net.topstrix.hubinteractions.fishing.util

import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.lake.FishLakeManager
import net.topstrix.hubinteractions.fishing.listeners.PlayerFishListener
import net.topstrix.hubinteractions.fishing.listeners.PlayerInteractListener
import org.bukkit.Bukkit
import org.bukkit.Location

object FishingUtil {
    //TODO: if rod out for too long, some fish will be attracted.
    val fishLakeManagers = listOf<FishLakeManager>(
        FishLakeManager(
            Location(Bukkit.getWorld("world"), 17.0, 68.8, -293.0),
            Location(Bukkit.getWorld("world"), 21.0, 68.8, -289.0),
            Location(Bukkit.getWorld("world"), 15.0, 64.0, -295.0),
            Location(Bukkit.getWorld("world"), 24.0, 71.0, -286.0),
            67.0,
            10
        )
    )

    val fishingConfig: FishingConfig = FishingConfig(LoggerUtil.LogLevel.DEBUG)


    fun onEnable() {
        Bukkit.getPluginManager().registerEvents(PlayerFishListener, HubInteractions.plugin)
        Bukkit.getPluginManager().registerEvents(PlayerInteractListener, HubInteractions.plugin)
    }

    fun onTick() {
        for (fishLakeManager in fishLakeManagers) fishLakeManager.onTick()
    }

    fun onSecondPassed() {
        //Add/Remove players from fish lake managers, handle logic
        for (player in Bukkit.getOnlinePlayers()) {
            for (fishLakeManager in fishLakeManagers) {
                val playerInFishingArea = player.location.x > fishLakeManager.corner1.x && player.location.x < fishLakeManager.corner2.x &&
                    player.location.y > fishLakeManager.corner1.y && player.location.y < fishLakeManager.corner2.y &&
                    player.location.z > fishLakeManager.corner1.z && player.location.z < fishLakeManager.corner2.z
                if (fishLakeManager.allPlayers.any { it == player.uniqueId } && !playerInFishingArea)
                    fishLakeManager.removePlayer(player.uniqueId)
                else if (!fishLakeManager.allPlayers.any { it == player.uniqueId } && playerInFishingArea)
                    fishLakeManager.addPlayer(player.uniqueId)
            }
        }


        for (fishLakeManager in fishLakeManagers) fishLakeManager.onSecondPassed()
    }
}