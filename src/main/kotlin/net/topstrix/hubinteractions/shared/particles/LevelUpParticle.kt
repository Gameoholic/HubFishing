package net.topstrix.hubinteractions.shared.particles

import com.github.gameoholic.partigon.particle.PartigonParticle
import com.github.gameoholic.partigon.particle.PartigonParticle.Companion.partigonParticle
import com.github.gameoholic.partigon.particle.envelope.Envelope
import com.github.gameoholic.partigon.particle.envelope.EnvelopeGroup
import com.github.gameoholic.partigon.particle.envelope.TrigonometricEnvelope
import com.github.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper
import com.github.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper.circleEnvelopeGroup
import com.github.gameoholic.partigon.particle.loop.RepeatLoop
import com.github.gameoholic.partigon.util.EnvelopePair
import com.github.gameoholic.partigon.util.Utils.envelope
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle

object LevelUpParticle {
    fun getParticle(location: Location): PartigonParticle {
        val framesPerTick = 10
        val circlePoint1 = TrigonometricEnvelope(
            Envelope.PropertyType.NONE,
            1.5.envelope,
            0.0.envelope,
            TrigonometricEnvelope.TrigFunc.COS,
            RepeatLoop(50 * framesPerTick)
        )
        val circlePoint2 = TrigonometricEnvelope(
            Envelope.PropertyType.NONE,
            (-1.5).envelope,
            0.0.envelope,
            TrigonometricEnvelope.TrigFunc.COS,
            RepeatLoop(50 * framesPerTick)
        )
        val particle = partigonParticle(location, Particle.END_ROD) {
            envelopes = listOf(
                *circleEnvelopeGroup(
                    EnvelopeGroup.EnvelopeGroupType.POSITION,
                    EnvelopePair(circlePoint2, 0.0.envelope),
                    EnvelopePair(0.0.envelope, circlePoint1),
                    CircleEnvelopeWrapper.CircleDirection.RIGHT,
                    RepeatLoop(framesPerTick),
                ).getEnvelopes().toTypedArray(),

                *circleEnvelopeGroup(
                    EnvelopeGroup.EnvelopeGroupType.OFFSET,
                    EnvelopePair(circlePoint2, 0.0.envelope),
                    EnvelopePair(0.0.envelope, circlePoint1),
                    CircleEnvelopeWrapper.CircleDirection.RIGHT,
                    RepeatLoop(10 * framesPerTick),
                ).getEnvelopes().toTypedArray(),
            )
            animationFrameAmount = framesPerTick
            animationInterval = 1
            count = 0.envelope
            extra = 0.1.envelope
        }

        return particle
    }

}