package xyz.gameoholic.hubfishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.gameoholic.hubfishing.fish.FishVariant

@Serializable
data class FishVariantsConfig(
    @SerialName("fish-variants") val variants: List<FishVariant>
)
