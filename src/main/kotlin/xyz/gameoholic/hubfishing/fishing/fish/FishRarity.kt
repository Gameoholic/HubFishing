package xyz.gameoholic.hubfishing.fishing.fish

enum class FishRarity(
    val value: Int,
    var minigameMinDirectionDuration: Int,
    var minigameMaxDirectionDuration: Int,
    var minigameMinSpeed: Double,
    var minigameMaxSpeed: Double,
    var fishesRequiredToSpawnMin: Int,
    var fishesRequiredToSpawnMax: Int,
    var aliveTimeMin: Int,
    var aliveTimeMax: Int,
    var xp: Int,
    var displayName: String,
    var crateShardChance: Double
) {
    COMMON(0, -1, -1, -1.0, -1.0, -1, -1, -1, -1, -1, "", 0.0),
    RARE(1, -1, -1, -1.0, -1.0, -1, -1, -1, -1, -1, "", 0.0),
    EPIC(2, -1, -1, -1.0, -1.0, -1, -1, -1, -1, -1, "", 0.0),
    LEGENDARY(3, -1, -1, -1.0, -1.0, -1, -1, -1, -1, -1, "", 0.0),
}



