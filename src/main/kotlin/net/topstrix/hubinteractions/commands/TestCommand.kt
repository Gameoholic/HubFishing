package net.topstrix.hubinteractions.commands

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import net.topstrix.hubinteractions.HubInteractions
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.Vector


object TestCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return true

        return true
    }
}