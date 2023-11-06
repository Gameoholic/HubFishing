package xyz.gameoholic.hubfishing.menus

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import xyz.gameoholic.hubfishing.HubFishing
import xyz.gameoholic.hubfishing.util.FishingUtil
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
import java.util.*
import kotlin.collections.HashMap

class CrateShardsInventory(private val playerUUID: UUID) : FishingInventory {
    val player = Bukkit.getPlayer(playerUUID)
    override val inv: Inventory = Bukkit.createInventory(
        this,
        FishingUtil.fishingConfig.crateShardsMenuSize,
        MiniMessage.miniMessage()
            .deserialize(PlaceholderAPI.setPlaceholders(player, FishingUtil.fishingConfig.crateShardsMenuName))
    )

    override var eventsAreRegistered = false

    override fun registerEvents() {
        if (eventsAreRegistered) return
        Bukkit.getPluginManager().registerEvents(this, HubFishing.plugin)
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
     * Generates the inventory contents for the crate shards menu
     * @return The item indexes mapped to the item stacks
     */
    private fun getInventoryItems(): HashMap<Int, ItemStack> {
        val items = hashMapOf<Int, ItemStack>()

        val startIndex = FishingUtil.fishingConfig.crateShardsMenuCrateMinIndex
        val maxIndex = FishingUtil.fishingConfig.crateShardsMenuCrateMaxIndex

        //sort fishes caught by rarity
        val crates = FishingUtil.playerData.firstOrNull { it.playerUUID == playerUUID }?.crateShards ?: return items
        var crateIndex = 0

        for (i in startIndex until maxIndex) {
            //Rectangular menu
            if (i % 9 > maxIndex % 9 || i % 9 < startIndex % 9) {
                continue
            }

            if (crateIndex >= crates.entries.size)
                break

            val crateEntry = crates.entries.elementAt(crateIndex)
            val crate = crateEntry.key
            val crateShardAmount = crateEntry.value

            val item = ItemStack(crate.menuMaterial, 1)
            val meta = item.itemMeta
            meta.displayName(
                MiniMessage.miniMessage().deserialize(
                    PlaceholderAPI.setPlaceholders(player, FishingUtil.fishingConfig.crateShardsMenuCrateName),
                    Placeholder.component("crate", text(crate.displayName))
                ).decoration(TextDecoration.ITALIC, false)
            )
            meta.lore(
                FishingUtil.fishingConfig.crateShardsMenuCrateLore.split("<newline>", "<br>")
                    .map {
                        MiniMessage.miniMessage().deserialize(
                            PlaceholderAPI.setPlaceholders(player, it),
                            Placeholder.component("current_shards", text(crateShardAmount)),
                            Placeholder.component("required_shards", text(crate.amountOfShardsToCraft)),
                            Placeholder.component(
                                "shards_left",
                                text(crate.amountOfShardsToCraft - crateShardAmount)
                            ),
                            Placeholder.component("crate", text(crate.displayName)),
                        ).decoration(TextDecoration.ITALIC, false)
                    }
            )

            item.itemMeta = meta
            items[i] = item
            crateIndex++
        }
        items[FishingUtil.fishingConfig.crateShardsMenuCloseItemIndex] = getCloseItem()

        return items
    }

    private fun getCloseItem(): ItemStack {
        val item = ItemStack(FishingUtil.fishingConfig.crateShardsMenuCloseItemMaterial, 1)
        val meta = item.itemMeta

        meta.displayName(
            MiniMessage.miniMessage().deserialize(
                PlaceholderAPI.setPlaceholders(player, FishingUtil.fishingConfig.crateShardsMenuCloseItemName)
            )
                .decoration(TextDecoration.ITALIC, false)
        )
        meta.lore(
            FishingUtil.fishingConfig.crateShardsMenuCloseItemLore.split("<newline>", "<br>")
                .map {
                    MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, it))
                        .decoration(TextDecoration.ITALIC, false)
                }
        )
        //Make item uniquely identifiable for inventory click detection with ID
        meta.persistentDataContainer.set(
            NamespacedKey(HubFishing.plugin, "menu_item"),
            PersistentDataType.STRING,
            "close_menu"
        )
        meta.setCustomModelData(FishingUtil.fishingConfig.crateShardsMenuCloseItemModelData)

        item.itemMeta = meta
        return item
    }

    @EventHandler
    override fun onInventoryClick(e: InventoryClickEvent) {
        if (e.whoClicked.uniqueId != playerUUID) return

        val clickedInv = e.clickedInventory
        //Add null check in case player clicked outside of window
        if (clickedInv == null || e.inventory.getHolder(false) !is CrateShardsInventory) return

        val player = e.whoClicked as? Player ?: return

        e.isCancelled = true

        val clickedItem: ItemStack = e.currentItem ?: return
        handleClick(clickedItem, player)
    }

    override fun handleClick(clickedItem: ItemStack, player: Player) {
        val menuItemId = clickedItem.itemMeta.persistentDataContainer
            .get(NamespacedKey(HubFishing.plugin, "menu_item"), PersistentDataType.STRING)
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
        }.runTask(HubFishing.plugin)
    }


    @EventHandler
    override fun onInventoryClose(e: InventoryCloseEvent) {
        if (e.player.uniqueId != playerUUID) return

        if (e.inventory.getHolder(false) !is FishCollectionInventory) return

        unregisterEvents()
    }

    override fun getInventory(): Inventory {
        return inv
    }
}