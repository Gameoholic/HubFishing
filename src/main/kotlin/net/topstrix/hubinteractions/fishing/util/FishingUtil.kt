package net.topstrix.hubinteractions.fishing.util

import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.commands.FishingCommand
import net.topstrix.hubinteractions.fishing.commands.SpawnFishCommand
import net.topstrix.hubinteractions.fishing.config.FishingConfig
import net.topstrix.hubinteractions.fishing.config.FishingConfigParser
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
import org.bukkit.util.Vector
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
        fishingConfig = FishingConfigParser.parseConfig()

        removeOldEntities()

        val fishLakeManagersList = mutableListOf<FishLakeManager>()
        for (fishLakeManagerSettings in fishingConfig.fishLakeManagersSettings) {
            fishLakeManagersList += FishLakeManager(fishLakeManagerSettings.spawnCorner1,
                fishLakeManagerSettings.spawnCorner2,
                fishLakeManagerSettings.corner1,
                fishLakeManagerSettings.corner2,
                fishLakeManagerSettings.armorStandYLevel,
                fishLakeManagerSettings.fishAmountChances,
                fishLakeManagerSettings.maxFishCount,
                fishLakeManagerSettings.statsDisplayLocation,
                fishLakeManagerSettings.permissionRequiredToEnter,
                fishLakeManagerSettings.fishSpawningAlgorithmCurve,
                fishLakeManagerSettings.rankBoostDisplayLocation
            )
        }
        fishLakeManagers = fishLakeManagersList

        Bukkit.getPluginManager().registerEvents(PlayerJoinListener, HubInteractions.plugin)
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener, HubInteractions.plugin)

        HubInteractions.plugin.getCommand("spawnfish")!!.setExecutor(SpawnFishCommand)
        HubInteractions.plugin.getCommand("fishing")!!.setExecutor(FishingCommand)

        SQLUtil.load(fishingConfig.fishVariants, fishingConfig.crates)
    }

    fun onTick() {
        for (fishLakeManager in fishLakeManagers) fishLakeManager.onTick()
    }

    fun onSecondPassed() {
        //Add/Remove players from fish lake managers, handle logic
        fishLakeManagers.forEach {
            Bukkit.getOnlinePlayers().forEach {
                player ->
                val playerInFishingArea = player.location.x > it.corner1.x && player.location.x < it.corner2.x &&
                    player.location.y > it.corner1.y && player.location.y < it.corner2.y &&
                    player.location.z > it.corner1.z && player.location.z < it.corner2.z
                if (it.allPlayers.any { uuid -> uuid == player.uniqueId } && !playerInFishingArea)
                    it.removePlayer(player.uniqueId)

                //We only add player to the lake manager, if they're in its region, and if player data for this player was correctly loaded
                else if (!it.allPlayers.any { uuid -> uuid == player.uniqueId } && playerInFishingArea &&
                    playerData.any { playerData -> playerData.playerUUID == player.uniqueId }) {
                    if (player.hasPermission(it.permissionRequiredToEnter)) {
                        it.addPlayer(player.uniqueId)
                    }
                    //If player doesn't have permission to enter lake, launch them away
                    else {
                        val rnd = Random()
                        val velocity = Vector(rnd.nextDouble(-1.0, 1.0), 1.0, rnd.nextDouble(-1.0, 1.0))
                        player.velocity = velocity
                    }
                }

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

        //Before removing the entities, we must load the chunks.

        //Fish lakes have not been initialized yet, so we go over the settings and load the chunks for every block
        fishingConfig.fishLakeManagersSettings.forEach {
            for (x in it.corner1.x.toInt() .. it.corner2.x.toInt()) {
                for (y in it.corner1.y.toInt() .. it.corner2.y.toInt()) {
                    for (z in it.corner1.z.toInt() .. it.corner2.z.toInt()) {
                        fishingConfig.world.getBlockAt(x, y, z).location.chunk.load()
                    }
                }
            }
        }

        fishingConfig.world.entities.forEach {
            val container: PersistentDataContainer = it.persistentDataContainer
            if (container.has(key, PersistentDataType.BOOLEAN)) {
                it.remove()
            }
        }
    }
}