package xyz.gameoholic.hubfishing.util

import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject

object LoggerUtil {

    private val plugin: HubFishingPlugin by inject()
    enum class LogLevel(val value: Int) {
        NOTHING(3),
        ERROR(2),
        INFO(1),
        DEBUG(0)
    }
    fun debug(msg: String) {
        if (plugin.config.fishing.logLevel.value <= LogLevel.DEBUG.value)
            plugin.logger.info("[Fishing] DEBUG: $msg")
    }
    fun error(msg: String) {
        if (plugin.config.fishing.logLevel.value <= LogLevel.ERROR.value)
            plugin.logger.info("[Fishing] ERROR: $msg")
    }
}