package net.topstrix.hubinteractions.commands

import com.github.gameoholic.partigon.Partigon
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import net.topstrix.hubinteractions.shared.particles.LevelUpParticle
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.time.Duration

object Test2Command : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
//        sender.let {
//            it.sendTitlePart(
//                TitlePart.TIMES,
//                Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(10000), Duration.ofMillis(0))
//            )
//            it.sendTitlePart(
//                TitlePart.TITLE,
//                MiniMessage.miniMessage().deserialize(args!![0]!!)
//            )
//        }

        LevelUpParticle.getParticle().start()
        return true
    }
}