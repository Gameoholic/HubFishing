package net.topstrix.hubinteractions.fishing.fish

enum class FishRarity(
    val value: Int,
    var minigameMinDirectionDuration: Int,
    var minigameMaxDirectionDuration: Int,
    var minigameSpeed: Double,
    var fishesRequiredToSpawnMin: Int,
    var fishesRequiredToSpawnMax: Int,
    var aliveTimeMin: Int,
    var aliveTimeMax: Int,
    var xp: Int,
) {
    COMMON(0, -1, -1, -1.0, -1, -1, -1, -1, -1),
    RARE(1, -1, -1, -1.0, -1, -1, -1, -1, -1),
    EPIC(2, -1, -1, -1.0, -1, -1, -1, -1, -1),
    LEGENDARY(3, -1, -1, -1.0, -1, -1, -1, -1, -1),
}



