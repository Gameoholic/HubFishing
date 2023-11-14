@file:UseSerializers(SoundSerializer::class)

package xyz.gameoholic.hubfishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.sound.Sound
import xyz.gameoholic.hubfishing.serialization.SoundSerializer

@Serializable

data class SoundsConfig(
    @SerialName("time-restriction-strike-sound") val timeRestrictionStrikeSound: Sound,
    @SerialName("minigame-failure-sound") val minigameFailureSound: Sound,
    @SerialName("passed-time-restriction-sound") val passedTimeRestrictionSound: Sound,
    @SerialName("fish-found-sound") val fishFoundSound: Sound,
    @SerialName("fishing-minigame-miss-sound") val fishingMinigameMissSound: Sound,
    @SerialName("level-up-sound") val levelUpSound: Sound,
    @SerialName("fishing-minigame-catch-sound") val fishingMinigameCatchSound: Sound,

)
