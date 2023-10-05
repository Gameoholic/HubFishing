package net.topstrix.hubinteractions.fishing.menus

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack


interface FishingInventory: InventoryHolder, Listener {
    val inv: Inventory

    var eventsAreRegistered: Boolean
    fun registerEvents()
    fun unregisterEvents()
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent)
    /**
     * Handles when an item in this inventory menu is clicked.
     */
    fun handleClick(clickedItem: ItemStack, player: Player)
    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent)

}