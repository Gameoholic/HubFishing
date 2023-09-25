package net.topstrix.hubinteractions.fishing.util

import kotlinx.coroutines.Job
import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.commands.TestCommand
import net.topstrix.hubinteractions.fishing.commands.SpawnFishCommand
import net.topstrix.hubinteractions.fishing.config.FishingConfig
import net.topstrix.hubinteractions.fishing.config.FishingFileParser
import net.topstrix.hubinteractions.fishing.data.PlayerData
import net.topstrix.hubinteractions.fishing.lake.FishLakeManager
import net.topstrix.hubinteractions.fishing.data.sql.SQLUtil
import net.topstrix.hubinteractions.fishing.listeners.PlayerJoinListener
import net.topstrix.hubinteractions.fishing.listeners.PlayerQuitListener
import org.bukkit.Bukkit
import java.util.*

object FishingUtil {

    lateinit var fishingConfig: FishingConfig


    //TODO: if rod out for too long, some fish will be attracted.
    lateinit var fishLakeManagers: List<FishLakeManager>

    val playerData = mutableListOf<PlayerData>()
    val playerDataJobs = mutableMapOf<UUID, Job>()

    fun onEnable() {
        fishingConfig = FishingFileParser.parseFile()

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

        Bukkit.getPluginManager().registerEvents(PlayerJoinListener, HubInteractions.plugin)
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener, HubInteractions.plugin)

        HubInteractions.plugin.getCommand("spawnfish")!!.setExecutor(SpawnFishCommand)

        SQLUtil.load(fishingConfig.fishVariants)

    }

    fun onTick() {
        for (fishLakeManager in fishLakeManagers) fishLakeManager.onTick()
    }

    fun onSecondPassed() {
        //Add/Remove players from fish lake managers, handle logic
        for (player in Bukkit.getOnlinePlayers()) {
            fishLakeManagers.forEach {
                val playerInFishingArea = player.location.x > it.corner1.x && player.location.x < it.corner2.x &&
                    player.location.y > it.corner1.y && player.location.y < it.corner2.y &&
                    player.location.z > it.corner1.z && player.location.z < it.corner2.z
                if (it.allPlayers.any { uuid -> uuid == player.uniqueId } && !playerInFishingArea)
                    it.removePlayer(player.uniqueId)
                //We only add player to the lake manager, if they're in its region, and if player data for this player was correctly loaded
                else if (!it.allPlayers.any { uuid -> uuid == player.uniqueId } && playerInFishingArea &&
                    playerData.any { playerData -> playerData.playerUUID == player.uniqueId })
                    it.addPlayer(player.uniqueId)
            }
        }


        for (fishLakeManager in fishLakeManagers) fishLakeManager.onSecondPassed()
    }
}