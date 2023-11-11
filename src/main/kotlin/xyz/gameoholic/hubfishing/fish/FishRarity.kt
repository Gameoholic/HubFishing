package xyz.gameoholic.hubfishing.fish

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.sound.Sound
import xyz.gameoholic.hubfishing.particles.FishParticleType
import xyz.gameoholic.hubfishing.serialization.SoundSerializer

@Serializable
data class FishRarity(
    val id: String,
    val value: Int,
    @SerialName("minigame-min-direction-duration") val minigameMinDirectionDuration: Int,
    @SerialName("minigame-max-direction-duration") val minigameMaxDirectionDuration: Int,
    @SerialName("minigame-min-speed") val minigameMinSpeed: Double,
    @SerialName("minigame-max-speed") val minigameMaxSpeed: Double,
    @SerialName("fishes-required-to-spawn-min") val fishesRequiredToSpawnMin: Int,
    @SerialName("fishes-required-to-spawn-max") val fishesRequiredToSpawnMax: Int,
    @SerialName("alive-time-min") val aliveTimeMin: Int,
    @SerialName("alive-time-max") val aliveTimeMax: Int,
    val xp: Int,
    @SerialName("display-name") val displayName: String,
    @SerialName("fish-particle-type") val fishParticleType: FishParticleType,
    @SerialName("fish-spawn-message") val fishSpawnMessage: String? = null,
    @SerialName("fish-spawn-sound") val fishSpawnSound: @Serializable(with = SoundSerializer::class) Sound? = null,
)



