package net.topstrix.hubinteractions.fishing.util

import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.commands.SpawnFishCommand
import net.topstrix.hubinteractions.fishing.config.FishingConfig
import net.topstrix.hubinteractions.fishing.config.FishingFileParser
import net.topstrix.hubinteractions.fishing.data.PlayerData
import net.topstrix.hubinteractions.fishing.data.sql.SQLUtil
import net.topstrix.hubinteractions.fishing.displays.PlayerDisplayManager
import net.topstrix.hubinteractions.fishing.lake.FishLakeManager
import net.topstrix.hubinteractions.fishing.listeners.PlayerJoinListener
import net.topstrix.hubinteractions.fishing.listeners.PlayerQuitListener
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.*


object FishingUtil {

    lateinit var fishingConfig: FishingConfig

    //TODO: if rod out for too long, some fish will be attracted.
    lateinit var fishLakeManagers: List<FishLakeManager>

    /**
     * Player data is guaranteed to not be null or invalid, if it's in this list.
     */
    val playerData = mutableListOf<PlayerData>()

    val playerDisplayManagers = mutableMapOf<UUID, PlayerDisplayManager>()

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
                fishLakeManagerSettings.maxFishCount,
                fishLakeManagerSettings.statsDisplayLocation)
        }
        fishLakeManagers = fishLakeManagersList

        Bukkit.getPluginManager().registerEvents(PlayerJoinListener, HubInteractions.plugin)
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener, HubInteractions.plugin)

        HubInteractions.plugin.getCommand("spawnfish")!!.setExecutor(SpawnFishCommand)

        SQLUtil.load(fishingConfig.fishVariants)

        removeOldEntities()
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

    /**
     * In the event of a crash / server close, old fishing related entities
     * (displays, armor stands, etc.) will remain. This gets rid of them.
     */
    private fun removeOldEntities() {
        val key = NamespacedKey(HubInteractions.plugin, "fishing-removable")

        fishingConfig.world.entities.forEach {
            val container: PersistentDataContainer = it.persistentDataContainer
            if (container.has(key, PersistentDataType.BOOLEAN)) {
                it.remove()
            }
        }
    }
}