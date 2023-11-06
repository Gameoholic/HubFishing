package xyz.gameoholic.hubfishing.displays

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import xyz.gameoholic.hubfishing.HubFishing
import xyz.gameoholic.hubfishing.data.PlayerData
import xyz.gameoholic.hubfishing.fish.FishRarity
import xyz.gameoholic.hubfishing.util.FishingUtil
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay
import org.bukkit.persistence.PersistentDataType
import java.util.*


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

            val key =
                NamespacedKey(HubFishing.plugin, "fishing-removable") //Mark entity, for removal upon server start
            display.persistentDataContainer.set(key, PersistentDataType.BOOLEAN, true)

            display.isVisibleByDefault = false
            display.text(getDisplayText())
            display.alignment = TextDisplay.TextAlignment.CENTER
            display.billboard = Display.Billboard.CENTER
            player.showEntity(HubFishing.plugin, display)

            displays.add(display)
        }
    }

    fun updateDisplays() {
        displays.forEach {
            it.text(getDisplayText())
        }
    }

    fun removeDisplays() {
        displays.forEach { it.remove() }
    }
//todo: sort out the ? playerdata mess in the entire plugin.
    /**
     * Gets the text for the display, and applies plugin placeholders with player data and PAPI placeholders.
     * @return The component for the display text.
     */
    private fun getDisplayText(): Component {
        val player = Bukkit.getPlayer(playerData.playerUUID)
        return MiniMessage.miniMessage().deserialize(
            PlaceholderAPI.setPlaceholders(player, FishingUtil.fishingConfig.statsDisplayContent),
            Placeholder.component(
                "playtime",
                text((playerData.playtime?.let { it / 3600 }).toString())
            ),
            Placeholder.component(
                "total_xp",
                text(playerData.xp.toString())
            ),
            Placeholder.component(
                "total_xp_to_level_up",
                text(playerData.levelData?.neededXPToLevelUp.toString())
            ),
            Placeholder.component(
                "remaining_xp_to_level_up",
                text(playerData.levelData?.remainingXPToLevelUp.toString())
            ),
            Placeholder.component(
                "xp",
                text(playerData.levelData?.remainderXP.toString())
            ),
            Placeholder.component(
                "level",
                text(playerData.levelData?.level.toString())
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