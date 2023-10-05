package net.topstrix.hubinteractions.fishing.commands

import net.topstrix.hubinteractions.fishing.menus.MainMenuInventory
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object FishingCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return true
        sender.openInventory(MainMenuInventory(sender.uniqueId).inventory)
        return true
    }
}