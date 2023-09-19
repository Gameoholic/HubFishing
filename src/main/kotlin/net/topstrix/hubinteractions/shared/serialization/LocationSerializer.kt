package net.topstrix.hubinteractions.shared.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.Location


object LocationSerializer : KSerializer<Location> {
    @Serializable
    @SerialName("location")
    private class LocationSurrogate(
        val world: String,
        val x: Double,
        val y: Double,
        val z: Double,
        val pitch: Float = 0f,
        val yaw: Float = 0f
    )

    override val descriptor: SerialDescriptor = LocationSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: Location) {
        val surrogate = LocationSurrogate(value.world.name, value.x, value.y, value.z, value.pitch, value.yaw)
        encoder.encodeSerializableValue(LocationSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Location {
        val surrogate = decoder.decodeSerializableValue(LocationSurrogate.serializer())
        return Location(
            Bukkit.getWorld(surrogate.world),
            surrogate.x,
            surrogate.y,
            surrogate.z,
            surrogate.yaw,
            surrogate.pitch
        )
    }
}