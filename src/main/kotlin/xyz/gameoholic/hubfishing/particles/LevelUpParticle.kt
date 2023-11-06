package xyz.gameoholic.hubfishing.particles

import xyz.gameoholic.partigon.particle.PartigonParticle
import xyz.gameoholic.partigon.particle.PartigonParticle.Companion.partigonParticle
import xyz.gameoholic.partigon.particle.envelope.Envelope
import xyz.gameoholic.partigon.particle.envelope.EnvelopeGroup
import xyz.gameoholic.partigon.particle.envelope.TrigonometricEnvelope
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper.circleEnvelopeGroup
import xyz.gameoholic.partigon.particle.loop.RepeatLoop
import xyz.gameoholic.partigon.util.EnvelopePair
import xyz.gameoholic.partigon.util.Utils.envelope
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