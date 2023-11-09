package xyz.gameoholic.hubfishing.util

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject

object FishingUtil {
    private val plugin: HubFishingPlugin by inject()

    /**
     * In the event of a crash / server close, old fishing related entities
     * (displays, armor stands, etc.) will remain. This gets rid of them.
     */
    fun removeOldEntities() {
        val key = NamespacedKey(plugin, "fishing-removable")

        //Before removing the entities, we must load the chunks.
        plugin.fishLakeManagers.forEach {
            for (x in it.corner1.x.toInt() .. it.corner2.x.toInt()) {
                for (y in it.corner1.y.toInt() .. it.corner2.y.toInt()) {
                    for (z in it.corner1.z.toInt() .. it.corner2.z.toInt()) {
                        plugin.config.fishing.world.getBlockAt(x, y, z).location.chunk.load()
                    }
                }
            }
        }

        plugin.config.fishing.world.entities.forEach {
            val container: PersistentDataContainer = it.persistentDataContainer
            if (container.has(key, PersistentDataType.BOOLEAN)) {
                it.remove()
            }
        }
    }
}