package xyz.gameoholic.hubfishing.elytraspots.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.gameoholic.hubfishing.elytraspots.ElytraSpot


@Serializable
data class ElytraSpotsConfig(
    @SerialName("allow-spot-reuse") val allowSpotReuse: Boolean,
    @SerialName("elytra-activate-message") val elytraActivateMessage: String,
    @SerialName("elytra-deactivate-message") val elytraDeactivateMessage: String,
    @SerialName("elytra-activate-sound") val elytraActivateSound: String,
    @SerialName("elytra-deactivate-sound") val elytraDeactivateSound: String,
    @SerialName("elytra-spots") val elytraSpots: List<ElytraSpot>
)
