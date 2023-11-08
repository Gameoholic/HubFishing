package xyz.gameoholic.hubfishing.util

fun interface Delegate<out T> {
    fun get(): T
    operator fun getValue(thisRef: Any?, property: Any): T = get()
}