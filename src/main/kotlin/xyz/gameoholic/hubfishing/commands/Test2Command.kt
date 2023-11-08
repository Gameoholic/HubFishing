package xyz.gameoholic.hubfishing.commands

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
//import xyz.gameoholic.hubfishing.particles.LevelUpParticle
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Duration

object Test2Command : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return true
        sender.sendTitlePart(
            TitlePart.TIMES,
            Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(10000), Duration.ofMillis(0))
        )
        sender.sendTitlePart(
            TitlePart.TITLE,
            MiniMessage.miniMessage().deserialize(args!![0]!!)
        )

        return true
    }
}