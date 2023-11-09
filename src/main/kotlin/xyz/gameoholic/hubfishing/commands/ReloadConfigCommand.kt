package xyz.gameoholic.hubfishing.commands

import xyz.gameoholic.hubfishing.config.ConfigParser
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject


object ReloadConfigCommand : CommandExecutor {
    private val plugin: HubFishingPlugin by inject()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        plugin.config = ConfigParser.parseConfig()
        return true
    }
}