package net.topstrix.hubinteractions.commands

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import net.topstrix.hubinteractions.shared.particles.LevelUpParticle
//import net.topstrix.hubinteractions.shared.particles.LevelUpParticle
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

        return true
    }
}