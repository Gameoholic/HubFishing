package xyz.gameoholic.hubfishing.particles

import xyz.gameoholic.partigon.particle.PartigonParticle
import xyz.gameoholic.partigon.particle.envelope.EnvelopeGroup
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper
import xyz.gameoholic.partigon.particle.loop.RepeatLoop
import xyz.gameoholic.partigon.util.EnvelopePair
import xyz.gameoholic.partigon.util.Utils.envelope
import org.bukkit.Location
import org.bukkit.Particle

object FishCatchParticle {
    fun getParticle(location: Location) =
        PartigonParticle.partigonParticle(location, Particle.END_ROD) {
            envelopes = listOf(
                *CircleEnvelopeWrapper.circleEnvelopeGroup(
                    EnvelopeGroup.EnvelopeGroupType.POSITION,
                    EnvelopePair((-0.05).envelope, 0.0.envelope),
                    EnvelopePair(0.0.envelope, 0.05.envelope),
                    CircleEnvelopeWrapper.CircleDirection.RIGHT,
                    RepeatLoop(10),
                ).getEnvelopes().toTypedArray(),
                *CircleEnvelopeWrapper.circleEnvelopeGroup(
                    EnvelopeGroup.EnvelopeGroupType.OFFSET,
                    EnvelopePair((-0.3).envelope, 0.0.envelope),
                    EnvelopePair(0.0.envelope, 0.3.envelope),
                    CircleEnvelopeWrapper.CircleDirection.RIGHT,
                    RepeatLoop(10),
                ).getEnvelopes().toTypedArray(),
            )

            count = 0.envelope
            extra = 0.15.envelope
            animationFrameAmount = 10
            animationInterval = 100
        }
}