package xyz.gameoholic.hubfishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FishingMinigameConfig(
    // Gameplay
    @SerialName("water-area-start-position") val waterAreaStartPosition: Double,
    @SerialName("water-area-length-pixels") val waterAreaLengthPixels: Double,
    @SerialName("water-area-fish-padding") val waterAreaFishPadding: Double,
    @SerialName("water-area-fish-spawn-padding") val waterAreaFishSpawnPadding: Double,
    @SerialName("rod-box-speed") val rodBoxSpeed: Double,
    @SerialName("max-fishing-rod-uses") val maxFishingRodUses: Int,
    @SerialName("time-restriction-warning-delay") val timeRestrictionWarningDelay: Int,
    @SerialName("time-restriction-strike-delay") val timeRestrictionStrikeDelay: Int,
    // Water UI
    @SerialName("water-characters") val waterCharacters: List<Char>,
    @SerialName("water-character-height") val waterCharacterHeight: Int,
    @SerialName("water-animation-speed") val waterAnimationSpeed: Int,
    // Rodbox UI
    @SerialName("rod-box-character") val rodBoxCharacter: Char,
    @SerialName("rod-box-character-height") val rodBoxCharacterHeight: Int,
    // Minirod UI
    @SerialName("mini-rod-character") val miniRodCharacter: Char,
    @SerialName("mini-rod-character-height") val miniRodCharacterHeight: Int,
    @SerialName("mini-rod-animation-speed") val miniRodsAnimationSpeed: Int,
    @SerialName("mini-rod-used-characters") val miniRodUsedCharacters: List<Char>,
    @SerialName("mini-rods-characters-position") val miniRodsCharactersPosition: Double,
    // Bigrod UI
    @SerialName("big-rod-position") val bigRodPosition: Double,
    @SerialName("big-rod-character-height") val bigRodCharacterHeight: Int,
    @SerialName("big-rod-characters") val bigRodCharacters: List<Char>,
    // Longrod UI
    @SerialName("long-rod-character-height") val longRodCharacterHeight: Int,
    @SerialName("long-rod-character") val longRodCharacter: Char,
    // Longrod end UI
    @SerialName("long-rod-end-character-height") val longRodEndCharacterHeight: Int,
    @SerialName("long-rod-end-character") val longRodEndCharacter: Char,
    @SerialName("long-rod-end-character-offset") val longRodEndCharacterOffset: Double,
    // Infobox UI
    @SerialName("info-box-character") val infoBoxCharacter: Char,
    @SerialName("info-box-character-height") val infoBoxCharacterHeight: Int,
    @SerialName("info-box-position") val infoBoxPosition: Double,
    // Bucket UI
    @SerialName("bucket-character-height") val bucketCharacterHeight: Int,
    @SerialName("bucket-characters") val bucketCharacters: List<Char>,
    @SerialName("fishing-minigame-success-animation-length") val fishingMinigameSuccessAnimationLength: Int,
    @SerialName("fishing-minigame-success-animation-speed") val fishingMinigameSuccessAnimationSpeed: Int,
    @SerialName("bucket-character-offset") val bucketOffset: Double,
    // Clock UI
    @SerialName("clock-animation-speed") val clockAnimationSpeed: Int,
    @SerialName("clock-characters") val clockCharacters: List<Char>,
    @SerialName("clock-character-height") val clockCharacterHeight: Int,
    @SerialName("clock-position") val clockPosition: Double,
    // Big rod break UI
    @SerialName("rod-break-animation-speed") val rodBreakAnimationSpeed: Int,
    @SerialName("rod-break-animation-height") val rodBreakAnimationHeight: Int,
    @SerialName("rod-break-characters") val rodBreakCharacters: List<Char>,
    // Long rod break UI
    @SerialName("rod-long-break-animation-speed") val rodLongBreakAnimationSpeed: Int,
    @SerialName("rod-long-break-animation-height") val rodLongBreakAnimationHeight: Int,
    @SerialName("rod-long-break-characters") val rodLongBreakCharacters: List<Char>,
    // Long rod end break UI
    @SerialName("rod-long-end-break-characters") val rodLongEndBreakCharacters: List<Char>,
    @SerialName("rod-long-end-break-animation-height") val rodLongEndBreakAnimationHeight: Int,
    @SerialName("rod-long-end-break-animation-speed") val rodLongEndBreakAnimationSpeed: Int,
    // Rod extension
    @SerialName("extension-animation-delay") val extensionAnimationDelay: Int,

)
