package xyz.gameoholic.hubfishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.gameoholic.hubfishing.fish.FishRarity
import xyz.gameoholic.hubfishing.fish.FishVariant

@Serializable
data class FishRaritiesConfig(
    @SerialName("fish-rarities") val rarities: List<FishRarity>
)