package xyz.gameoholic.hubfishing.fishing.util
import xyz.gameoholic.hubfishing.HubFishing
import xyz.gameoholic.hubfishing.fishing.util.FishingUtil
import java.util.*

object LoggerUtil {

    enum class LogLevel(val value: Int) {
        NOTHING(3),
        ERROR(2),
        INFO(1),
        DEBUG(0)
    }
    fun debug(msg: String) {
        if (FishingUtil.fishingConfig.logLevel.value <= LogLevel.DEBUG.value)
            HubFishing.plugin.logger.info("[Fishing] DEBUG: $msg")
    }
    fun error(msg: String) {
        if (FishingUtil.fishingConfig.logLevel.value <= LogLevel.ERROR.value)
            HubFishing.plugin.logger.info("[Fishing] ERROR: $msg")
    }
}