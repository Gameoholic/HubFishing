package net.topstrix.hubinteractions.elytraspots

import com.github.gameoholic.partigon.particle.PartigonParticle
import com.github.gameoholic.partigon.particle.PartigonParticle.Companion.partigonParticleBuilder
import com.github.gameoholic.partigon.particle.envelope.Envelope
import com.github.gameoholic.partigon.particle.envelope.EnvelopeGroup
import com.github.gameoholic.partigon.particle.envelope.TrigonometricEnvelope
import com.github.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper
import com.github.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper.circleEnvelopeGroup
import com.github.gameoholic.partigon.particle.envelope.wrapper.CurveEnvelopeWrapper
import com.github.gameoholic.partigon.particle.envelope.wrapper.CurveEnvelopeWrapper.curveEnvelope
import com.github.gameoholic.partigon.particle.loop.BounceLoop
import com.github.gameoholic.partigon.particle.loop.RepeatLoop
import com.github.gameoholic.partigon.util.EnvelopePair
import com.github.gameoholic.partigon.util.EnvelopeTriple
import com.github.gameoholic.partigon.util.Utils.envelope
import com.github.gameoholic.partigon.util.rotation.RotationOptions
import com.github.gameoholic.partigon.util.rotation.RotationType
import org.bukkit.Location
import org.bukkit.Particle

object ElytraSpotParticle {
    fun startParticle(location: Location) {
        // Define basic animation
        fun builder() =
            partigonParticleBuilder(location, Particle.END_ROD) {
                envelopes = listOf(
                    *circleEnvelopeGroup(
                        EnvelopeGroup.EnvelopeGroupType.POSITION,
                        EnvelopePair((-3.0).envelope, 0.0.envelope),
                        EnvelopePair(0.0.envelope, (3.0).envelope),
                        CircleEnvelopeWrapper.CircleDirection.RIGHT,
                        RepeatLoop(100),
                    ).getEnvelopes().toTypedArray(),

                    *circleEnvelopeGroup(
                        EnvelopeGroup.EnvelopeGroupType.OFFSET,
                        EnvelopePair((3.0).envelope, 0.0.envelope),
                        EnvelopePair(0.0.envelope, (-3.0).envelope),
                        CircleEnvelopeWrapper.CircleDirection.RIGHT,
                        RepeatLoop(100),
                    ).getEnvelopes().toTypedArray(),

                    curveEnvelope(
                        Envelope.PropertyType.POS_Y,
                        0.0.envelope, 4.0.envelope,
                        CurveEnvelopeWrapper.CurveOrientation.BELOW,
                        BounceLoop(200),
                    )
                )
                extra = 0.05.envelope
                count = 0.envelope
            }

        builder().build().start()

        // Mirror all particles:
        builder().apply {
            this.rotationOptions = listOf(
                RotationOptions(EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                    180.0.envelope, RotationType.X_AXIS),
            )
        }.build().start()
        builder().apply {
            this.rotationOptions = listOf(
                RotationOptions(EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                    180.0.envelope, RotationType.Z_AXIS)
            )
        }.build().start()
        builder().apply {
            this.rotationOptions = listOf(
                RotationOptions(EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                    180.0.envelope, RotationType.X_AXIS),
                RotationOptions(EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                    180.0.envelope, RotationType.Z_AXIS)
            )
        }.build().start()
        builder().apply {
            this.rotationOptions = listOf(
                RotationOptions(EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                    270.0.envelope, RotationType.Y_AXIS),
                RotationOptions(EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                    180.0.envelope, RotationType.Z_AXIS)
            )
        }.build().start()
        builder().apply {
            this.rotationOptions = listOf(
                RotationOptions(EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                    270.0.envelope, RotationType.Y_AXIS),
                RotationOptions(EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                    180.0.envelope, RotationType.X_AXIS)
            )
        }.build().start()

    }
}