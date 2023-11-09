package xyz.gameoholic.hubfishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.gameoholic.hubfishing.fish.FishVariant

@Serializable
data class FishVariantsConfig(
    // Common fish rarity
    @SerialName("common-fish-minigame-min-direction-duration") val commonFishMinigameMinDirectionDuration: Int,
    @SerialName("common-fish-minigame-max-direction-duration") val commonFishMinigameMaxDirectionDuration: Int,
    @SerialName("common-fish-minigame-min-speed") val commonFishMinigameMinSpeed: Double,
    @SerialName("common-fish-minigame-max-speed") val commonFishMinigameMaxSpeed: Double,
    @SerialName("common-fish-alive-time-min") val commonFishAliveTimeMin: Int,
    @SerialName("common-fish-alive-time-max") val commonFishAliveTimeMax: Int,
    @SerialName("common-fish-xp") val commonFishXP: Int,
    // Rare fish rarity
    @SerialName("rare-fish-minigame-min-direction-duration") val rareFishMinigameMinDirectionDuration: Int,
    @SerialName("rare-fish-minigame-max-direction-duration") val rareFishMinigameMaxDirectionDuration: Int,
    @SerialName("rare-fish-minigame-min-speed") val rareFishMinigameMinSpeed: Double,
    @SerialName("rare-fish-minigame-max-speed") val rareFishMinigameMaxSpeed: Double,
    @SerialName("rare-fish-fishes-required-to-spawn-min") val rareFishFishesRequiredToSpawnMin: Int,
    @SerialName("rare-fish-fishes-required-to-spawn-max") val rareFishFishesRequiredToSpawnMax: Int,
    @SerialName("rare-fish-alive-time-min") val rareFishAliveTimeMin: Int,
    @SerialName("rare-fish-alive-time-max") val rareFishAliveTimeMax: Int,
    @SerialName("rare-fish-xp") val rareFishXP: Int,
    // Epic fish rarity
    @SerialName("epic-fish-minigame-min-direction-duration") val epicFishMinigameMinDirectionDuration: Int,
    @SerialName("epic-fish-minigame-max-direction-duration") val epicFishMinigameMaxDirectionDuration: Int,
    @SerialName("epic-fish-minigame-min-speed") val epicFishMinigameMinSpeed: Double,
    @SerialName("epic-fish-minigame-max-speed") val epicFishMinigameMaxSpeed: Double,
    @SerialName("epic-fish-fishes-required-to-spawn-min") val epicFishFishesRequiredToSpawnMin: Int,
    @SerialName("epic-fish-fishes-required-to-spawn-max") val epicFishFishesRequiredToSpawnMax: Int,
    @SerialName("epic-fish-alive-time-min") val epicFishAliveTimeMin: Int,
    @SerialName("epic-fish-alive-time-max") val epicFishAliveTimeMax: Int,
    @SerialName("epic-fish-xp") val epicFishXP: Int,
    // Legendary fish rarity
    @SerialName("legendary-fish-minigame-min-direction-duration") val legendaryFishMinigameMinDirectionDuration: Int,
    @SerialName("legendary-fish-minigame-max-direction-duration") val legendaryFishMinigameMaxDirectionDuration: Int,
    @SerialName("legendary-fish-minigame-min-speed") val legendaryFishMinigameMinSpeed: Double,
    @SerialName("legendary-fish-minigame-max-speed") val legendaryFishMinigameMaxSpeed: Double,
    @SerialName("legendary-fish-fishes-required-to-spawn-min") val legendaryFishFishesRequiredToSpawnMin: Int,
    @SerialName("legendary-fish-fishes-required-to-spawn-max") val legendaryFishFishesRequiredToSpawnMax: Int,
    @SerialName("legendary-fish-alive-time-min") val legendaryFishAliveTimeMin: Int,
    @SerialName("legendary-fish-alive-time-max") val legendaryFishAliveTimeMax: Int,
    @SerialName("legendary-fish-xp") val legendaryFishXP: Int,
    // Fish variants
    @SerialName("fish-variants") val variants: List<FishVariant>,
    )
