package xyz.gameoholic.hubfishing

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import xyz.gameoholic.hubfishing.commands.Test2Command
import xyz.gameoholic.hubfishing.commands.TestCommand
import xyz.gameoholic.hubfishing.elytraspots.ElytraSpotsUtil
import xyz.gameoholic.hubfishing.fishing.util.FishingUtil
import org.bukkit.scheduler.BukkitRunnable

object HubFishing {

    lateinit var plugin: HubFishingPlugin
    lateinit var protocolManager: ProtocolManager

    fun onEnable(plugin: HubFishingPlugin) {
        this.plugin = plugin

        protocolManager = ProtocolLibrary.getProtocolManager()

        plugin.saveResource("elytraspots.yml", false)

        plugin.saveResource("fishing/config.yml", false)
        plugin.saveResource("fishing/crates.yml", false)
        plugin.saveResource("fishing/fish_lake_managers.yml", false)
        plugin.saveResource("fishing/fish_variants.yml", false)
        plugin.saveResource("fishing/menus.yml", false)
        plugin.saveResource("fishing/sql.yml", false)
        plugin.saveResource("fishing/strings.yml", false)
        plugin.saveResource("fishing/sounds.yml", false)

        plugin.getCommand("test")!!.setExecutor(TestCommand)
        plugin.getCommand("test2")!!.setExecutor(Test2Command)

        ElytraSpotsUtil.onEnable()

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