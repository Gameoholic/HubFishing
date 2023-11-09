package xyz.gameoholic.hubfishing.menus

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import xyz.gameoholic.hubfishing.fish.FishVariant
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import java.util.UUID

class FishCollectionInventory(private val playerUUID: UUID) : FishingInventory {
    private val plugin: HubFishingPlugin by inject()

    val player = Bukkit.getPlayer(playerUUID)
    override val inv: Inventory = Bukkit.createInventory(
        this,
        plugin.config.menus.fishingCollectionMenuSize,
        MiniMessage.miniMessage().deserialize(
            PlaceholderAPI.setPlaceholders(player, plugin.config.menus.fishingCollectionMenuName)
        )
    )


    override var eventsAreRegistered = false

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

    init {
        registerEvents()

        getInventoryItems().entries.forEach {
            inv.setItem(it.key, it.value)
        }
    }

    /**
     * Generates the inventory contents for the fishing collection menu
     * @return The item indexes mapped to the item stacks
     */
    private fun getInventoryItems(): HashMap<Int, ItemStack> {
        val items = hashMapOf<Int, ItemStack>()

        val startIndex = plugin.config.menus.fishingCollectionMenuFishMinIndex
        val maxIndex = plugin.config.menus.fishingCollectionMenuFishMaxIndex

        //sort fishes caught by rarity
        val fishesCaught = plugin.playerData.firstOrNull { it.playerUUID == playerUUID }?.let {
            it.fishesCaught?.toSortedMap(compareBy<FishVariant> { variant -> variant.rarity.value }.thenBy { variant -> variant.id })
        } ?: return items
        var fishIndex = 0

        for (i in startIndex until maxIndex) {
            //Rectangular menu
            if (i % 9 > maxIndex % 9 || i % 9 < startIndex % 9) {
                continue
            }
            if (fishIndex >= fishesCaught.entries.size)
                break

            val fishVariantEntry = fishesCaught.entries.elementAt(fishIndex)
            val fishVariant = fishVariantEntry.key
            val timesCaught = fishVariantEntry.value

            var item: ItemStack
            var meta: ItemMeta
            if (timesCaught > 0) {
                item = ItemStack(plugin.config.menus.fishingCollectionMenuUndiscoveredFishMaterial, 1)
                meta = item.itemMeta
                meta.displayName(
                    MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, fishVariant.name))
                        .decoration(TextDecoration.ITALIC, false)
                )
                meta.lore(
                    plugin.config.menus.fishingCollectionMenuDiscoveredFishLore.split("<newline>", "<br>")
                    .map {
                        MiniMessage.miniMessage().deserialize(
                            PlaceholderAPI.setPlaceholders(player, it),
                            Placeholder.component("times_caught", text(timesCaught)),
                            Placeholder.component(
                                "rarity",
                                MiniMessage.miniMessage().deserialize(fishVariant.rarity.displayName)
                            )
                        ).decoration(TextDecoration.ITALIC, false)
                    }
                )
            } else {
                item = ItemStack(fishVariant.menuMaterial, 1)
                meta = item.itemMeta
                meta.displayName(
                    MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, fishVariant.name))
                        .decoration(TextDecoration.ITALIC, false)
                )
                meta.lore(
                    plugin.config.menus.fishingCollectionMenuUndiscoveredFishLore.split("<newline>", "<br>")
                    .map {
                        MiniMessage.miniMessage().deserialize(
                            PlaceholderAPI.setPlaceholders(player, it),
                            Placeholder.component(
                                "rarity",
                                MiniMessage.miniMessage().deserialize(
                                    PlaceholderAPI.setPlaceholders(player, fishVariant.rarity.displayName)
                                )
                            )
                        ).decoration(TextDecoration.ITALIC, false)
                    }
                )
                meta.setCustomModelData(fishVariant.menuModelData)
            }
            item.itemMeta = meta
            items[i] = item
            fishIndex++
        }
        items[plugin.config.menus.fishingCollectionMenuCloseItemIndex] = getCloseItem()

        return items
    }

    private fun getCloseItem(): ItemStack {
        val item = ItemStack(plugin.config.menus.fishingCollectionMenuCloseItemMaterial, 1)
        val meta = item.itemMeta

        meta.displayName(
            MiniMessage.miniMessage().deserialize(
                PlaceholderAPI.setPlaceholders(
                    player,
                    plugin.config.menus.fishingCollectionMenuCloseItemName
                )
            )
                .decoration(TextDecoration.ITALIC, false)
        )
        meta.lore(
            plugin.config.menus.fishingCollectionMenuCloseItemLore.split("<newline>", "<br>")
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
        meta.setCustomModelData(plugin.config.menus.fishingCollectionMenuCloseItemModelData)

        item.itemMeta = meta
        return item
    }

    @EventHandler
    override fun onInventoryClick(e: InventoryClickEvent) {
        if (e.whoClicked.uniqueId != playerUUID) return

        val clickedInv = e.clickedInventory
        //Add null check in case player clicked outside of window
        if (clickedInv == null || e.inventory.getHolder(false) !is FishCollectionInventory) return

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
            "coming_soon" -> {}
            "close_menu" -> handleCloseMenuItemClick(player)
        }
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

        if (e.inventory.getHolder(false) !is FishCollectionInventory) return

        unregisterEvents()
    }

    override fun getInventory(): Inventory = inv
}