package net.topstrix.hubinteractions.commands

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.config.FishingConfigParser
import net.topstrix.hubinteractions.fishing.data.LevelUtil
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.time.Duration


object TestCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return true

        FishingUtil.fishingConfig = FishingConfigParser.parseConfig()


        return true
    }
}