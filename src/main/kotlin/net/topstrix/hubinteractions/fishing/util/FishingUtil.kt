package net.topstrix.hubinteractions.fishing.util

import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.config.FishingConfig
import net.topstrix.hubinteractions.fishing.lake.FishLakeManager
import net.topstrix.hubinteractions.fishing.listeners.PlayerFishListener
import net.topstrix.hubinteractions.fishing.listeners.PlayerInteractListener
import org.bukkit.Bukkit
import org.bukkit.Location

object FishingUtil {

    lateinit var fishingConfig: FishingConfig


    //TODO: if rod out for too long, some fish will be attracted.
    lateinit var fishLakeManagers: List<FishLakeManager>

    fun onEnable() {
        val fishLakeManagersList = mutableListOf<FishLakeManager>()
        for (fishLakeManagerSettings in fishingConfig.fishLakeManagersSettings) {
            fishLakeManagersList += FishLakeManager(fishLakeManagerSettings.spawnCorner1,
                fishLakeManagerSettings.spawnCorner2,
                fishLakeManagerSettings.corner1,
                fishLakeManagerSettings.corner2,
                fishLakeManagerSettings.armorStandYLevel,
                fishLakeManagerSettings.fishAmountChances,
                fishLakeManagerSettings.maxFishCount)
        }
        fishLakeManagers = fishLakeManagersList

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