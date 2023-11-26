package xyz.gameoholic.hubfishing.util

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject

object FishingUtil {
    private val plugin: HubFishingPlugin by inject()


    /**
     * Returns the FishRarity given a FishRarity ID.
     */
    fun getRarity(id: String) = plugin.config.fishRarities.rarities.first { it.id == id }
}