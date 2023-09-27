package net.topstrix.hubinteractions.fishing.displays

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.data.PlayerData
import net.topstrix.hubinteractions.fishing.fish.FishRarity
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*


//todo :remove all when server starts. add some sort of tag.
/**
 * This adds, removes, updates and manages the text displays for
 * every player.
 *
 * This class must only be created after the player data has been loaded properly.
 */
class PlayerDisplayManager(private val uuid: UUID) {
    private val displays = mutableListOf<TextDisplay>()
    private val playerData: PlayerData = FishingUtil.playerData.first { it.playerUUID == uuid }

    /**
     * Spawns the displays for a player, and makes them visible only to them.
     */
    fun spawnDisplays() {
        val player = Bukkit.getPlayer(uuid) ?: return
        FishingUtil.fishLakeManagers.forEach {
            val display = it.statsDisplayLocation.world
                .spawnEntity(it.statsDisplayLocation, EntityType.TEXT_DISPLAY) as TextDisplay

            val key = NamespacedKey(HubInteractions.plugin, "fishing-removable") //Mark entity, for removal upon server start
            display.persistentDataContainer.set(key, PersistentDataType.BOOLEAN, true)

            display.isVisibleByDefault = false
            display.text(getDisplayText())
            display.alignment = TextDisplay.TextAlignment.CENTER
            display.billboard = Display.Billboard.CENTER
            player.showEntity(HubInteractions.plugin, display)

            displays.add(display)
        }
    }
    fun updateDisplays() {
        displays.forEach { it.text(getDisplayText()) }
    }
    fun removeDisplays() {
        displays.forEach { it.remove() }
    }

    private fun getDisplayText(): Component {
        return MiniMessage.miniMessage().deserialize(
            FishingUtil.fishingConfig.statsDisplayContent,
            Placeholder.component(
                "playtime",
                text(playerData.playtime.toString())
            ),
            Placeholder.component(
                "xp",
                text(playerData.xp.toString())
            ),
            Placeholder.component(
                "fishes_caught",
                text(playerData.fishesCaught!!.values.sum())
            ),
            Placeholder.component(
                "common_fishes_caught",
                text(playerData.fishesCaught!!.filterKeys { it.rarity == FishRarity.COMMON }.values.sum())
            ),
            Placeholder.component(
                "rare_fishes_caught",
                text(playerData.fishesCaught!!.filterKeys { it.rarity == FishRarity.RARE }.values.sum())
            ),
            Placeholder.component(
                "epic_fishes_caught",
                text(playerData.fishesCaught!!.filterKeys { it.rarity == FishRarity.EPIC }.values.sum())
            ),
            Placeholder.component(
                "legendary_fishes_caught",
                text(playerData.fishesCaught!!.filterKeys { it.rarity == FishRarity.LEGENDARY }.values.sum())
            )
        )
    }
}