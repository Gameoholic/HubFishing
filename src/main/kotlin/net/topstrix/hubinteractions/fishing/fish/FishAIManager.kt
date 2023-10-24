package net.topstrix.hubinteractions.fishing.fish

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class FishAIManager(val fish: Fish) {

    private var state: State = State.CONSTANT_SPEED

    private var velocity = 0.1
    private var acceleration = 0.0
    private var maxSpeed = 0.2
    private var minSpeed = 0.01
    private var timeLeftUntilStateSwitch = 40

    private lateinit var nextLocation: Location

    private var startRotation = 0f
    private var endRotation = 0f

    /**
     * The speed at which the fish rotates per tick, in yaw degrees.
     */
    private val rotationSpeed = 10f

    /**
     * The remaining rotation (yaw) the fish has to do. If set to 0, fish is not rotating, or has finished rotating.
     */
    private var remainingRotation = 0f

    enum class State {
        ACCELERATING,
        DECELERATING,
        CONSTANT_SPEED,
        CLOSE_TO_DESTINATION,
        AT_DESTINATION,
        ROTATING
    }

    init {
        generateNextLocation()
        endRotation = generateNextRotation()
        teleportFish(endRotation)
    }


    /**
     * Handles the fish rotation & movement.
     */
    fun onTick() {
        if (state == State.ROTATING) {
            rotateFish()
            // If fish's remaining rotation is 0, it means it should stop rotating.
            if (remainingRotation == 0f) {
                // On stop rotation:
                updateNonRotationState() // This sets a new state, that isn't ROTATION
            }
            return
        }

        // Update to any AI state that isn't ROTATING:
        updateNonRotationState()

        when (state) {
            State.AT_DESTINATION -> {
                state = State.ROTATING
                velocity = 0.0

                generateNextLocation()
                startRotation = fish.armorStand.yaw
                endRotation = generateNextRotation()
            }

            State.ACCELERATING -> {
                velocity += acceleration
            }

            State.DECELERATING -> {
                velocity += acceleration
            }
            State.CLOSE_TO_DESTINATION -> { // When close to destination, will act similarly to DECELERATING
                velocity += acceleration
                velocity = max(velocity, 0.025)
            }

            else -> {}
        }

        velocity = min(velocity, maxSpeed)
        velocity = max(velocity, minSpeed)


        // Give fish needed velocity
        val currentLocation = fish.armorStand.location
        val direction = nextLocation.clone().subtract(currentLocation).toVector().normalize()
        val finalVelocity = direction.multiply(velocity)
        fish.armorStand.velocity = finalVelocity

    }

    /**
     * Updates the AI state if needed to anything that isn't ROTATING.
     * If the state is ROTATING when calling the method, will force the state to switch.
     */
    private fun updateNonRotationState() {
        // If method was called during rotation, it means rotation has ended.
        if (state == State.ROTATING) {
            determineStateSwitchState()
            return
        }

        val currentLocation = fish.armorStand.location
        val distanceFromDestination = currentLocation.clone().distanceSquared(nextLocation)

        if (distanceFromDestination <= 0.2) {
            state = State.AT_DESTINATION
            return
        }
        else if (distanceFromDestination <= 5.0) {
            state = State.CLOSE_TO_DESTINATION
            determineStateSwitchState() // Special case: We want a specific velocity for this
            return
        }

        if (timeLeftUntilStateSwitch <= 0) {
            determineStateSwitchState()
        }
        else
            timeLeftUntilStateSwitch--
    }

    /**
     * Determines the non-ROTATION state
     */
    private fun determineStateSwitchState() {
        if (state == State.ROTATING) {
            state = State.ACCELERATING
            acceleration = 0.005
            return
        }

        if (state == State.CLOSE_TO_DESTINATION) {
            acceleration = -0.005
            return
        }

        if (state == State.ACCELERATING) {
            state = State.CONSTANT_SPEED
            return
        }

        if (state == State.DECELERATING) {
            state = State.CONSTANT_SPEED
            return
        }

        if (state == State.CONSTANT_SPEED) {
            var rnd = Random.nextInt(2)
            if (rnd == 0) {
                state = State.ACCELERATING
                acceleration = 0.005
            }
            else {
                state = State.DECELERATING
                acceleration = -0.005
            }
        }

        timeLeftUntilStateSwitch = Random.nextInt(10, 40)
    }


    /**
     * Calculates the needed yaw rotation for the fish to be facing
     * towards its next destination.
     * @return The yaw value of the max (required) armor stand rotation
     */
    private fun generateNextRotation(): Float {
        val currentLocation = fish.armorStand.location

        var direction = nextLocation.clone().subtract(currentLocation).toVector().normalize()
        val xAngleRadians = direction.angle(Vector(0, 0, 1))
        var xAngleDegrees = Math.toDegrees(xAngleRadians.toDouble()).toFloat()

        //Because it calculates angle to (0, 0, 1), the angle is always between 0-180 and positive, it's mirrored. Therefore, we manually check if the vector's x component is positive, and negate the angle so it's pointed at the correct side.
        var yaw = xAngleDegrees
        if (direction.x > 0)
            yaw *= -1
        return yaw
    }

    /**
     * Rotates the fish from startRotation to endRotation, based on rotationProgress.
     */
    private fun rotateFish() {
        // Yaws in the 0..360 range instead of -180..180 range
        var absoluteStartYaw = startRotation
        if (absoluteStartYaw < 0)
            absoluteStartYaw += 360
        var absoluteEndYaw = endRotation
        if (absoluteEndYaw < 0)
            absoluteEndYaw += 360

        // Determine rotation direction
        var clockWise = false
        var amount: Float
        if (absoluteEndYaw > absoluteStartYaw) {
            val clockWiseAmount = absoluteEndYaw - absoluteStartYaw
            val counterClockWiseAmount = absoluteStartYaw + (360 - absoluteEndYaw)
            if (clockWiseAmount < counterClockWiseAmount) {
                clockWise = true
                amount = clockWiseAmount
            } else
                amount = counterClockWiseAmount
        } else {
            val clockWiseAmount = absoluteEndYaw + (360 - absoluteStartYaw)
            val counterClockWiseAmount = absoluteStartYaw - absoluteEndYaw
            if (clockWiseAmount < counterClockWiseAmount) {
                clockWise = true
                amount = clockWiseAmount
            } else
                amount = counterClockWiseAmount
        }

        if (remainingRotation == 0f) { // If the rotation has just started, will be set to 0
            remainingRotation = amount
        }
        remainingRotation -= rotationSpeed

        // If yaw rotation has reached, or almost reached the end yaw, stop rotation.
        if (remainingRotation.absoluteValue <= rotationSpeed) {
            remainingRotation = 0f
            teleportFish(endRotation)
            return
        }

        val sign = if (amount == 0f)
            0f
        else
            amount / (amount.absoluteValue) // sign = 1/-1
        // Determine yaw from direction
        val yaw = if (clockWise)
            fish.armorStand.location.yaw + sign * rotationSpeed
        else
            fish.armorStand.location.yaw - sign * rotationSpeed


        teleportFish(yaw)
    }

    private fun teleportFish(yaw: Float) {
        val currentLocation = fish.armorStand.location
        fish.armorStand.teleport(
            Location(
                currentLocation.world,
                currentLocation.x,
                fish.fishLakeManager.armorStandYLevel, //water effects Y level, we teleport to the wanted Y level
                currentLocation.z,
                yaw,
                0f
            )
        )
    }

    /**
     * Generates a new location for the fish to move to. Location will be
     * at least 1 block further away from its current one.
     */
    private fun generateNextLocation() {
        val currentLocation = fish.armorStand.location
        do {
            val x = fish.fishLakeManager.spawnCorner1.blockX + fish.fishLakeManager.rnd.nextInt(fish.fishLakeManager.spawnCorner2.blockX - fish.fishLakeManager.spawnCorner1.blockX + 1)
            val z = fish.fishLakeManager.spawnCorner1.blockZ + fish.fishLakeManager.rnd.nextInt(fish.fishLakeManager.spawnCorner2.blockZ - fish.fishLakeManager.spawnCorner1.blockZ + 1)
            nextLocation = Location(currentLocation.world, x.toDouble(), currentLocation.y, z.toDouble())
        } while (currentLocation.clone().distanceSquared(nextLocation) <= 1.0)
    }
}