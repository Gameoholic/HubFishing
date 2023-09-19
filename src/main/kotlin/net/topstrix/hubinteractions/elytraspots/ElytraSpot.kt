package net.topstrix.hubinteractions.elytraspots

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.topstrix.hubinteractions.shared.serialization.LocationSerializer
import org.bukkit.Location


@Serializable
data class ElytraSpot(
    val location: @Serializable(with = LocationSerializer::class) Location,
    @SerialName("levitation-duration") val levitationDuration: Int,
    @SerialName("levitation-amplifier") val levitationAmplifier: Int,
    @SerialName("radius") val radius: Int
)

