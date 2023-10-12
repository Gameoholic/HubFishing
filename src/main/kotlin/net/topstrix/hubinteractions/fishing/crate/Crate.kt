package net.topstrix.hubinteractions.fishing.crate

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class Crate(
    val id: String,
    @SerialName("display-name") val displayName: String,
    @SerialName("amount-of-shards-to-craft") val amountOfShardsToCraft: Int,
    @SerialName("command-to-craft") val commandToCraft: String,
    val chance: Double,
    @SerialName("menu-material") val menuMaterial: Material

)
