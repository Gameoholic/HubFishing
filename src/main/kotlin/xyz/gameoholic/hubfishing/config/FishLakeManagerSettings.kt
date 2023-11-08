@file:UseSerializers(LocationSerializer::class)
package xyz.gameoholic.hubfishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import xyz.gameoholic.hubfishing.serialization.LocationSerializer
import org.bukkit.Location

@Serializable
data class FishLakeManagerSettings(
    @SerialName("spawn-corner-1") val spawnCorner1: Location,
    @SerialName("spawn-corner-2") val spawnCorner2: Location,
    @SerialName("corner-1") val corner1: Location,
    @SerialName("corner-2") val corner2: Location,
    @SerialName("armor-stand-y-level") val armorStandYLevel: Double,
    @SerialName("fish-amount-chances") val fishAmountChances: HashMap<Int, Double>,
    @SerialName("max-fish-count") val maxFishCount: Int,
    @SerialName("stats-display-location") val statsDisplayLocation: Location,
    @SerialName("rank-boost-display-location") val rankBoostDisplayLocation: Location,
    @SerialName("permission-required-to-enter") val permissionRequiredToEnter: String,
    @SerialName("fish-spawning-algorithm-curve") val fishSpawningAlgorithmCurve: Double,
    @SerialName("surface-y-level") val surfaceYLevel: Double,
)
