package net.topstrix.hubinteractions

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import net.topstrix.hubinteractions.commands.Test2Command
import net.topstrix.hubinteractions.commands.TestCommand
import net.topstrix.hubinteractions.elytraspots.ElytraSpotsUtil
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.scheduler.BukkitRunnable

object HubInteractions {

    lateinit var plugin: HubInteractionsPlugin
    lateinit var protocolManager: ProtocolManager

    fun onEnable(plugin: HubInteractionsPlugin) {
        this.plugin = plugin

        protocolManager = ProtocolLibrary.getProtocolManager()

        plugin.saveResource("config.yml", false)
        plugin.saveResource("elytraspots.yml", false)

        plugin.saveResource("fishing/config.yml", true)
        plugin.saveResource("fishing/crates.yml", true)
        plugin.saveResource("fishing/fish_lake_managers.yml", true)
        plugin.saveResource("fishing/fish_variants.yml", true)
        plugin.saveResource("fishing/menus.yml", true)
        plugin.saveResource("fishing/sql.yml", true)
        plugin.saveResource("fishing/messages.yml", true)
        plugin.saveResource("fishing/sounds.yml", true)

        plugin.getCommand("test")!!.setExecutor(TestCommand)
        plugin.getCommand("test2")!!.setExecutor(Test2Command)

        ElytraSpotsUtil.onEnable()

        FishingUtil.onEnable()

        object : BukkitRunnable() {
            override fun run() {
                FishingUtil.onSecondPassed()
            }
        }.runTaskTimer(HubInteractions.plugin, 0L, 20L)

        object : BukkitRunnable() {
            override fun run() {
                FishingUtil.onTick()
            }
        }.runTaskTimer(HubInteractions.plugin, 0L, 1L)


    }

}