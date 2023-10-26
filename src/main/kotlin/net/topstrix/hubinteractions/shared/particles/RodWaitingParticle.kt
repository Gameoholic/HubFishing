package net.topstrix.hubinteractions.shared.particles

import com.github.gameoholic.partigon.particle.PartigonParticle
import com.github.gameoholic.partigon.particle.PartigonParticle.Companion.partigonParticle
import com.github.gameoholic.partigon.particle.envelope.EnvelopeGroup
import com.github.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper
import com.github.gameoholic.partigon.particle.loop.RepeatLoop
import com.github.gameoholic.partigon.util.EnvelopePair
import com.github.gameoholic.partigon.util.Utils.envelope
import org.bukkit.Location
import org.bukkit.Particle

object RodWaitingParticle {
    fun getParticle(location: Location) =
        partigonParticle(location, Particle.SPELL_INSTANT) {
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
