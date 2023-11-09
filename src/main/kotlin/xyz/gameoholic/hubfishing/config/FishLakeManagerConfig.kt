package xyz.gameoholic.hubfishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.gameoholic.hubfishing.fish.FishVariant
import xyz.gameoholic.hubfishing.lake.FishLakeManagerOptions

@Serializable
data class FishLakeManagerConfig(
    @SerialName("fish-lake-managers") val fishLakeManagersOptions: List<FishLakeManagerOptions>,
)