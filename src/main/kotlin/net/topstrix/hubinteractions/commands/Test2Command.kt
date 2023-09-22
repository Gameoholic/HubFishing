package net.topstrix.hubinteractions.commands

import com.github.gameoholic.partigon.particle.PartigonParticle
import com.github.gameoholic.partigon.particle.envelope.CurveEnvelope
import com.github.gameoholic.partigon.particle.envelope.Envelope
import com.github.gameoholic.partigon.particle.envelope.LineEnvelope
import com.github.gameoholic.partigon.particle.loop.RepeatLoop
import com.github.gameoholic.partigon.particle.loop.ReverseLoop
import org.bukkit.Particle
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.Vector

object Test2Command : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        Vector(2.0, 3.0, 1.0)
        PartigonParticle((sender as Player).location,
            Particle.HEART,
            listOf(
                CurveEnvelope(
                    Envelope.PropertyType.POS_X,
                    -2.0,
                    LineEnvelope(Envelope.PropertyType.POS_X, 0, 2, ReverseLoop(40)),
                    CurveEnvelope.TrigFunc.SIN,
                    RepeatLoop(80),
                    2.0,
                    1.0,
                    false),
                CurveEnvelope(
                    Envelope.PropertyType.POS_Z,
                    LineEnvelope(Envelope.PropertyType.POS_X, 0, -2, ReverseLoop(40)),
                    2.0,
                    CurveEnvelope.TrigFunc.COS,
                    RepeatLoop(80),
                    2.0,
                    1.0,
                    false)
            ),
            1,
            Vector(0.0, 0.0, 0.0)
        ).start()
        return true
    }
}