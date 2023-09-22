package net.topstrix.hubinteractions.fishing.player.minigame.states.util

import net.topstrix.hubinteractions.fishing.fish.Fish
import net.topstrix.hubinteractions.fishing.fish.FishRarity
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import java.util.*
import kotlin.collections.HashMap

/**
 * @param fishMinPosition The fish's min position in UI pixels, from the right
 * @param fishMaxPosition The fish's max position in UI pixels, from the right
 */
class FishMovementManager(
    private val fishMinPosition: Double,
    private val fishMaxPosition: Double,
    private val caughtFish: Fish
) {
    private val rnd = Random()

    private enum class FishDirection {LEFT, RIGHT}
    private var fishDirection: FishDirection = FishDirection.LEFT

    /** The amount of ticks the fish is heading in the current direction */
    private var fishDirectionTime = 0
    /** The maximum amount of ticks the fish will head in the current direction */
    private var fishMaxDirectionTime = 0
    /** The fish's position in UI pixels, from the right */
    var fishPosition = fishMinPosition + (fishMaxPosition - fishMinPosition) / 2

    var heatmap = hashMapOf<Int, Double>()
    init {
        for (i in 0 until FishingUtil.fishingConfig.waterAmount) {
            heatmap[i] = 100.0 / FishingUtil.fishingConfig.waterAmount //14.2%
        }
        for (i in 0 until FishingUtil.fishingConfig.waterAmount / 2) {
            val heatmapValue = heatmap[i]!!
            heatmap[i] = heatmap[i]!! - heatmapValue / 2.0
            heatmap[FishingUtil.fishingConfig.waterAmount - 1 - i] = heatmap[FishingUtil.fishingConfig.waterAmount - 1 - i]!! - heatmapValue / 2.0
            for (j in i + 1 until FishingUtil.fishingConfig.waterAmount - 1 - i) {
                heatmap[j] = heatmap[j]!! + heatmapValue / (FishingUtil.fishingConfig.waterAmount - 1 - i - i - 1)
            }
        }


        println(heatmap)
    }
    /**
     * Determines and updates the fish position based on algorithm
     */
    fun updateFishPosition() {
        fishDirectionTime++
        if (fishDirectionTime >= fishMaxDirectionTime)
            switchFishDirection()
        moveFishInDirection()

        //Cap fish movement
        if (fishPosition > fishMaxPosition || fishPosition < fishMinPosition) {
            fishPosition = if (fishPosition > fishMaxPosition)
                fishMaxPosition
            else
                fishMinPosition
            switchFishDirection()
            //We don't want there to be a still frame when fish is at the edge, so we move in the new direction
            moveFishInDirection()
        }
    }

    /**
     * Moves fish in its current direction.
     */
    private fun moveFishInDirection() {
        if (fishDirection == FishDirection.RIGHT)
            fishPosition -= caughtFish.variant.rarity.minigameSpeed
        else
            fishPosition += caughtFish.variant.rarity.minigameSpeed
    }

    /**
     * Switches the fish's current direction.
     */
    private fun switchFishDirection() {
        fishDirectionTime = 0
        fishMaxDirectionTime = determineFishDirectionDuration(caughtFish.variant.rarity)
        fishDirection = if (fishDirection == FishDirection.LEFT)
            FishDirection.RIGHT
        else
            FishDirection.LEFT
    }

    /**
     * Determines the amount of ticks the fish will move in a direction, based on its rarity.
     */
    private fun determineFishDirectionDuration(fishRarity: FishRarity): Int {
        return rnd.nextInt(fishRarity.minigameMinDirectionDuration, fishRarity.minigameMaxDirectionDuration + 1)
    }
}