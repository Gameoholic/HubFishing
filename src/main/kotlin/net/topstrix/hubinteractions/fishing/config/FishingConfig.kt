package net.topstrix.hubinteractions.fishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.topstrix.hubinteractions.elytraspots.ElytraSpot
import net.topstrix.hubinteractions.fishing.fish.FishVariant
import net.topstrix.hubinteractions.fishing.lake.FishLakeManager
import net.topstrix.hubinteractions.fishing.util.LoggerUtil
import net.topstrix.hubinteractions.shared.serialization.LocationSerializer
import net.topstrix.hubinteractions.shared.serialization.WorldSerializer
import org.bukkit.World


@Serializable
data class FishingConfig(
    @SerialName("log-level") val logLevel: LoggerUtil.LogLevel,
    @SerialName("fish-lake-managers") val fishLakeManagersSettings: List<FishLakeManagerSettings>,
    @SerialName("fish-variants") val fishVariants: List<FishVariant>,
    @SerialName("water-amount") val waterAmount: Int,
    @SerialName("water-character") val waterCharacter: Char,
    @SerialName("water-character-height") val waterCharacterHeight: Int,
    @SerialName("rod-box-speed") val rodBoxSpeed: Double,
    @SerialName("rod-box-character") val rodBoxCharacter: Char,
    @SerialName("rod-box-character-height") val rodBoxCharacterHeight: Int,
    @SerialName("mini-rod-character") val miniRodCharacter: Char,
    @SerialName("mini-rod-character-height") val miniRodCharacterHeight: Int,
    @SerialName("mini-rod-used-character") val miniRodUsedCharacter: Char,
    @SerialName("mini-rod-used-character-height") val miniRodUsedCharacterHeight: Int,
    @SerialName("big-rod-character-height") val bigRodCharacterHeight: Int,
    @SerialName("big-rod-characters") val bigRodCharacters: List<Char>,
    @SerialName("long-rod-character-height") val longRodCharacterHeight: Int,
    @SerialName("long-rod-character") val longRodCharacter: Char,
    @SerialName("max-fishing-rod-uses") val maxFishingRodUses: Int,
    @SerialName("common-fish-minigame-min-direction-duration") val commonFishMinigameMinDirectionDuration: Int,
    @SerialName("common-fish-minigame-max-direction-duration") val commonFishMinigameMaxDirectionDuration: Int,
    @SerialName("common-fish-minigame-speed") val commonFishMinigameSpeed: Double,
    @SerialName("common-fish-alive-time-min") val commonFishAliveTimeMin: Int,
    @SerialName("common-fish-alive-time-max") val commonFishAliveTimeMax: Int,
    @SerialName("rare-fish-minigame-min-direction-duration") val rareFishMinigameMinDirectionDuration: Int,
    @SerialName("rare-fish-minigame-max-direction-duration") val rareFishMinigameMaxDirectionDuration: Int,
    @SerialName("rare-fish-minigame-speed") val rareFishMinigameSpeed: Double,
    @SerialName("rare-fish-fishes-required-to-spawn-min") val rareFishFishesRequiredToSpawnMin: Int,
    @SerialName("rare-fish-fishes-required-to-spawn-max") val rareFishFishesRequiredToSpawnMax: Int,
    @SerialName("rare-fish-alive-time-min") val rareFishAliveTimeMin: Int,
    @SerialName("rare-fish-alive-time-max") val rareFishAliveTimeMax: Int,
    @SerialName("epic-fish-minigame-min-direction-duration") val epicFishMinigameMinDirectionDuration: Int,
    @SerialName("epic-fish-minigame-max-direction-duration") val epicFishMinigameMaxDirectionDuration: Int,
    @SerialName("epic-fish-minigame-speed") val epicFishMinigameSpeed: Double,
    @SerialName("epic-fish-fishes-required-to-spawn-min") val epicFishFishesRequiredToSpawnMin: Int,
    @SerialName("epic-fish-fishes-required-to-spawn-max") val epicFishFishesRequiredToSpawnMax: Int,
    @SerialName("epic-fish-alive-time-min") val epicFishAliveTimeMin: Int,
    @SerialName("epic-fish-alive-time-max") val epicFishAliveTimeMax: Int,
    @SerialName("legendary-fish-minigame-min-direction-duration") val legendaryFishMinigameMinDirectionDuration: Int,
    @SerialName("legendary-fish-minigame-max-direction-duration") val legendaryFishMinigameMaxDirectionDuration: Int,
    @SerialName("legendary-fish-minigame-speed") val legendaryFishMinigameSpeed: Double,
    @SerialName("legendary-fish-fishes-required-to-spawn-min") val legendaryFishFishesRequiredToSpawnMin: Int,
    @SerialName("legendary-fish-fishes-required-to-spawn-max") val legendaryFishFishesRequiredToSpawnMax: Int,
    @SerialName("legendary-fish-alive-time-min") val legendaryFishAliveTimeMin: Int,
    @SerialName("legendary-fish-alive-time-max") val legendaryFishAliveTimeMax: Int,
    @SerialName("sql-ip") val sqlIP: String,
    @SerialName("sql-port") val sqlPort: Int,
    @SerialName("sql-database-name") val sqlDatabaseName: String,
    @SerialName("sql-username") val sqlUsername: String,
    @SerialName("sql-password") val sqlPassword: String,
    @SerialName("sql-query-timeout") val sqlQueryTimeout: Long,
    @SerialName("common-fish-xp") val commonFishXP: Int,
    @SerialName("rare-fish-xp") val rareFishXP: Int,
    @SerialName("epic-fish-xp") val epicFishXP: Int,
    @SerialName("legendary-fish-xp") val legendaryFishXP: Int,
    @SerialName("stats-display-content") val statsDisplayContent: String,
    @SerialName("world") val world: @Serializable(with = WorldSerializer::class) World,
)


