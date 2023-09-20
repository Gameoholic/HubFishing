package net.topstrix.hubinteractions.fishing.fish

enum class FishRarity(
    val value: Int,
    val minigameMinDirectionDuration: Int,
    val minigameMaxDirectionDuration: Int,
    val minigameSpeed: Double
) {
    COMMON(0, 30, 100, 0.5),
    RARE(1, 20, 50, 0.75),
    EPIC(2, 5, 25, 1.25),
    LEGENDARY(3, 5, 15, 1.5)
}

