package xyz.gameoholic.hubfishing.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.Sound.Source

object SoundSerializer : KSerializer<Sound> {
    @Serializable
    @SerialName("sound")
    private class SoundSurrogate(
        val name: String,
        val source: Source,
        val volume: Float,
        val pitch: Float
    )

    override val descriptor: SerialDescriptor = SoundSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: Sound) {
        val surrogate = SoundSurrogate(value.name().value(), value.source(), value.volume(), value.pitch())
        encoder.encodeSerializableValue(SoundSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Sound {
        val surrogate = decoder.decodeSerializableValue(SoundSurrogate.serializer())
        return Sound.sound(
            Key.key(surrogate.name),
            surrogate.source,
            surrogate.volume,
            surrogate.pitch
        )
    }
}