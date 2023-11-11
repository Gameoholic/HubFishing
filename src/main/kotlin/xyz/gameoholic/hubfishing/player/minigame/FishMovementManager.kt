package xyz.gameoholic.hubfishing.player.minigame

import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.fish.Fish
import xyz.gameoholic.hubfishing.fish.FishRarity
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.util.FishingUtil.getRarity
import kotlin.random.Random

/**
 * @param fishMinPosition The fish's min position in UI pixels, from the right
 * @param fishMaxPosition The fish's max position in UI pixels, from the right
 */
class FishMovementManager(
    private val fishMinPosition: Double,
    private val fishMaxPosition: Double,
    private val caughtFish: Fish
) {
    private val plugin: HubFishingPlugin by inject()

    private enum class FishDirection { LEFT, RIGHT }

    private var fishDirection: FishDirection = FishDirection.LEFT

    /** The amount of ticks the fish is heading in the current direction */
    private var fishDirectionTime = 0

    /** The maximum amount of ticks the fish will head in the current direction */
    private var fishMaxDirectionTime = 0

    /** The fish's position in UI pixels, from the right */
    var fishPosition = Random.nextDouble(
        fishMinPosition + plugin.config.fishingMinigame.waterAreaFishSpawnPadding,
        fishMaxPosition - plugin.config.fishingMinigame.waterAreaFishSpawnPadding
    )

    private val fishSpeed =
        if (getRarity(caughtFish.variant.rarityId).minigameMaxSpeed == getRarity(caughtFish.variant.rarityId).minigameMinSpeed)
            getRarity(caughtFish.variant.rarityId).minigameMinSpeed
        else
            Random.nextDouble(getRarity(caughtFish.variant.rarityId).minigameMinSpeed, getRarity(caughtFish.variant.rarityId).minigameMaxSpeed)

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
        fishMaxDirectionTime = determineFishDirectionDuration(getRarity(caughtFish.variant.rarityId))
        fishDirection = if (fishDirection == FishDirection.LEFT)
            FishDirection.RIGHT
        else
            FishDirection.LEFT
    }

    /**
     * Determines the amount of ticks the fish will move in a direction, based on its rarity.
     */
    private fun determineFishDirectionDuration(fishRarity: FishRarity): Int {
        return Random.nextInt(fishRarity.minigameMinDirectionDuration, fishRarity.minigameMaxDirectionDuration + 1)
    }
}