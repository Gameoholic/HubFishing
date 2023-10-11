package net.topstrix.hubinteractions.fishing.menus

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.data.PlayerData
import net.topstrix.hubinteractions.fishing.fish.FishRarity
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.HashMap


class MainMenuInventory(private val playerUUID: UUID) : FishingInventory {
    override val inv: Inventory = Bukkit.createInventory(
        this,
        FishingUtil.fishingConfig.mainMenuSize,
        MiniMessage.miniMessage().deserialize(FishingUtil.fishingConfig.mainMenuName)
    )
    override var eventsAreRegistered = false

    private val playerData: PlayerData?

    init {
        registerEvents()

        playerData = FishingUtil.playerData.firstOrNull { it.playerUUID == playerUUID }

        getInventoryItems().entries.forEach {
            inv.setItem(it.key, it.value)
        }
    }

    override fun registerEvents() {
        if (eventsAreRegistered) return
        Bukkit.getPluginManager().registerEvents(this, HubInteractions.plugin)
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

        items[FishingUtil.fishingConfig.mainMenuFishCollectionItemIndex] = getFishCollectionItem()
        items[FishingUtil.fishingConfig.mainMenuRodCustomizationItemIndex] = getRodCustomizationItem()
        items[FishingUtil.fishingConfig.mainMenuRewardsItemIndex] = getRewardsItem()
        items[FishingUtil.fishingConfig.mainMenuStatsItemIndex] = getStatsItem()
        items[FishingUtil.fishingConfig.mainMenuCrateShardsItemIndex] = getCrateShardsItem()
        items[FishingUtil.fishingConfig.mainMenuCloseItemIndex] = getCloseItem()

        return items
    }

    private fun getFishCollectionItem(): ItemStack {
        val item = ItemStack(FishingUtil.fishingConfig.mainMenuFishCollectionItemMaterial, 1)
        val meta = item.itemMeta

        meta.displayName(
            MiniMessage.miniMessage().deserialize(FishingUtil.fishingConfig.mainMenuFishCollectionItemName)
                .decoration(TextDecoration.ITALIC, false)
        )
        meta.lore(FishingUtil.fishingConfig.mainMenuFishCollectionItemLore.split("<newline>", "<br>")
            .map {
                MiniMessage.miniMessage().deserialize(it).decoration(TextDecoration.ITALIC, false)
            }
        )
        //Make item uniquely identifiable for inventory click detection with ID
        meta.persistentDataContainer.set(
            NamespacedKey(HubInteractions.plugin, "menu_item"),
            PersistentDataType.STRING,
            "fish_collection"
        )
        meta.setCustomModelData(FishingUtil.fishingConfig.mainMenuFishCollectionItemModelData)

        item.itemMeta = meta
        return item
    }

    private fun getRodCustomizationItem(): ItemStack {
        val item = ItemStack(FishingUtil.fishingConfig.mainMenuRodCustomizationItemMaterial, 1)
        val meta = item.itemMeta

        meta.displayName(
            MiniMessage.miniMessage().deserialize(FishingUtil.fishingConfig.mainMenuRodCustomizationItemName)
                .decoration(TextDecoration.ITALIC, false)
        )
        meta.lore(FishingUtil.fishingConfig.mainMenuRodCustomizationItemLore.split("<newline>", "<br>")
            .map {
                MiniMessage.miniMessage().deserialize(it).decoration(TextDecoration.ITALIC, false)
            }
        )
        //Make item uniquely identifiable for inventory click detection with ID
        meta.persistentDataContainer.set(
            NamespacedKey(HubInteractions.plugin, "menu_item"),
            PersistentDataType.STRING,
            "coming_soon"
        )
        meta.setCustomModelData(FishingUtil.fishingConfig.mainMenuRodCustomizationItemModelData)

        item.itemMeta = meta
        return item
    }

    private fun getRewardsItem(): ItemStack {
        val item = ItemStack(FishingUtil.fishingConfig.mainMenuRewardsItemMaterial, 1)
        val meta = item.itemMeta

        meta.displayName(
            MiniMessage.miniMessage().deserialize(FishingUtil.fishingConfig.mainMenuRewardsItemName)
                .decoration(TextDecoration.ITALIC, false)
        )
        meta.lore(FishingUtil.fishingConfig.mainMenuRewardsItemLore.split("<newline>", "<br>")
            .map {
                MiniMessage.miniMessage().deserialize(it).decoration(TextDecoration.ITALIC, false)
            }
        )
        //Make item uniquely identifiable for inventory click detection with ID
        meta.persistentDataContainer.set(
            NamespacedKey(HubInteractions.plugin, "menu_item"),
            PersistentDataType.STRING,
            "coming_soon"
        )
        meta.setCustomModelData(FishingUtil.fishingConfig.mainMenuRewardsItemModelData)

        item.itemMeta = meta
        return item
    }

    private fun getStatsItem(): ItemStack {
        val item = ItemStack(FishingUtil.fishingConfig.mainMenuStatsItemMaterial, 1)
        val meta = item.itemMeta

        meta.displayName(
            MiniMessage.miniMessage().deserialize(FishingUtil.fishingConfig.mainMenuStatsItemName)
                .decoration(TextDecoration.ITALIC, false)
        )
        meta.lore(FishingUtil.fishingConfig.mainMenuStatsItemLore.split("<newline>", "<br>")
            .map {
                MiniMessage.miniMessage().deserialize(
                    it,
                    Placeholder.component(
                        "playtime",
                        text((playerData?.playtime?.let { playtime -> playtime / 3600 } ?: "Unknown").toString())
                    ),
                    Placeholder.component(
                        "xp",
                        text((playerData?.xp ?: "Unknown").toString())
                    ),
                    Placeholder.component(
                        "fishes_caught",
                        text((playerData?.fishesCaught?.values?.sum() ?: "Unknown").toString())
                    ),
                    Placeholder.component(
                        "common_fishes_caught",
                        text(
                            (playerData?.fishesCaught?.filterKeys { fish -> fish.rarity == FishRarity.COMMON }?.values?.sum()
                                ?: "Unknown").toString()
                        )
                    ),
                    Placeholder.component(
                        "rare_fishes_caught",
                        text(
                            (playerData?.fishesCaught?.filterKeys { fish -> fish.rarity == FishRarity.RARE }?.values?.sum()
                                ?: "Unknown").toString()
                        )
                    ),
                    Placeholder.component(
                        "epic_fishes_caught",
                        text(
                            (playerData?.fishesCaught?.filterKeys { fish -> fish.rarity == FishRarity.EPIC }?.values?.sum()
                                ?: "Unknown").toString()
                        )
                    ),
                    Placeholder.component(
                        "legendary_fishes_caught",
                        text(
                            (playerData?.fishesCaught?.filterKeys { fish -> fish.rarity == FishRarity.LEGENDARY }?.values?.sum()
                                ?: "Unknown").toString()
                        )
                    )
                ).decoration(TextDecoration.ITALIC, false)
            }
        )
        //Make item uniquely identifiable for inventory click detection with ID
        meta.persistentDataContainer.set(
            NamespacedKey(HubInteractions.plugin, "menu_item"),
            PersistentDataType.STRING,
            "nothing"
        )
        meta.setCustomModelData(FishingUtil.fishingConfig.mainMenuStatsItemModelData)

        item.itemMeta = meta
        return item
    }
    private fun getCrateShardsItem(): ItemStack {
        val item = ItemStack(FishingUtil.fishingConfig.mainMenuCrateShardsItemMaterial, 1)
        val meta = item.itemMeta

        meta.displayName(
            MiniMessage.miniMessage().deserialize(FishingUtil.fishingConfig.mainMenuCrateShardsItemName)
                .decoration(TextDecoration.ITALIC, false)
        )
        meta.lore(FishingUtil.fishingConfig.mainMenuCrateShardsItemLore.split("<newline>", "<br>")
            .map {
                MiniMessage.miniMessage().deserialize(it).decoration(TextDecoration.ITALIC, false)
            }
        )
        //Make item uniquely identifiable for inventory click detection with ID
        meta.persistentDataContainer.set(
            NamespacedKey(HubInteractions.plugin, "menu_item"),
            PersistentDataType.STRING,
            "crate_shards"
        )
        meta.setCustomModelData(FishingUtil.fishingConfig.mainMenuCrateShardsItemModelData)

        item.itemMeta = meta
        return item
    }

    private fun getCloseItem(): ItemStack {
        val item = ItemStack(FishingUtil.fishingConfig.mainMenuCloseItemMaterial, 1)
        val meta = item.itemMeta

        meta.displayName(
            MiniMessage.miniMessage().deserialize(FishingUtil.fishingConfig.mainMenuCloseItemName)
                .decoration(TextDecoration.ITALIC, false)
        )
        meta.lore(FishingUtil.fishingConfig.mainMenuCloseItemLore.split("<newline>", "<br>")
            .map {
                MiniMessage.miniMessage().deserialize(it).decoration(TextDecoration.ITALIC, false)
            }
        )
        //Make item uniquely identifiable for inventory click detection with ID
        meta.persistentDataContainer.set(
            NamespacedKey(HubInteractions.plugin, "menu_item"),
            PersistentDataType.STRING,
            "close_menu"
        )
        meta.setCustomModelData(FishingUtil.fishingConfig.mainMenuCloseItemModelData)

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

        val clickedItem: ItemStack = e.currentItem ?: return
        handleClick(clickedItem, player)
    }

    override fun handleClick(clickedItem: ItemStack, player: Player) {
        val menuItemId = clickedItem.itemMeta.persistentDataContainer
            .get(NamespacedKey(HubInteractions.plugin, "menu_item"), PersistentDataType.STRING)
        when (menuItemId) {
            "fish_collection" -> handleFishCollectionItemClick(player)
            "crate_shards" -> handleCrateShardsItemClick(player)
            "close_menu" -> handleCloseMenuItemClick(player)
            "coming_soon" -> {}
        }
    }

    private fun handleCrateShardsItemClick(player: Player) {
        //Because InventoryClickEvent occurs within a modification of the Inventory, Inventory related methods aren't safe to use,
        //so we schedule a tick later
        object : BukkitRunnable() {
            override fun run() {
                player.openInventory(CrateShardsInventory(player.uniqueId).inventory)
            }
        }.runTask(HubInteractions.plugin)
    }

    /**
     * Handles when a player clicks on the fish collection item in the menu.
     */
    private fun handleFishCollectionItemClick(player: Player) {
        //Because InventoryClickEvent occurs within a modification of the Inventory, Inventory related methods aren't safe to use,
        //so we schedule a tick later
        object : BukkitRunnable() {
            override fun run() {
                player.openInventory(FishCollectionInventory(player.uniqueId).inventory)
            }
        }.runTask(HubInteractions.plugin)
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
        }.runTask(HubInteractions.plugin)
    }

    @EventHandler
    override fun onInventoryClose(e: InventoryCloseEvent) {
        if (e.player.uniqueId != playerUUID) return

        if (e.inventory.getHolder(false) !is MainMenuInventory) return

        unregisterEvents()
    }

    override fun getInventory(): Inventory {
        return inv
    }
}