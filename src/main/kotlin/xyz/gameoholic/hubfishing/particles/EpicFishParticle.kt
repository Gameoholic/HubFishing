package xyz.gameoholic.hubfishing.particles

import xyz.gameoholic.partigon.particle.PartigonParticle
import xyz.gameoholic.partigon.particle.envelope.EnvelopeGroup
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper
import xyz.gameoholic.partigon.particle.loop.RepeatLoop
import xyz.gameoholic.partigon.util.EnvelopePair
import xyz.gameoholic.partigon.util.Utils.envelope
import org.bukkit.Location
import org.bukkit.Particle

object EpicFishParticle {
    fun getParticle(location: Location): PartigonParticle {
        fun builder() =
            PartigonParticle.partigonParticleBuilder(location, Particle.BUBBLE_POP) {
                envelopes = listOf(
                    *CircleEnvelopeWrapper.circleEnvelopeGroup(
                        EnvelopeGroup.EnvelopeGroupType.POSITION,
                        EnvelopePair(0.5.envelope, 0.0.envelope),
                        EnvelopePair(0.0.envelope, (-0.5).envelope),
                        CircleEnvelopeWrapper.CircleDirection.RIGHT,
                        RepeatLoop(40),
                    ).getEnvelopes().toTypedArray(),
                    *CircleEnvelopeWrapper.circleEnvelopeGroup(
                        EnvelopeGroup.EnvelopeGroupType.OFFSET,
                        EnvelopePair((-0.5).envelope, 0.0.envelope),
                        EnvelopePair(0.0.envelope, 0.5.envelope),
                        CircleEnvelopeWrapper.CircleDirection.RIGHT,
                        RepeatLoop(40),
                    ).getEnvelopes().toTypedArray(),
                )
                animationFrameAmount = 40
                animationInterval = 20
                count = 0.0.envelope
                extra = 0.2.envelope
            }

        val particle = builder().build()
        //builder().apply { this.particleType = Particle.BUBBLE_POP }.build().start()
        return particle
    }
}