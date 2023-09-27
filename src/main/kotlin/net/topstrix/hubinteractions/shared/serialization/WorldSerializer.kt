package net.topstrix.hubinteractions.shared.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World


object WorldSerializer : KSerializer<World> {
    @Serializable
    @SerialName("world")
    private class WorldSurrogate(
        val name: String
    )

    override val descriptor: SerialDescriptor = WorldSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: World) {
        val surrogate = WorldSurrogate(value.name)
        encoder.encodeSerializableValue(WorldSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): World {
        val surrogate = decoder.decodeSerializableValue(WorldSurrogate.serializer())
        return Bukkit.getWorld(surrogate.name)!!
    }
}