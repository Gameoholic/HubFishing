package xyz.gameoholic.hubfishing.elytraspots

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.gameoholic.hubfishing.shared.serialization.LocationSerializer
import org.bukkit.Location


@Serializable
data class ElytraSpot(
    val location: @Serializable(with = LocationSerializer::class) Location,
    @SerialName("levitation-duration") val levitationDuration: Int,
    @SerialName("levitation-amplifier") val levitationAmplifier: Int,
    @SerialName("radius") val radius: Int
)

