package xyz.gameoholic.hubfishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class MenusConfig(
    @SerialName("fishing-collection-menu-undiscovered-fish-material") val fishingCollectionMenuUndiscoveredFishMaterial: Material,
    @SerialName("fishing-collection-menu-undiscovered-fish-lore") val fishingCollectionMenuUndiscoveredFishLore: String,
    @SerialName("fishing-collection-menu-discovered-fish-lore") val fishingCollectionMenuDiscoveredFishLore: String,
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
)