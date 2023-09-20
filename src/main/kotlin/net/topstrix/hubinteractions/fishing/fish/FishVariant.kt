package net.topstrix.hubinteractions.fishing.fish

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.util.Vector


data class FishVariant(
    val material: Material,
    val modelData: Int,
    val speed: Double,
    val rarity: FishRarity,
    val name: Component,
    val maxAliveTimeMin: Int,
    val maxAliveTimeMax: Int,
    val hitboxSize: Vector,
    val hitboxOffset: Vector,
    val minigameHitboxWidth: Double
)
