package xyz.gameoholic.hubfishing

import org.bukkit.plugin.java.JavaPlugin

class HubFishingPlugin: JavaPlugin() {
    override fun onEnable() {
        HubFishing.onEnable(this)
    }
}