package net.topstrix.hubinteractions.fishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.topstrix.hubinteractions.shared.serialization.LocationSerializer
import org.bukkit.Location

@Serializable
data class FishLakeManagerSettings(
    @SerialName("spawn-corner-1") val spawnCorner1: @Serializable(with = LocationSerializer::class) Location,
    @SerialName("spawn-corner-2") val spawnCorner2: @Serializable(with = LocationSerializer::class) Location,
    @SerialName("corner-1") val corner1: @Serializable(with = LocationSerializer::class) Location,
    @SerialName("corner-2") val corner2: @Serializable(with = LocationSerializer::class) Location,
    @SerialName("armor-stand-y-level") val armorStandYLevel: Double,
    @SerialName("fish-amount-chances") val fishAmountChances: HashMap<Int, Double>,
    @SerialName("max-fish-count") val maxFishCount: Int,
    @SerialName("stats-display-location") val statsDisplayLocation: @Serializable(with = LocationSerializer::class) Location,
    @SerialName("permission-required-to-enter") val permissionRequiredToEnter: String,
)
