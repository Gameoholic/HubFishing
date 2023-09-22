package net.topstrix.hubinteractions.fishing.config

import com.charleskorn.kaml.Yaml
import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.fish.FishRarity
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object FishingFileParser {

    /**
     * Parses the fishing.yml file and returns the config for it.
     * @return The config for elytra spots
     */
    fun parseFile(): FishingConfig {
        val filePath = File(HubInteractions.plugin.dataFolder, "fishing.yml").path
        val input = Files.readString(Path.of(filePath))
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
        FishRarity.COMMON.minigameSpeed = fishingConfig.commonFishMinigameSpeed
        FishRarity.COMMON.fishesRequiredToSpawnMin = 0
        FishRarity.COMMON.fishesRequiredToSpawnMax = 0

        FishRarity.RARE.minigameMinDirectionDuration = fishingConfig.rareFishMinigameMinDirectionDuration
        FishRarity.RARE.minigameMaxDirectionDuration = fishingConfig.rareFishMinigameMaxDirectionDuration
        FishRarity.RARE.minigameSpeed = fishingConfig.rareFishMinigameSpeed
        FishRarity.RARE.fishesRequiredToSpawnMin = fishingConfig.rareFishFishesRequiredToSpawnMin
        FishRarity.RARE.fishesRequiredToSpawnMax = fishingConfig.rareFishFishesRequiredToSpawnMax

        FishRarity.EPIC.minigameMinDirectionDuration = fishingConfig.epicFishMinigameMinDirectionDuration
        FishRarity.EPIC.minigameMaxDirectionDuration = fishingConfig.epicFishMinigameMaxDirectionDuration
        FishRarity.EPIC.minigameSpeed = fishingConfig.epicFishMinigameSpeed
        FishRarity.EPIC.fishesRequiredToSpawnMin = fishingConfig.epicFishFishesRequiredToSpawnMin
        FishRarity.EPIC.fishesRequiredToSpawnMax = fishingConfig.epicFishFishesRequiredToSpawnMax

        FishRarity.LEGENDARY.minigameMinDirectionDuration = fishingConfig.legendaryFishMinigameMinDirectionDuration
        FishRarity.LEGENDARY.minigameMaxDirectionDuration = fishingConfig.legendaryFishMinigameMaxDirectionDuration
        FishRarity.LEGENDARY.minigameSpeed = fishingConfig.legendaryFishMinigameSpeed
        FishRarity.LEGENDARY.fishesRequiredToSpawnMin = fishingConfig.legendaryFishFishesRequiredToSpawnMin
        FishRarity.LEGENDARY.fishesRequiredToSpawnMax = fishingConfig.legendaryFishFishesRequiredToSpawnMax
    }


}