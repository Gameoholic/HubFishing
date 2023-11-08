package xyz.gameoholic.hubfishing

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import xyz.gameoholic.hubfishing.commands.FishingCommand
import xyz.gameoholic.hubfishing.commands.SpawnFishCommand
import xyz.gameoholic.hubfishing.commands.Test2Command
import xyz.gameoholic.hubfishing.commands.TestCommand
import xyz.gameoholic.hubfishing.config.FishingConfig
import xyz.gameoholic.hubfishing.config.FishingConfigParser
import xyz.gameoholic.hubfishing.data.PlayerData
import xyz.gameoholic.hubfishing.data.sql.SQLUtil
import xyz.gameoholic.hubfishing.displays.PlayerDisplayManager
import xyz.gameoholic.hubfishing.injection.bind
import xyz.gameoholic.hubfishing.lake.FishLakeManager
import xyz.gameoholic.hubfishing.listeners.PlayerInteractEntityListener
import xyz.gameoholic.hubfishing.listeners.PlayerJoinListener
import xyz.gameoholic.hubfishing.listeners.PlayerQuitListener
import java.util.*

class HubFishingPlugin: JavaPlugin() {

    lateinit var protocolManager: ProtocolManager
    lateinit var config: FishingConfig
    lateinit var fishLakeManagers: List<FishLakeManager>

    /**
     * Player data is guaranteed to not be null or invalid, if it's in this list.
     */
    val playerData = mutableListOf<PlayerData>()

    val playerDisplayManagers = mutableMapOf<UUID, PlayerDisplayManager>()

    override fun onEnable() {
        bind()

        protocolManager = ProtocolLibrary.getProtocolManager()

        saveResource("config.yml", false)
        saveResource("fish_lake_managers.yml", false)
        saveResource("fish_variants.yml", false)
        saveResource("menus.yml", false)
        saveResource("sql.yml", false)
        saveResource("strings.yml", false)
        saveResource("sounds.yml", false)
        config = FishingConfigParser.parseConfig()


        SQLUtil.load(config.fishVariants)
        createFishLakeManagers()
        removeOldEntities()


        getCommand("spawnfish")!!.setExecutor(SpawnFishCommand)
        getCommand("fishing")!!.setExecutor(FishingCommand)
        getCommand("test")!!.setExecutor(TestCommand)
        getCommand("test2")!!.setExecutor(Test2Command)


        Bukkit.getPluginManager().registerEvents(PlayerJoinListener, this)
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener, this)
        Bukkit.getPluginManager().registerEvents(PlayerInteractEntityListener, this)



        object : BukkitRunnable() {
            override fun run() {
                fishLakeManagers.forEach {
                    it.onSecondPassed()
                }
            }
        }.runTaskTimer(this, 0L, 20L)

        object : BukkitRunnable() {
            override fun run() {
                fishLakeManagers.forEach {
                    it.onTick()
                }
            }
        }.runTaskTimer(this, 0L, 1L)
    }

    private fun createFishLakeManagers() {
        val fishLakeManagersList = mutableListOf<FishLakeManager>()
        config.fishLakeManagersSettings.forEach {
            fishLakeManagersList += FishLakeManager(
                it.spawnCorner1,
                it.spawnCorner2,
                it.corner1,
                it.corner2,
                it.armorStandYLevel,
                it.fishAmountChances,
                it.maxFishCount,
                it.statsDisplayLocation,
                it.permissionRequiredToEnter,
                it.fishSpawningAlgorithmCurve,
                it.rankBoostDisplayLocation,
                it.surfaceYLevel
            )
        }
        fishLakeManagers = fishLakeManagersList
    }


    /**
     * In the event of a crash / server close, old fishing related entities
     * (displays, armor stands, etc.) will remain. This gets rid of them.
     */
    private fun removeOldEntities() {
        val key = NamespacedKey(this, "fishing-removable")

        //Before removing the entities, we must load the chunks.

        fishLakeManagers.forEach {
            for (x in it.corner1.x.toInt() .. it.corner2.x.toInt()) {
                for (y in it.corner1.y.toInt() .. it.corner2.y.toInt()) {
                    for (z in it.corner1.z.toInt() .. it.corner2.z.toInt()) {
                        config.world.getBlockAt(x, y, z).location.chunk.load()
                    }
                }
            }
        }

        config.world.entities.forEach {
            val container: PersistentDataContainer = it.persistentDataContainer
            if (container.has(key, PersistentDataType.BOOLEAN)) {
                it.remove()
            }
        }
    }
}