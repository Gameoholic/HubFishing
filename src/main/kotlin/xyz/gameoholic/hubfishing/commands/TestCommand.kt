package xyz.gameoholic.hubfishing.commands

import xyz.gameoholic.hubfishing.config.FishingConfigParser
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject


object TestCommand : CommandExecutor {
    private val plugin: HubFishingPlugin by inject()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return true

        plugin.config = FishingConfigParser.parseConfig()

        return true
    }
}