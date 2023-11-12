package xyz.gameoholic.hubfishing.commands

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import xyz.gameoholic.hubfishing.menus.MainMenuInventory
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject

object FishingCommand: CommandExecutor {
    private val plugin: HubFishingPlugin by inject()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return true
        val playerData = plugin.playerData[sender.uniqueId] ?: run {
            Bukkit.getPlayer(sender.uniqueId)?.let {
                it.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                        PlaceholderAPI.setPlaceholders(it, plugin.config.strings.fishingCommandFailure)
                    )
                )
            }
            return true
        }

        sender.openInventory(MainMenuInventory(sender.uniqueId, playerData).inventory)
        return true
    }
}