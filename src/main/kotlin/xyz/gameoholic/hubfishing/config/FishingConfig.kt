package xyz.gameoholic.hubfishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.World
import xyz.gameoholic.hubfishing.serialization.WorldSerializer
import xyz.gameoholic.hubfishing.util.LoggerUtil

@Serializable
data class FishingConfig(
    // General
    @SerialName("log-level") val logLevel: LoggerUtil.LogLevel,
    @SerialName("world") val world: @Serializable(with = WorldSerializer::class) World,
    // Leveling
    @SerialName("level-initial-xp-requirement") val levelInitialXPRequirement: Int,
    @SerialName("level-xp-requirement-growth") val levelXPRequirementGrowth: Int,
    @SerialName("level-xp-requirement-growth-multiplier") val levelXPRequirementGrowthMultiplier: Double,
    @SerialName("level-growth-delay") val levelGrowthDelay: Int,
    @SerialName("level-xp-requirement-growth-cap") val levelXPRequirementGrowthCap: Int,
    // Rank boost
    @SerialName("rank-boost-amount") val rankBoostAmount: Double,
    @SerialName("rank-boost-permission") val rankBoostPermission: String,
    // Gameplay
    @SerialName("hook-cooldown") val hookCooldown: Int,
    )
