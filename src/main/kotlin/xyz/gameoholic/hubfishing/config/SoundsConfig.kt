package xyz.gameoholic.hubfishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.sound.Sound
import xyz.gameoholic.hubfishing.serialization.SoundSerializer

@Serializable
data class SoundsConfig(
    @SerialName("time-restriction-strike-sound") val timeRestrictionStrikeSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("minigame-failure-sound") val minigameFailureSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("passed-time-restriction-sound") val passedTimeRestrictionSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("fish-found-sound") val fishFoundSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("fishing-minigame-miss-sound") val fishingMinigameMissSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("level-up-sound") val levelUpSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("fishing-minigame-catch-sound") val fishingMinigameCatchSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("legendary-fish-spawn-sound") val legendaryFishSpawnSound: @Serializable(with = SoundSerializer::class) Sound,

)
