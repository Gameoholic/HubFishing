package net.topstrix.hubinteractions.shared.particles

import com.github.gameoholic.partigon.particle.PartigonParticle
import com.github.gameoholic.partigon.particle.envelope.EnvelopeGroup
import com.github.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper
import com.github.gameoholic.partigon.particle.loop.RepeatLoop
import com.github.gameoholic.partigon.util.EnvelopePair
import com.github.gameoholic.partigon.util.Utils.envelope
import org.bukkit.Particle
import org.bukkit.entity.Entity

object LegendaryFishParticle {
    fun getParticle(entity: Entity): PartigonParticle {
        fun builder() =
            PartigonParticle.partigonParticleBuilder(entity.location, Particle.SPELL_INSTANT) {
                this.entity = entity
                envelopes = listOf(
                    *CircleEnvelopeWrapper.circleEnvelopeGroup(
                        EnvelopeGroup.EnvelopeGroupType.POSITION,
                        EnvelopePair(0.0.envelope, 0.0.envelope),
                        EnvelopePair(1.5.envelope, 1.5.envelope),
                        CircleEnvelopeWrapper.CircleDirection.RIGHT,
                        RepeatLoop(40),
                    ).getEnvelopes().toTypedArray(),
                )
                animationFrameAmount = 40
                animationInterval = 20
            }

        val particle = builder().build()
        //builder().apply { this.particleType = Particle.BUBBLE_POP }.build().start()
        return particle
    }
}