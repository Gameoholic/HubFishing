package net.topstrix.hubinteractions.fishing.fish

import net.topstrix.hubinteractions.fishing.util.FishingUtil

enum class FishRarity(
    val value: Int,
    var minigameMinDirectionDuration: Int,
    var minigameMaxDirectionDuration: Int,
    var minigameSpeed: Double,
    var fishesRequiredToSpawnMin: Int,
    var fishesRequiredToSpawnMax: Int
) {
    COMMON(0, -1, -1, -1.0, -1, -1),
    RARE(1, -1, -1, -1.0, -1, -1),
    EPIC(2, -1, -1, -1.0, -1, -1),
    LEGENDARY(3, -1, -1, -1.0, -1, -1),
}



