package xyz.gameoholic.hubfishing.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.util.Vector


object VectorSerializer : KSerializer<Vector> {
    @Serializable
    @SerialName("vector")
    private class VectorSurrogate(
        val x: Double,
        val y: Double,
        val z: Double
    )

    override val descriptor: SerialDescriptor = VectorSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: Vector) {
        val surrogate = VectorSurrogate(value.x, value.y, value.z)
        encoder.encodeSerializableValue(VectorSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Vector {
        val surrogate = decoder.decodeSerializableValue(VectorSurrogate.serializer())
        return Vector(
            surrogate.x,
            surrogate.y,
            surrogate.z,
        )
    }
}