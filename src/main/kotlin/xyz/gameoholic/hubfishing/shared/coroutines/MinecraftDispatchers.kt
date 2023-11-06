package xyz.gameoholic.hubfishing.shared.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import net.minecraft.Util

/** Some coroutine dispatchers, backed by Minecraft's own thread pools. */
object MinecraftDispatchers {

    /** A coroutine dispatcher, backed by [Util.backgroundExecutor]. */
    val Background: CoroutineDispatcher = Util.backgroundExecutor().asCoroutineDispatcher()

    /** A coroutine dispatcher, backed by [Util.ioPool]. Use this for blocking operations. */
    val IO: CoroutineDispatcher = Util.ioPool().asCoroutineDispatcher()
}