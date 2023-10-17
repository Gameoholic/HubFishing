package net.topstrix.hubinteractions.commands

import com.github.gameoholic.partigon.Partigon
import net.topstrix.hubinteractions.shared.particles.LevelUpParticle
import org.bukkit.Particle
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

object Test2Command : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val particle = LevelUpParticle.getParticle()
        particle.start()
        object: BukkitRunnable() {
            override fun run() {
                particle.stop()
                this.cancel()
            }
        }.runTaskTimer(Partigon.plugin, 50L, 1L)
        return true
    }
}