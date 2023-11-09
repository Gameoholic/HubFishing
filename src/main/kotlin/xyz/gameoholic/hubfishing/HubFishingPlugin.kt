package xyz.gameoholic.hubfishing

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import xyz.gameoholic.hubfishing.commands.FishingCommand
import xyz.gameoholic.hubfishing.commands.SpawnFishCommand
import xyz.gameoholic.hubfishing.commands.Test2Command
import xyz.gameoholic.hubfishing.commands.ReloadConfigCommand
import xyz.gameoholic.hubfishing.config.FishingConfig
import xyz.gameoholic.hubfishing.config.FishingConfigParser
import xyz.gameoholic.hubfishing.data.PlayerData
import xyz.gameoholic.hubfishing.data.sql.SQLManager
import xyz.gameoholic.hubfishing.displays.PlayerDisplayManager
import xyz.gameoholic.hubfishing.injection.bind
import xyz.gameoholic.hubfishing.lake.FishLakeManager
import xyz.gameoholic.hubfishing.listeners.PlayerInteractEntityListener
import xyz.gameoholic.hubfishing.listeners.PlayerJoinListener
import xyz.gameoholic.hubfishing.listeners.PlayerQuitListener
import xyz.gameoholic.hubfishing.util.FishingUtil
import java.util.*

class HubFishingPlugin: JavaPlugin() {

    lateinit var protocolManager: ProtocolManager
        private set
    lateinit var config: FishingConfig
    lateinit var fishLakeManagers: List<FishLakeManager>
        private set
    lateinit var sqlManager: SQLManager

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
        fishLakeManagers = FishingConfigParser.getFishLakeManagers()

        sqlManager = SQLManager()

        getCommand("spawnfish")!!.setExecutor(SpawnFishCommand)
        getCommand("fishing")!!.setExecutor(FishingCommand)
        getCommand("reloadconfig")!!.setExecutor(ReloadConfigCommand)
        getCommand("test2")!!.setExecutor(Test2Command)

        Bukkit.getPluginManager().registerEvents(PlayerJoinListener, this)
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener, this)
        Bukkit.getPluginManager().registerEvents(PlayerInteractEntityListener, this)

        FishingUtil.removeOldEntities()

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

    override fun onDisable() {
        sqlManager.closeDataSource()
    }



}