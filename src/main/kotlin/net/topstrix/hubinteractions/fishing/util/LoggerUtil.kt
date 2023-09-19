package net.topstrix.hubinteractions.fishing.util
import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import java.util.*

object LoggerUtil {

    enum class LogLevel(val value: Int) {
        NOTHING(2),
        ERROR(1),
        DEBUG(0)
    }
    fun debug(msg: String) {
        if (FishingUtil.fishingConfig.logLevel.value <= LogLevel.DEBUG.value)
            HubInteractions.plugin.logger.info("[Fishing] DEBUG: $msg")
    }
    fun error(msg: String) {
        if (FishingUtil.fishingConfig.logLevel.value <= LogLevel.ERROR.value)
            HubInteractions.plugin.logger.info("[Fishing] ERROR: $msg")
    }
}