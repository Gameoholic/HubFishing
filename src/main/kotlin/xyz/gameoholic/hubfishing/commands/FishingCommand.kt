package xyz.gameoholic.hubfishing.commands

import xyz.gameoholic.hubfishing.menus.MainMenuInventory
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object FishingCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return true
        sender.openInventory(MainMenuInventory(sender.uniqueId).inventory)
        return true
    }
}