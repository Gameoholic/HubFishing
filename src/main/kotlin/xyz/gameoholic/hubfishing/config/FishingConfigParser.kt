package xyz.gameoholic.hubfishing.config

import com.charleskorn.kaml.Yaml
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.fish.FishRarity
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.lake.FishLakeManager
import java.nio.file.Files
import kotlin.io.path.Path

object FishingConfigParser {
    private val plugin: HubFishingPlugin by inject()

    /**
     * Parses the fishing config files and returns the config for them.
     * @return The fishing config
     */
    fun parseConfig(): FishingConfig {
        var files = listOf(
            "config.yml",
            "fish_lake_managers.yml",
            "fish_variants.yml",
            "menus.yml",
            "strings.yml",
            "sounds.yml",
            "sql.yml",
        )
        var input = ""
        files.forEach {
            val filePath = Path("${plugin.dataFolder}/$it")
            input += Files.readString(filePath) + "\n"
        }
        val fishingConfig = Yaml.default.decodeFromString(FishingConfig.serializer(), input)

        loadFishRarities(fishingConfig)

        return fishingConfig
    }

    /**
     * The FishRarity enum class is initialized with dummy values at first,
     * because of a cyclic dependency between FishingConfig and FishRarity.
     * Because of that, FishRarity properites must be set directly
     * here.
     */
    private fun loadFishRarities(fishingConfig: FishingConfig) {
        FishRarity.COMMON.minigameMinDirectionDuration = fishingConfig.commonFishMinigameMinDirectionDuration
        FishRarity.COMMON.minigameMaxDirectionDuration = fishingConfig.commonFishMinigameMaxDirectionDuration
        FishRarity.COMMON.minigameMinSpeed = fishingConfig.commonFishMinigameMinSpeed
        FishRarity.COMMON.minigameMaxSpeed = fishingConfig.commonFishMinigameMaxSpeed
        FishRarity.COMMON.fishesRequiredToSpawnMin = 0
        FishRarity.COMMON.fishesRequiredToSpawnMax = 0
        FishRarity.COMMON.aliveTimeMin = fishingConfig.commonFishAliveTimeMin
        FishRarity.COMMON.aliveTimeMax = fishingConfig.commonFishAliveTimeMax
        FishRarity.COMMON.xp = fishingConfig.commonFishXP
        FishRarity.COMMON.displayName = fishingConfig.commonFishDisplayName

        FishRarity.RARE.minigameMinDirectionDuration = fishingConfig.rareFishMinigameMinDirectionDuration
        FishRarity.RARE.minigameMaxDirectionDuration = fishingConfig.rareFishMinigameMaxDirectionDuration
        FishRarity.RARE.minigameMinSpeed = fishingConfig.rareFishMinigameMinSpeed
        FishRarity.RARE.minigameMaxSpeed = fishingConfig.rareFishMinigameMaxSpeed
        FishRarity.RARE.fishesRequiredToSpawnMin = fishingConfig.rareFishFishesRequiredToSpawnMin
        FishRarity.RARE.fishesRequiredToSpawnMax = fishingConfig.rareFishFishesRequiredToSpawnMax
        FishRarity.RARE.aliveTimeMin = fishingConfig.rareFishAliveTimeMin
        FishRarity.RARE.aliveTimeMax = fishingConfig.rareFishAliveTimeMax
        FishRarity.RARE.xp = fishingConfig.rareFishXP
        FishRarity.RARE.displayName = fishingConfig.rareFishDisplayName

        FishRarity.EPIC.minigameMinDirectionDuration = fishingConfig.epicFishMinigameMinDirectionDuration
        FishRarity.EPIC.minigameMaxDirectionDuration = fishingConfig.epicFishMinigameMaxDirectionDuration
        FishRarity.EPIC.minigameMinSpeed = fishingConfig.epicFishMinigameMinSpeed
        FishRarity.EPIC.minigameMaxSpeed = fishingConfig.epicFishMinigameMaxSpeed
        FishRarity.EPIC.fishesRequiredToSpawnMin = fishingConfig.epicFishFishesRequiredToSpawnMin
        FishRarity.EPIC.fishesRequiredToSpawnMax = fishingConfig.epicFishFishesRequiredToSpawnMax
        FishRarity.EPIC.aliveTimeMin = fishingConfig.epicFishAliveTimeMin
        FishRarity.EPIC.aliveTimeMax = fishingConfig.epicFishAliveTimeMax
        FishRarity.EPIC.xp = fishingConfig.epicFishXP
        FishRarity.EPIC.displayName = fishingConfig.epicFishDisplayName

        FishRarity.LEGENDARY.minigameMinDirectionDuration = fishingConfig.legendaryFishMinigameMinDirectionDuration
        FishRarity.LEGENDARY.minigameMaxDirectionDuration = fishingConfig.legendaryFishMinigameMaxDirectionDuration
        FishRarity.LEGENDARY.minigameMinSpeed = fishingConfig.legendaryFishMinigameMinSpeed
        FishRarity.LEGENDARY.minigameMaxSpeed = fishingConfig.legendaryFishMinigameMaxSpeed
        FishRarity.LEGENDARY.fishesRequiredToSpawnMin = fishingConfig.legendaryFishFishesRequiredToSpawnMin
        FishRarity.LEGENDARY.fishesRequiredToSpawnMax = fishingConfig.legendaryFishFishesRequiredToSpawnMax
        FishRarity.LEGENDARY.aliveTimeMin = fishingConfig.legendaryFishAliveTimeMin
        FishRarity.LEGENDARY.aliveTimeMax = fishingConfig.legendaryFishAliveTimeMax
        FishRarity.LEGENDARY.xp = fishingConfig.legendaryFishXP
        FishRarity.LEGENDARY.displayName = fishingConfig.legendaryFishDisplayName
    }

    /**
     * Creates fish lake managers from config
     */
    fun getFishLakeManagers(): List<FishLakeManager> = plugin.config.fishLakeManagersSettings.map {
        FishLakeManager(
            it.spawnCorner1,
            it.spawnCorner2,
            it.corner1,
            it.corner2,
            it.armorStandYLevel,
            it.fishAmountChances,
            it.maxFishCount,
            it.statsDisplayLocation,
            it.permissionRequiredToEnter,
            it.fishSpawningAlgorithmCurve,
            it.rankBoostDisplayLocation,
            it.surfaceYLevel
        )
    }


}