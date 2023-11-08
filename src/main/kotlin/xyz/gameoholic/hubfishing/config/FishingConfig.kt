package xyz.gameoholic.hubfishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.sound.Sound
import xyz.gameoholic.hubfishing.crate.Crate
import xyz.gameoholic.hubfishing.fish.FishVariant
import xyz.gameoholic.hubfishing.util.LoggerUtil
import xyz.gameoholic.hubfishing.serialization.SoundSerializer
import xyz.gameoholic.hubfishing.serialization.WorldSerializer
import org.bukkit.Material
import org.bukkit.World


@Serializable
data class FishingConfig(
    @SerialName("log-level") val logLevel: LoggerUtil.LogLevel,
    @SerialName("fish-lake-managers") val fishLakeManagersSettings: List<FishLakeManagerSettings>,
    @SerialName("fish-variants") val fishVariants: List<FishVariant>,
    val crates: List<Crate>,
    @SerialName("water-area-length-pixels") val waterAreaLengthPixels: Double,
    @SerialName("water-area-fish-padding") val waterAreaFishPadding: Double,
    @SerialName("water-area-fish-spawn-padding") val waterAreaFishSpawnPadding: Double,
    @SerialName("water-characters") val waterCharacters: List<Char>,
    @SerialName("water-character-height") val waterCharacterHeight: Int,
    @SerialName("rod-box-speed") val rodBoxSpeed: Double,
    @SerialName("rod-box-character") val rodBoxCharacter: Char,
    @SerialName("rod-box-character-height") val rodBoxCharacterHeight: Int,
    @SerialName("mini-rod-character") val miniRodCharacter: Char,
    @SerialName("mini-rod-character-height") val miniRodCharacterHeight: Int,
    @SerialName("mini-rod-animation-speed") val miniRodsAnimationSpeed: Int,
    @SerialName("mini-rod-used-characters") val miniRodUsedCharacters: List<Char>,
    @SerialName("big-rod-character-height") val bigRodCharacterHeight: Int,
    @SerialName("big-rod-characters") val bigRodCharacters: List<Char>,
    @SerialName("long-rod-character-height") val longRodCharacterHeight: Int,
    @SerialName("long-rod-character") val longRodCharacter: Char,
    @SerialName("long-rod-end-character-height") val longRodEndCharacterHeight: Int,
    @SerialName("long-rod-end-character") val longRodEndCharacter: Char,
    @SerialName("max-fishing-rod-uses") val maxFishingRodUses: Int,
    @SerialName("common-fish-minigame-min-direction-duration") val commonFishMinigameMinDirectionDuration: Int,
    @SerialName("common-fish-minigame-max-direction-duration") val commonFishMinigameMaxDirectionDuration: Int,
    @SerialName("common-fish-minigame-min-speed") val commonFishMinigameMinSpeed: Double,
    @SerialName("common-fish-minigame-max-speed") val commonFishMinigameMaxSpeed: Double,
    @SerialName("common-fish-alive-time-min") val commonFishAliveTimeMin: Int,
    @SerialName("common-fish-alive-time-max") val commonFishAliveTimeMax: Int,
    @SerialName("rare-fish-minigame-min-direction-duration") val rareFishMinigameMinDirectionDuration: Int,
    @SerialName("rare-fish-minigame-max-direction-duration") val rareFishMinigameMaxDirectionDuration: Int,
    @SerialName("rare-fish-minigame-min-speed") val rareFishMinigameMinSpeed: Double,
    @SerialName("rare-fish-minigame-max-speed") val rareFishMinigameMaxSpeed: Double,
    @SerialName("rare-fish-fishes-required-to-spawn-min") val rareFishFishesRequiredToSpawnMin: Int,
    @SerialName("rare-fish-fishes-required-to-spawn-max") val rareFishFishesRequiredToSpawnMax: Int,
    @SerialName("rare-fish-alive-time-min") val rareFishAliveTimeMin: Int,
    @SerialName("rare-fish-alive-time-max") val rareFishAliveTimeMax: Int,
    @SerialName("epic-fish-minigame-min-direction-duration") val epicFishMinigameMinDirectionDuration: Int,
    @SerialName("epic-fish-minigame-max-direction-duration") val epicFishMinigameMaxDirectionDuration: Int,
    @SerialName("epic-fish-minigame-min-speed") val epicFishMinigameMinSpeed: Double,
    @SerialName("epic-fish-minigame-max-speed") val epicFishMinigameMaxSpeed: Double,
    @SerialName("epic-fish-fishes-required-to-spawn-min") val epicFishFishesRequiredToSpawnMin: Int,
    @SerialName("epic-fish-fishes-required-to-spawn-max") val epicFishFishesRequiredToSpawnMax: Int,
    @SerialName("epic-fish-alive-time-min") val epicFishAliveTimeMin: Int,
    @SerialName("epic-fish-alive-time-max") val epicFishAliveTimeMax: Int,
    @SerialName("legendary-fish-minigame-min-direction-duration") val legendaryFishMinigameMinDirectionDuration: Int,
    @SerialName("legendary-fish-minigame-max-direction-duration") val legendaryFishMinigameMaxDirectionDuration: Int,
    @SerialName("legendary-fish-minigame-min-speed") val legendaryFishMinigameMinSpeed: Double,
    @SerialName("legendary-fish-minigame-max-speed") val legendaryFishMinigameMaxSpeed: Double,
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
    @SerialName("rank-boost-display-none-content") val rankBoostDisplayNoneContent: String,
    @SerialName("rank-boost-display-boosted-content") val rankBoostDisplayBoostedContent: String,
    @SerialName("rank-boost-amount") val rankBoostAmount: Double,
    @SerialName("rank-boost-permission") val rankBoostPermission: String,
    @SerialName("fishing-collection-menu-undiscovered-fish-material") val fishingCollectionMenuUndiscoveredFishMaterial: Material,
    @SerialName("fishing-collection-menu-undiscovered-fish-lore") val fishingCollectionMenuUndiscoveredFishLore: String,
    @SerialName("fishing-collection-menu-discovered-fish-lore") val fishingCollectionMenuDiscoveredFishLore: String,
    @SerialName("common-fish-display-name") val commonFishDisplayName: String,
    @SerialName("rare-fish-display-name") val rareFishDisplayName: String,
    @SerialName("epic-fish-display-name") val epicFishDisplayName: String,
    @SerialName("legendary-fish-display-name") val legendaryFishDisplayName: String,
    @SerialName("main-menu-fish-collection-item-material") val mainMenuFishCollectionItemMaterial: Material,
    @SerialName("main-menu-fish-collection-item-model-data") val mainMenuFishCollectionItemModelData: Int,
    @SerialName("main-menu-fish-collection-item-index") val mainMenuFishCollectionItemIndex: Int,
    @SerialName("main-menu-fish-collection-item-name") val mainMenuFishCollectionItemName: String,
    @SerialName("main-menu-fish-collection-item-lore") val mainMenuFishCollectionItemLore: String,
    @SerialName("fishing-collection-menu-fish-min-index") val fishingCollectionMenuFishMinIndex: Int,
    @SerialName("fishing-collection-menu-fish-max-index") val fishingCollectionMenuFishMaxIndex: Int,
    @SerialName("main-menu-rod-customization-item-material") val mainMenuRodCustomizationItemMaterial: Material,
    @SerialName("main-menu-rod-customization-item-model-data") val mainMenuRodCustomizationItemModelData: Int,
    @SerialName("main-menu-rod-customization-item-index") val mainMenuRodCustomizationItemIndex: Int,
    @SerialName("main-menu-rod-customization-item-name") val mainMenuRodCustomizationItemName: String,
    @SerialName("main-menu-rod-customization-item-lore") val mainMenuRodCustomizationItemLore: String,
    @SerialName("main-menu-rewards-item-material") val mainMenuRewardsItemMaterial: Material,
    @SerialName("main-menu-rewards-item-model-data") val mainMenuRewardsItemModelData: Int,
    @SerialName("main-menu-rewards-item-index") val mainMenuRewardsItemIndex: Int,
    @SerialName("main-menu-rewards-item-name") val mainMenuRewardsItemName: String,
    @SerialName("main-menu-rewards-item-lore") val mainMenuRewardsItemLore: String,
    @SerialName("main-menu-stats-item-index") val mainMenuStatsItemIndex: Int,
    @SerialName("main-menu-stats-item-name") val mainMenuStatsItemName: String,
    @SerialName("main-menu-stats-item-lore") val mainMenuStatsItemLore: String,
    @SerialName("main-menu-stats-item-material") val mainMenuStatsItemMaterial: Material,
    @SerialName("main-menu-stats-item-model-data") val mainMenuStatsItemModelData: Int,
    @SerialName("main-menu-size") val mainMenuSize: Int,
    @SerialName("main-menu-name") val mainMenuName: String,
    @SerialName("fishing-collection-menu-size") val fishingCollectionMenuSize: Int,
    @SerialName("fishing-collection-menu-name") val fishingCollectionMenuName: String,
    @SerialName("main-menu-close-item-material") val mainMenuCloseItemMaterial: Material,
    @SerialName("main-menu-close-item-model-data") val mainMenuCloseItemModelData: Int,
    @SerialName("main-menu-close-item-index") val mainMenuCloseItemIndex: Int,
    @SerialName("main-menu-close-item-name") val mainMenuCloseItemName: String,
    @SerialName("main-menu-close-item-lore") val mainMenuCloseItemLore: String,
    @SerialName("fishing-collection-menu-close-item-material") val fishingCollectionMenuCloseItemMaterial: Material,
    @SerialName("fishing-collection-menu-close-item-model-data") val fishingCollectionMenuCloseItemModelData: Int,
    @SerialName("fishing-collection-menu-close-item-index") val fishingCollectionMenuCloseItemIndex: Int,
    @SerialName("fishing-collection-menu-close-item-name") val fishingCollectionMenuCloseItemName: String,
    @SerialName("fishing-collection-menu-close-item-lore") val fishingCollectionMenuCloseItemLore: String,
    @SerialName("common-fish-crate-shard-chance") val commonFishCrateShardChance: Double,
    @SerialName("rare-fish-crate-shard-chance") val rareFishCrateShardChance: Double,
    @SerialName("epic-fish-crate-shard-chance") val epicFishCrateShardChance: Double,
    @SerialName("legendary-fish-crate-shard-chance") val legendaryFishCrateShardChance: Double,
    @SerialName("main-menu-crate-shards-item-name") val mainMenuCrateShardsItemName: String,
    @SerialName("main-menu-crate-shards-item-lore") val mainMenuCrateShardsItemLore: String,
    @SerialName("main-menu-crate-shards-item-index") val mainMenuCrateShardsItemIndex: Int,
    @SerialName("main-menu-crate-shards-item-material") val mainMenuCrateShardsItemMaterial: Material,
    @SerialName("main-menu-crate-shards-item-model-data") val mainMenuCrateShardsItemModelData: Int,
    @SerialName("crate-shards-menu-size") val crateShardsMenuSize: Int,
    @SerialName("crate-shards-menu-name") val crateShardsMenuName: String,
    @SerialName("crate-shards-menu-close-item-material") val crateShardsMenuCloseItemMaterial: Material,
    @SerialName("crate-shards-menu-close-item-model-data") val crateShardsMenuCloseItemModelData: Int,
    @SerialName("crate-shards-menu-close-item-index") val crateShardsMenuCloseItemIndex: Int,
    @SerialName("crate-shards-menu-close-item-name") val crateShardsMenuCloseItemName: String,
    @SerialName("crate-shards-menu-close-item-lore") val crateShardsMenuCloseItemLore: String,
    @SerialName("crate-shards-menu-crate-min-index") val crateShardsMenuCrateMinIndex: Int,
    @SerialName("crate-shards-menu-crate-max-index") val crateShardsMenuCrateMaxIndex: Int,
    @SerialName("crate-shards-menu-crate-name") val crateShardsMenuCrateName: String,
    @SerialName("crate-shards-menu-crate-lore") val crateShardsMenuCrateLore: String,
    @SerialName("level-initial-xp-requirement") val levelInitialXPRequirement: Int,
    @SerialName("level-xp-requirement-growth") val levelXPRequirementGrowth: Int,
    @SerialName("level-xp-requirement-growth-multiplier") val levelXPRequirementGrowthMultiplier: Double,
    @SerialName("level-growth-delay") val levelGrowthDelay: Int,
    @SerialName("level-xp-requirement-growth-cap") val levelXPRequirementGrowthCap: Int,
    @SerialName("legendary-fish-spawn-message") val legendaryFishSpawnMessage: String,
    @SerialName("legendary-fish-spawn-sound") val legendaryFishSpawnSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("fish-found-message") val fishFoundMessage: String,
    @SerialName("fish-found-sound") val fishFoundSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("fishing-minigame-miss-sound") val fishingMinigameMissSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("extension-animation-delay") val extensionAnimationDelay: Int,
    @SerialName("long-rod-end-character-offset") val longRodEndCharacterOffset: Double,
    @SerialName("big-rod-position") val bigRodPosition: Double,
    @SerialName("water-area-start-position") val waterAreaStartPosition: Double,
    @SerialName("water-animation-speed") val waterAnimationSpeed: Int,
    @SerialName("info-box-character") val infoBoxCharacter: Char,
    @SerialName("info-box-character-height") val infoBoxCharacterHeight: Int,
    @SerialName("info-box-position") val infoBoxPosition: Double,
    @SerialName("level-up-message") val levelUpMessage: String,
    @SerialName("level-up-sound") val levelUpSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("xp-gained-action-bar-message") val XPGainedActionBarMessage: String,
    @SerialName("shard-receive-message") val shardReceiveMessage: String,
    @SerialName("shard-receive-sound") val shardReceiveSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("crate-craft-message") val crateCraftMessage: String,
    @SerialName("crate-craft-sound") val crateCraftSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("fishing-minigame-catch-sound") val fishingMinigameCatchSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("bucket-character-height") val bucketCharacterHeight: Int,
    @SerialName("bucket-characters") val bucketCharacters: List<Char>,
    @SerialName("fishing-minigame-success-animation-length") val fishingMinigameSuccessAnimationLength: Int,
    @SerialName("fishing-minigame-success-animation-speed") val fishingMinigameSuccessAnimationSpeed: Int,
    @SerialName("bucket-character-offset") val bucketOffset: Double,
    @SerialName("mini-rods-characters-position") val miniRodsCharactersPosition: Double,
    @SerialName("passed-time-restriction-message") val passedTimeRestrictionMessage: String,
    @SerialName("passed-time-restriction-sound") val passedTimeRestrictionSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("clock-animation-speed") val clockAnimationSpeed: Int,
    @SerialName("clock-characters") val clockCharacters: List<Char>,
    @SerialName("clock-character-height") val clockCharacterHeight: Int,
    @SerialName("clock-position") val clockPosition: Double,
    @SerialName("time-restriction-strike-message") val timeRestrictionStrikeMessage: String,
    @SerialName("time-restriction-warning-delay") val timeRestrictionWarningDelay: Int,
    @SerialName("time-restriction-strike-delay") val timeRestrictionStrikeDelay: Int,
    @SerialName("time-restriction-strike-sound") val timeRestrictionStrikeSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("minigame-failure-sound") val minigameFailureSound: @Serializable(with = SoundSerializer::class) Sound,
    @SerialName("minigame-leave-message") val minigameLeaveMessage: String,
    @SerialName("hook-cooldown") val hookCooldown: Int,
    @SerialName("rod-break-animation-speed") val rodBreakAnimationSpeed: Int,
    @SerialName("rod-break-animation-height") val rodBreakAnimationHeight: Int,
    @SerialName("rod-break-characters") val rodBreakCharacters: List<Char>,
    @SerialName("rod-long-break-animation-speed") val rodLongBreakAnimationSpeed: Int,
    @SerialName("rod-long-break-animation-height") val rodLongBreakAnimationHeight: Int,
    @SerialName("rod-long-break-characters") val rodLongBreakCharacters: List<Char>,
    @SerialName("rod-long-end-break-characters") val rodLongEndBreakCharacters: List<Char>,
    @SerialName("rod-long-end-break-animation-height") val rodLongEndBreakAnimationHeight: Int,
    @SerialName("rod-long-end-break-animation-speed") val rodLongEndBreakAnimationSpeed: Int,
    )


