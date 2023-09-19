package net.topstrix.hubinteractions.fishing.fish

enum class FishRarity(val value: Int, val minigameMinDirectionDuration: Int, val minigameMaxDirectionDuration: Int) {
    COMMON(0, 30, 100),
    RARE(1, 20, 50),
    EPIC(2, 5, 10),
    LEGENDARY(3, 1, 5)
}

