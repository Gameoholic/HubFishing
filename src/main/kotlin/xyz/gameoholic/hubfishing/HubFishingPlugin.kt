package xyz.gameoholic.hubfishing

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import xyz.gameoholic.hubfishing.commands.Test2Command
import xyz.gameoholic.hubfishing.commands.TestCommand
import xyz.gameoholic.hubfishing.injection.bind
import xyz.gameoholic.hubfishing.util.FishingUtil

class HubFishingPlugin: JavaPlugin() {


    lateinit var protocolManager: ProtocolManager

    override fun onEnable() {
        bind()

        protocolManager = ProtocolLibrary.getProtocolManager()

        saveResource("config.yml", false)
        saveResource("crates.yml", false)
        saveResource("fish_lake_managers.yml", false)
        saveResource("fish_variants.yml", false)
        saveResource("menus.yml", false)
        saveResource("sql.yml", false)
        saveResource("strings.yml", false)
        saveResource("sounds.yml", false)

        getCommand("test")!!.setExecutor(TestCommand)
        getCommand("test2")!!.setExecutor(Test2Command)

        FishingUtil.onEnable()

        object : BukkitRunnable() {
            override fun run() {
                FishingUtil.onSecondPassed()
            }
        }.runTaskTimer(this, 0L, 20L)

        object : BukkitRunnable() {
            override fun run() {
                FishingUtil.onTick()
            }
        }.runTaskTimer(this, 0L, 1L)
    }
}