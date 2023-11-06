package xyz.gameoholic.hubfishing

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import xyz.gameoholic.hubfishing.commands.Test2Command
import xyz.gameoholic.hubfishing.commands.TestCommand
import xyz.gameoholic.hubfishing.util.FishingUtil
import org.bukkit.scheduler.BukkitRunnable

object HubFishing {

    lateinit var plugin: HubFishingPlugin
    lateinit var protocolManager: ProtocolManager

    fun onEnable(plugin: HubFishingPlugin) {
        this.plugin = plugin

        protocolManager = ProtocolLibrary.getProtocolManager()

        plugin.saveResource("elytraspots.yml", false)

        plugin.saveResource("config.yml", false)
        plugin.saveResource("crates.yml", false)
        plugin.saveResource("fish_lake_managers.yml", false)
        plugin.saveResource("fish_variants.yml", false)
        plugin.saveResource("menus.yml", false)
        plugin.saveResource("sql.yml", false)
        plugin.saveResource("strings.yml", false)
        plugin.saveResource("sounds.yml", false)

        plugin.getCommand("test")!!.setExecutor(TestCommand)
        plugin.getCommand("test2")!!.setExecutor(Test2Command)

        FishingUtil.onEnable()

        object : BukkitRunnable() {
            override fun run() {
                FishingUtil.onSecondPassed()
            }
        }.runTaskTimer(HubFishing.plugin, 0L, 20L)

        object : BukkitRunnable() {
            override fun run() {
                FishingUtil.onTick()
            }
        }.runTaskTimer(HubFishing.plugin, 0L, 1L)


    }

}