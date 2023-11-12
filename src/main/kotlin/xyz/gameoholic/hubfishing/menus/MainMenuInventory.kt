package xyz.gameoholic.hubfishing.menus

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import xyz.gameoholic.hubfishing.player.data.PlayerData
import xyz.gameoholic.hubfishing.fish.FishRarity
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.HashMap


class MainMenuInventory(private val playerUUID: UUID, private val playerData: PlayerData) : FishingInventory {
    private val plugin: HubFishingPlugin by inject()

    private val player = Bukkit.getPlayer(playerUUID)

    override val inv: Inventory = Bukkit.createInventory(
        this,
        plugin.config.menus.mainMenuSize,
        MiniMessage.miniMessage().deserialize(
            PlaceholderAPI.setPlaceholders(player, plugin.config.menus.mainMenuName)
        )
    )
    override var eventsAreRegistered = false

    init {
        registerEvents()

        getInventoryItems().entries.forEach {
            inv.setItem(it.key, it.value)
        }
    }

    override fun registerEvents() {
        if (eventsAreRegistered) return
        Bukkit.getPluginManager().registerEvents(this, plugin)
        eventsAreRegistered = true
    }

    override fun unregisterEvents() {
        if (!eventsAreRegistered) return
        InventoryCloseEvent.getHandlerList().unregister(this)
        InventoryClickEvent.getHandlerList().unregister(this)
        eventsAreRegistered = false
    }

    /**
     * Generates the inventory contents for the main menu
     * @return The item indexes mapped to the item stacks
     */
    private fun getInventoryItems(): HashMap<Int, ItemStack> {
        val items = hashMapOf<Int, ItemStack>()

        items[plugin.config.menus.mainMenuFishCollectionItemIndex] = getFishCollectionItem()
        items[plugin.config.menus.mainMenuRodCustomizationItemIndex] = getRodCustomizationItem()
        items[plugin.config.menus.mainMenuRewardsItemIndex] = getRewardsItem()
        items[plugin.config.menus.mainMenuStatsItemIndex] = getStatsItem()
        items[plugin.config.menus.mainMenuCloseItemIndex] = getCloseItem()

        return items
    }

    private fun getFishCollectionItem(): ItemStack {
        val item = ItemStack(plugin.config.menus.mainMenuFishCollectionItemMaterial, 1)
        val meta = item.itemMeta

        meta.displayName(
            MiniMessage.miniMessage().deserialize(
                PlaceholderAPI.setPlaceholders(
                    player,
                    plugin.config.menus.mainMenuFishCollectionItemName
                )
            ).decoration(TextDecoration.ITALIC, false)
        )
        meta.lore(PlaceholderAPI.setPlaceholders(
            player,
            plugin.config.menus.mainMenuFishCollectionItemLore.split("<newline>", "<br>")
        ).map {
            MiniMessage.miniMessage().deserialize(it).decoration(TextDecoration.ITALIC, false)
        }
        )

        //Make item uniquely identifiable for inventory click detection with ID
        meta.persistentDataContainer.set(
            NamespacedKey(plugin, "menu_item"),
            PersistentDataType.STRING,
            "fish_collection"
        )
        meta.setCustomModelData(plugin.config.menus.mainMenuFishCollectionItemModelData)

        item.itemMeta = meta
        return item
    }

    private fun getRodCustomizationItem(): ItemStack {
        val item = ItemStack(plugin.config.menus.mainMenuRodCustomizationItemMaterial, 1)
        val meta = item.itemMeta

        meta.displayName(
            MiniMessage.miniMessage().deserialize(
                PlaceholderAPI.setPlaceholders(player, plugin.config.menus.mainMenuRodCustomizationItemName)
            ).decoration(TextDecoration.ITALIC, false)
        )
        meta.lore(
            plugin.config.menus.mainMenuRodCustomizationItemLore.split("<newline>", "<br>")
            .map {
                MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, it))
                    .decoration(TextDecoration.ITALIC, false)
            }
        )
        //Make item uniquely identifiable for inventory click detection with ID
        meta.persistentDataContainer.set(
            NamespacedKey(plugin, "menu_item"),
            PersistentDataType.STRING,
            "coming_soon"
        )
        meta.setCustomModelData(plugin.config.menus.mainMenuRodCustomizationItemModelData)

        item.itemMeta = meta
        return item
    }

    private fun getRewardsItem(): ItemStack {
        val item = ItemStack(plugin.config.menus.mainMenuRewardsItemMaterial, 1)
        val meta = item.itemMeta

        meta.displayName(
            MiniMessage.miniMessage().deserialize(
                PlaceholderAPI.setPlaceholders(player, plugin.config.menus.mainMenuRewardsItemName)
            ).decoration(TextDecoration.ITALIC, false)
        )
        meta.lore(
            plugin.config.menus.mainMenuRewardsItemLore.split("<newline>", "<br>")
            .map {
                MiniMessage.miniMessage().deserialize(
                    PlaceholderAPI.setPlaceholders(player, it)
                ).decoration(TextDecoration.ITALIC, false)
            }
        )
        //Make item uniquely identifiable for inventory click detection with ID
        meta.persistentDataContainer.set(
            NamespacedKey(plugin, "menu_item"),
            PersistentDataType.STRING,
            "coming_soon"
        )
        meta.setCustomModelData(plugin.config.menus.mainMenuRewardsItemModelData)

        item.itemMeta = meta
        return item
    }

    private fun getStatsItem(): ItemStack {
        val item = ItemStack(plugin.config.menus.mainMenuStatsItemMaterial, 1)
        val meta = item.itemMeta

        meta.displayName(
            MiniMessage.miniMessage()
                .deserialize(PlaceholderAPI.setPlaceholders(player, plugin.config.menus.mainMenuStatsItemName))
                .decoration(TextDecoration.ITALIC, false)
        )
        meta.lore(
            plugin.config.menus.mainMenuStatsItemLore.split("<newline>", "<br>")
            .map {
                MiniMessage.miniMessage().deserialize(
                    PlaceholderAPI.setPlaceholders(player, it),
                    Placeholder.component(
                        "playtime",
                        text(playerData.playtime / 3600)
                    ),
                    Placeholder.component(
                        "xp",
                        text(playerData.xp)
                    ),
                    Placeholder.component(
                        "total_xp",
                        text(playerData.xp)
                    ),
                    Placeholder.component(
                        "total_xp_to_level_up",
                        text(playerData.levelData.neededXPToLevelUp)
                    ),
                    Placeholder.component(
                        "remaining_xp_to_level_up",
                        text(playerData.levelData.remainingXPToLevelUp)
                    ),
                    Placeholder.component(
                        "xp",
                        text(playerData.levelData.remainderXP)
                    ),
                    Placeholder.component(
                        "level",
                        text(playerData.levelData.level)
                    ),
                    Placeholder.component(
                        "fishes_caught",
                        text((playerData.fishesCaught.values.sum()))
                    ),
                    *plugin.config.fishRarities.rarities.map {fishRarity ->
                        Placeholder.component(
                            "${fishRarity.id}_fishes_caught",
                            text((playerData.fishesCaught.filterKeys { fishVariant ->
                                fishVariant.rarityId == fishRarity.id }.values.sum())
                            )
                        )
                    }.toTypedArray(),
                ).decoration(TextDecoration.ITALIC, false)
            }
        )
        //Make item uniquely identifiable for inventory click detection with ID
        meta.persistentDataContainer.set(
            NamespacedKey(plugin, "menu_item"),
            PersistentDataType.STRING,
            "nothing"
        )
        meta.setCustomModelData(plugin.config.menus.mainMenuStatsItemModelData)

        item.itemMeta = meta
        return item
    }

    private fun getCloseItem(): ItemStack {
        val item = ItemStack(plugin.config.menus.mainMenuCloseItemMaterial, 1)
        val meta = item.itemMeta

        meta.displayName(
            MiniMessage.miniMessage().deserialize(
                PlaceholderAPI.setPlaceholders(player, plugin.config.menus.mainMenuCloseItemName)
            ).decoration(TextDecoration.ITALIC, false)
        )
        meta.lore(
            plugin.config.menus.mainMenuCloseItemLore.split("<newline>", "<br>")
            .map {
                MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, it))
                    .decoration(TextDecoration.ITALIC, false)
            }
        )
        //Make item uniquely identifiable for inventory click detection with ID
        meta.persistentDataContainer.set(
            NamespacedKey(plugin, "menu_item"),
            PersistentDataType.STRING,
            "close_menu"
        )
        meta.setCustomModelData(plugin.config.menus.mainMenuCloseItemModelData)

        item.itemMeta = meta
        return item
    }

    @EventHandler
    override fun onInventoryClick(e: InventoryClickEvent) {
        if (e.whoClicked.uniqueId != playerUUID) return

        val clickedInv = e.clickedInventory
        //Add null check in case player clicked outside of window
        if (clickedInv == null || e.inventory.getHolder(false) !is MainMenuInventory) return
        val player = e.whoClicked as? Player ?: return

        e.isCancelled = true

        e.currentItem?.let{
            handleClick(it, player)
        }
    }

    override fun handleClick(clickedItem: ItemStack, player: Player) {
        val menuItemId = clickedItem.itemMeta.persistentDataContainer
            .get(NamespacedKey(plugin, "menu_item"), PersistentDataType.STRING)
        when (menuItemId) {
            "fish_collection" -> handleFishCollectionItemClick(player)
            "close_menu" -> handleCloseMenuItemClick(player)
            "coming_soon" -> {}
        }
    }

    /**
     * Handles when a player clicks on the fish collection item in the menu.
     */
    private fun handleFishCollectionItemClick(player: Player) {
        //Because InventoryClickEvent occurs within a modification of the Inventory, Inventory related methods aren't safe to use,
        //so we schedule a tick later
        object : BukkitRunnable() {
            override fun run() {
                player.openInventory(FishCollectionInventory(player.uniqueId, playerData).inventory)
            }
        }.runTask(plugin)
    }

    /**
     * Handles when a player clicks on the close menu item.
     */
    private fun handleCloseMenuItemClick(player: Player) {
        //Because InventoryClickEvent occurs within a modification of the Inventory, Inventory related methods aren't safe to use,
        //so we schedule a tick later
        object : BukkitRunnable() {
            override fun run() {
                player.closeInventory()
            }
        }.runTask(plugin)
    }

    @EventHandler
    override fun onInventoryClose(e: InventoryCloseEvent) {
        if (e.player.uniqueId != playerUUID) return

        if (e.inventory.getHolder(false) !is MainMenuInventory) return

        unregisterEvents()
    }

    override fun getInventory() = inv
}