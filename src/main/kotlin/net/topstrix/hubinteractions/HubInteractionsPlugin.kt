package net.topstrix.hubinteractions

import org.bukkit.plugin.java.JavaPlugin

class HubInteractionsPlugin: JavaPlugin() {
    override fun onEnable() {
        HubInteractions.onEnable(this)
    }
}