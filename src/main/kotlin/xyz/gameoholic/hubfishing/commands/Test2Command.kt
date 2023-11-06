package xyz.gameoholic.hubfishing.commands

import xyz.gameoholic.partigon.particle.PartigonParticle
import xyz.gameoholic.partigon.particle.envelope.Envelope
import xyz.gameoholic.partigon.particle.envelope.EnvelopeGroup
import xyz.gameoholic.partigon.particle.envelope.TrigonometricEnvelope
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper
import xyz.gameoholic.partigon.particle.loop.RepeatLoop
import xyz.gameoholic.partigon.util.EnvelopePair
import xyz.gameoholic.partigon.util.Utils.envelope
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import xyz.gameoholic.hubfishing.fishing.util.FishingUtil
import xyz.gameoholic.hubfishing.shared.particles.LevelUpParticle
import xyz.gameoholic.hubfishing.shared.particles.RodCatchParticle
import xyz.gameoholic.hubfishing.shared.particles.RodWaitingParticle
//import xyz.gameoholic.hubfishing.shared.particles.LevelUpParticle
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
        val player = sender as? Player ?: return true
        player?.let {
            it.sendTitlePart(
                TitlePart.TIMES,
                Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(10000), Duration.ofMillis(0))
            )
            it.sendTitlePart(
                TitlePart.TITLE,
                MiniMessage.miniMessage().deserialize(args!![0]!!)
            )
        }

        return true
    }
}