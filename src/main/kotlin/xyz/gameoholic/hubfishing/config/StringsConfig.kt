package xyz.gameoholic.hubfishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StringsConfig(
    @SerialName("fish-found-message") val fishFoundMessage: String,
    @SerialName("level-up-message") val levelUpMessage: String,
    @SerialName("xp-gained-action-bar-message") val xpGainedActionBarMessage: String,
    @SerialName("passed-time-restriction-message") val passedTimeRestrictionMessage: String,
    @SerialName("time-restriction-strike-message") val timeRestrictionStrikeMessage: String,
    @SerialName("minigame-leave-message") val minigameLeaveMessage: String,
    @SerialName("stats-display-content") val statsDisplayContent: String,
    @SerialName("rank-boost-display-none-content") val rankBoostDisplayNoneContent: String,
    @SerialName("rank-boost-display-boosted-content") val rankBoostDisplayBoostedContent: String,
    @SerialName("fishing-command-failure") val fishingCommandFailure: String,
)
