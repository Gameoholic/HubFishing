package xyz.gameoholic.hubfishing.particles

import xyz.gameoholic.partigon.particle.PartigonParticle.Companion.partigonParticle
import xyz.gameoholic.partigon.particle.envelope.EnvelopeGroup
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper
import xyz.gameoholic.partigon.particle.loop.RepeatLoop
import xyz.gameoholic.partigon.util.EnvelopePair
import xyz.gameoholic.partigon.util.Utils.envelope
import org.bukkit.Particle
import org.bukkit.entity.Entity

object RodWaitingParticle {
    fun getParticle(entity: Entity) =
        partigonParticle(entity.location, Particle.SPELL_INSTANT) {
            this.entity = entity
            envelopes = listOf(
                *CircleEnvelopeWrapper.circleEnvelopeGroup(
                    EnvelopeGroup.EnvelopeGroupType.POSITION,
                    EnvelopePair((-0.33).envelope, 0.0.envelope),
                    EnvelopePair(0.0.envelope, 0.33.envelope),
                    CircleEnvelopeWrapper.CircleDirection.RIGHT,
                    RepeatLoop(40),
                ).getEnvelopes().toTypedArray(),
            )
        }
}
