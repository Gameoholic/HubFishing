package xyz.gameoholic.hubfishing.config


data class Config(
    val fishing: FishingConfig,
    val fishingMinigame: FishingMinigameConfig,
    val fishLakeManager: FishLakeManagerConfig,
    val fishRarities: FishRaritiesConfig,
    val fishVariants: FishVariantsConfig,
    val menus: MenusConfig,
    val sounds: SoundsConfig,
    val sql: SQLConfig,
    val strings: StringsConfig
)


