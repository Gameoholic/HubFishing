package xyz.gameoholic.hubfishing.commands

import xyz.gameoholic.hubfishing.config.FishingConfigParser
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject


object ReloadConfigCommand : CommandExecutor {
    private val plugin: HubFishingPlugin by inject()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        plugin.config = FishingConfigParser.parseConfig()
        return true
    }
}