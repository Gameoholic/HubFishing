package net.topstrix.hubinteractions

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.topstrix.hubinteractions.commands.Test2Command
import net.topstrix.hubinteractions.commands.TestCommand
import net.topstrix.hubinteractions.elytraspots.ElytraSpotsUtil
import net.topstrix.hubinteractions.elytraspots.config.ElytraSpotsFileParser
import net.topstrix.hubinteractions.fishing.config.FishingFileParser
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.scheduler.BukkitRunnable

object HubInteractions {

    lateinit var plugin: HubInteractionsPlugin
    lateinit var protocolManager: ProtocolManager

    fun onEnable(plugin: HubInteractionsPlugin) {
        this.plugin = plugin

        protocolManager = ProtocolLibrary.getProtocolManager();

        plugin.saveResource("config.yml", true)
        plugin.saveResource("elytraspots.yml", true)
        plugin.saveResource("fishing.yml", true)

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