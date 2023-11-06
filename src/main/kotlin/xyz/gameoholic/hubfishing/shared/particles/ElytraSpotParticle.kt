package xyz.gameoholic.hubfishing.shared.particles

import xyz.gameoholic.partigon.particle.PartigonParticle.Companion.partigonParticleBuilder
import xyz.gameoholic.partigon.particle.envelope.EnvelopeGroup
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper.circleEnvelopeGroup
import xyz.gameoholic.partigon.particle.loop.RepeatLoop
import xyz.gameoholic.partigon.util.EnvelopePair
import xyz.gameoholic.partigon.util.EnvelopeTriple
import xyz.gameoholic.partigon.util.Utils.envelope
import xyz.gameoholic.partigon.util.rotation.RotationOptions
import xyz.gameoholic.partigon.util.rotation.RotationType
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