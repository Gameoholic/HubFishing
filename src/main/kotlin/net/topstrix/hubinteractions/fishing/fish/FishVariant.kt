package net.topstrix.hubinteractions.fishing.fish

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.topstrix.hubinteractions.shared.serialization.LocationSerializer
import net.topstrix.hubinteractions.shared.serialization.VectorSerializer
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
    @SerialName("alive-time-min") val aliveTimeMin: Int,
    @SerialName("alive-time-max") val aliveTimeMax: Int,
    @SerialName("hitbox-size") val hitboxSize: @Serializable(with = VectorSerializer::class) Vector,
    @SerialName("hitbox-offset") val hitboxOffset: @Serializable(with = VectorSerializer::class) Vector,
    @SerialName("minigame-hitbox-width") val minigameHitboxWidth: Double
)
