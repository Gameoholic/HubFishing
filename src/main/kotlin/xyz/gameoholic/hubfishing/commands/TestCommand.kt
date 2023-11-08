package xyz.gameoholic.hubfishing.commands

import xyz.gameoholic.hubfishing.config.FishingConfigParser
import xyz.gameoholic.hubfishing.util.FishingUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


object TestCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return true

        FishingUtil.fishingConfig = FishingConfigParser.parseConfig()

        return true
    }
}