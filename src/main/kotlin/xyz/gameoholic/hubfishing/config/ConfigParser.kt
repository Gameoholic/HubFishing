package xyz.gameoholic.hubfishing.config

import com.charleskorn.kaml.Yaml
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.fish.FishRarity
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.lake.FishLakeManager
import java.nio.file.Files
import kotlin.io.path.Path

object ConfigParser {
    private val plugin: HubFishingPlugin by inject()

    /**
     * Parses the fishing config files and returns the config for them.
     * @return The fishing config
     */
    fun parseConfig(): Config {
        val config = Config(
            Yaml.default.decodeFromString(FishingConfig.serializer(),
                Files.readString(Path("${plugin.dataFolder}/fishing.yml"))),
            Yaml.default.decodeFromString(FishingMinigameConfig.serializer(),
                Files.readString(Path("${plugin.dataFolder}/fishing_minigame.yml"))),
            Yaml.default.decodeFromString(FishLakeManagerConfig.serializer(),
                Files.readString(Path("${plugin.dataFolder}/fish_lake_managers.yml"))),
            Yaml.default.decodeFromString(FishVariantsConfig.serializer(),
                Files.readString(Path("${plugin.dataFolder}/fish_variants.yml"))),
            Yaml.default.decodeFromString(MenusConfig.serializer(),
                Files.readString(Path("${plugin.dataFolder}/menus.yml"))),
            Yaml.default.decodeFromString(SoundsConfig.serializer(),
                Files.readString(Path("${plugin.dataFolder}/sounds.yml"))),
            Yaml.default.decodeFromString(SQLConfig.serializer(),
                Files.readString(Path("${plugin.dataFolder}/sql.yml"))),
            Yaml.default.decodeFromString(StringsConfig.serializer(),
                Files.readString(Path("${plugin.dataFolder}/strings.yml"))),
        )

        loadFishRarities(config)

        return config
    }

    /**
     * The FishRarity enum class is initialized with dummy values at first,
     * because of a cyclic dependency between Config and FishRarity.
     * Because of that, FishRarity properites must be set directly
     * here.
     */
    private fun loadFishRarities(config: Config) {
        FishRarity.COMMON.minigameMinDirectionDuration = config.fishVariants.commonFishMinigameMinDirectionDuration
        FishRarity.COMMON.minigameMaxDirectionDuration = config.fishVariants.commonFishMinigameMaxDirectionDuration
        FishRarity.COMMON.minigameMinSpeed = config.fishVariants.commonFishMinigameMinSpeed
        FishRarity.COMMON.minigameMaxSpeed = config.fishVariants.commonFishMinigameMaxSpeed
        FishRarity.COMMON.fishesRequiredToSpawnMin = 0
        FishRarity.COMMON.fishesRequiredToSpawnMax = 0
        FishRarity.COMMON.aliveTimeMin = config.fishVariants.commonFishAliveTimeMin
        FishRarity.COMMON.aliveTimeMax = config.fishVariants.commonFishAliveTimeMax
        FishRarity.COMMON.xp = config.fishVariants.commonFishXP
        FishRarity.COMMON.displayName = config.strings.commonFishDisplayName

        FishRarity.RARE.minigameMinDirectionDuration = config.fishVariants.rareFishMinigameMinDirectionDuration
        FishRarity.RARE.minigameMaxDirectionDuration = config.fishVariants.rareFishMinigameMaxDirectionDuration
        FishRarity.RARE.minigameMinSpeed = config.fishVariants.rareFishMinigameMinSpeed
        FishRarity.RARE.minigameMaxSpeed = config.fishVariants.rareFishMinigameMaxSpeed
        FishRarity.RARE.fishesRequiredToSpawnMin = config.fishVariants.rareFishFishesRequiredToSpawnMin
        FishRarity.RARE.fishesRequiredToSpawnMax = config.fishVariants.rareFishFishesRequiredToSpawnMax
        FishRarity.RARE.aliveTimeMin = config.fishVariants.rareFishAliveTimeMin
        FishRarity.RARE.aliveTimeMax = config.fishVariants.rareFishAliveTimeMax
        FishRarity.RARE.xp = config.fishVariants.rareFishXP
        FishRarity.RARE.displayName = config.strings.rareFishDisplayName

        FishRarity.EPIC.minigameMinDirectionDuration = config.fishVariants.epicFishMinigameMinDirectionDuration
        FishRarity.EPIC.minigameMaxDirectionDuration = config.fishVariants.epicFishMinigameMaxDirectionDuration
        FishRarity.EPIC.minigameMinSpeed = config.fishVariants.epicFishMinigameMinSpeed
        FishRarity.EPIC.minigameMaxSpeed = config.fishVariants.epicFishMinigameMaxSpeed
        FishRarity.EPIC.fishesRequiredToSpawnMin = config.fishVariants.epicFishFishesRequiredToSpawnMin
        FishRarity.EPIC.fishesRequiredToSpawnMax = config.fishVariants.epicFishFishesRequiredToSpawnMax
        FishRarity.EPIC.aliveTimeMin = config.fishVariants.epicFishAliveTimeMin
        FishRarity.EPIC.aliveTimeMax = config.fishVariants.epicFishAliveTimeMax
        FishRarity.EPIC.xp = config.fishVariants.epicFishXP
        FishRarity.EPIC.displayName = config.strings.epicFishDisplayName

        FishRarity.LEGENDARY.minigameMinDirectionDuration = config.fishVariants.legendaryFishMinigameMinDirectionDuration
        FishRarity.LEGENDARY.minigameMaxDirectionDuration = config.fishVariants.legendaryFishMinigameMaxDirectionDuration
        FishRarity.LEGENDARY.minigameMinSpeed = config.fishVariants.legendaryFishMinigameMinSpeed
        FishRarity.LEGENDARY.minigameMaxSpeed = config.fishVariants.legendaryFishMinigameMaxSpeed
        FishRarity.LEGENDARY.fishesRequiredToSpawnMin = config.fishVariants.legendaryFishFishesRequiredToSpawnMin
        FishRarity.LEGENDARY.fishesRequiredToSpawnMax = config.fishVariants.legendaryFishFishesRequiredToSpawnMax
        FishRarity.LEGENDARY.aliveTimeMin = config.fishVariants.legendaryFishAliveTimeMin
        FishRarity.LEGENDARY.aliveTimeMax = config.fishVariants.legendaryFishAliveTimeMax
        FishRarity.LEGENDARY.xp = config.fishVariants.legendaryFishXP
        FishRarity.LEGENDARY.displayName = config.strings.legendaryFishDisplayName
    }

    /**
     * Creates fish lake managers from config
     */
    fun getFishLakeManagers(): List<FishLakeManager> = plugin.config.fishLakeManager.fishLakeManagersOptions.map {
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