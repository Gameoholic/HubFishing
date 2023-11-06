package xyz.gameoholic.hubfishing.player.minigame

import xyz.gameoholic.hubfishing.fishing.fish.Fish
import xyz.gameoholic.hubfishing.fishing.fish.FishRarity
import xyz.gameoholic.hubfishing.fishing.player.minigame.FishingMinigameManager
import xyz.gameoholic.hubfishing.fishing.player.minigame.states.FishingMinigameMissState
import xyz.gameoholic.hubfishing.fishing.util.FishingUtil
import java.util.*

/**
 * @param fishMinPosition The fish's min position in UI pixels, from the right
 * @param fishMaxPosition The fish's max position in UI pixels, from the right
 */
class FishMovementManager(
    private val minigameManager: FishingMinigameManager,
    private val fishMinPosition: Double,
    private val fishMaxPosition: Double,
    private val caughtFish: Fish
) {
    private val rnd = Random()

    private enum class FishDirection { LEFT, RIGHT }

    private var fishDirection: FishDirection = FishDirection.LEFT

    /** The amount of ticks the fish is heading in the current direction */
    private var fishDirectionTime = 0

    /** The maximum amount of ticks the fish will head in the current direction */
    private var fishMaxDirectionTime = 0

    /** The fish's position in UI pixels, from the right */
    var fishPosition = rnd.nextDouble(
        fishMinPosition + FishingUtil.fishingConfig.waterAreaFishSpawnPadding,
        fishMaxPosition - FishingUtil.fishingConfig.waterAreaFishSpawnPadding
    )

    private val fishSpeed =
        if (caughtFish.variant.rarity.minigameMaxSpeed == caughtFish.variant.rarity.minigameMinSpeed)
            caughtFish.variant.rarity.minigameMinSpeed
        else
            rnd.nextDouble(caughtFish.variant.rarity.minigameMinSpeed, caughtFish.variant.rarity.minigameMaxSpeed)


    var heatmap = hashMapOf<Int, Double>()

    init {
//        for (i in 0 until FishingUtil.fishingConfig.waterAmount) {
//            heatmap[i] = 100.0 / FishingUtil.fishingConfig.waterAmount //14.2%
//        }
//        for (i in 0 until FishingUtil.fishingConfig.waterAmount / 2) {
//            val heatmapValue = heatmap[i]!!
//            heatmap[i] = heatmap[i]!! - heatmapValue / 2.0
//            heatmap[FishingUtil.fishingConfig.waterAmount - 1 - i] = heatmap[FishingUtil.fishingConfig.waterAmount - 1 - i]!! - heatmapValue / 2.0
//            for (j in i + 1 until FishingUtil.fishingConfig.waterAmount - 1 - i) {
//                heatmap[j] = heatmap[j]!! + heatmapValue / (FishingUtil.fishingConfig.waterAmount - 1 - i - i - 1)
//            }
//        }
////todo: heatmap code

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
            fishPosition -= fishSpeed
        else
            fishPosition += fishSpeed
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