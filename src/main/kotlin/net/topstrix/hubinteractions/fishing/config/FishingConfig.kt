package net.topstrix.hubinteractions.fishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.topstrix.hubinteractions.elytraspots.ElytraSpot
import net.topstrix.hubinteractions.fishing.lake.FishLakeManager
import net.topstrix.hubinteractions.fishing.util.LoggerUtil


@Serializable
data class FishingConfig(
    @SerialName("log-level") val logLevel: LoggerUtil.LogLevel,
    @SerialName("fish-lake-managers") val fishLakeManagersSettings: List<FishLakeManagerSettings>,
    @SerialName("water-amount") val waterAmount: Int,
    @SerialName("water-character") val waterCharacter: Char,
    @SerialName("water-character-height") val waterCharacterHeight: Int,
    @SerialName("fish-character") val fishCharacter: Char,
    @SerialName("fish-character-height") val fishCharacterHeight: Int,
    //todo: better config?

)


