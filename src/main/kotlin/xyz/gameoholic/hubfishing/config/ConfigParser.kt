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
            Yaml.default.decodeFromString(FishRaritiesConfig.serializer(),
                Files.readString(Path("${plugin.dataFolder}/fish_rarities.yml"))),
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

        return config
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