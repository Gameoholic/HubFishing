package xyz.gameoholic.hubfishing.fish

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.gameoholic.hubfishing.serialization.VectorSerializer
import org.bukkit.Material
import org.bukkit.util.Vector


@Serializable
data class FishVariant(
    val id: String,
    val material: Material,
    @SerialName("model-data") val modelData: Int,
    val speed: Double,
    val rarity: FishRarity,
    val name: String,
    @SerialName("hitbox-size") val hitboxSize: @Serializable(with = VectorSerializer::class) Vector,
    @SerialName("hitbox-offset") val hitboxOffset: @Serializable(with = VectorSerializer::class) Vector,
    @SerialName("minigame-hitbox-width") val minigameHitboxWidth: Double,
    @SerialName("minigame-character") val minigameCharacter: Char,
    @SerialName("minigame-character-height") val minigameCharacterHeight: Int,
    @SerialName("menu-material") val menuMaterial: Material,
    @SerialName("menu-model-data") val menuModelData: Int,
)
