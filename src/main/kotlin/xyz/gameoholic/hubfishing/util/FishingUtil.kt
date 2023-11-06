package xyz.gameoholic.hubfishing.util

import xyz.gameoholic.hubfishing.HubFishing
import xyz.gameoholic.hubfishing.commands.FishingCommand
import xyz.gameoholic.hubfishing.commands.SpawnFishCommand
import xyz.gameoholic.hubfishing.config.FishingConfig
import xyz.gameoholic.hubfishing.config.FishingConfigParser
import xyz.gameoholic.hubfishing.data.PlayerData
import xyz.gameoholic.hubfishing.data.sql.SQLUtil
import xyz.gameoholic.hubfishing.displays.PlayerDisplayManager
import xyz.gameoholic.hubfishing.lake.FishLakeManager
import xyz.gameoholic.hubfishing.listeners.PlayerInteractEntityListener
import xyz.gameoholic.hubfishing.listeners.PlayerJoinListener
import xyz.gameoholic.hubfishing.listeners.PlayerQuitListener
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
                fishLakeManagerSettings.rankBoostDisplayLocation,
                fishLakeManagerSettings.surfaceYLevel
            )
        }
        fishLakeManagers = fishLakeManagersList

        Bukkit.getPluginManager().registerEvents(PlayerJoinListener, HubFishing.plugin)
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener, HubFishing.plugin)
        Bukkit.getPluginManager().registerEvents(PlayerInteractEntityListener, HubFishing.plugin)

        HubFishing.plugin.getCommand("spawnfish")!!.setExecutor(SpawnFishCommand)
        HubFishing.plugin.getCommand("fishing")!!.setExecutor(FishingCommand)

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
                if (it.allPlayers.any { lakePlayer -> lakePlayer.uuid == player.uniqueId } && !playerInFishingArea)
                    it.removePlayer(player.uniqueId)

                //We only add player to the lake manager, if they're in its region, and if player data for this player was correctly loaded
                else if (!it.allPlayers.any { lakePlayer -> lakePlayer.uuid == player.uniqueId } && playerInFishingArea &&
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
        val key = NamespacedKey(HubFishing.plugin, "fishing-removable")

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