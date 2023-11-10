package xyz.gameoholic.hubfishing.player.data

import xyz.gameoholic.hubfishing.fish.FishVariant
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.util.LevelUtil
import xyz.gameoholic.hubfishing.util.LoggerUtil
import java.util.*
import kotlin.collections.HashMap


/**
 * Stores fishing related player data.
 */
data class PlayerData(
    var xp: Int,
    var playtime: Int,
    var fishesCaught: HashMap<FishVariant, Int>,
    var fishesUncaught: HashMap<FishVariant, Int>
) {
    private val plugin: HubFishingPlugin by inject()

    var levelData = LevelUtil.getLevelData(xp)

    /**
     * Uploads the data of this PlayerData instance to the database.
     * @return The result of the operation.
     */
    fun uploadData(playerUUID: UUID): Result<Unit> {
        LoggerUtil.debug("Uploading player data for player $playerUUID")
        return (plugin.sqlManager.uploadPlayerData(this, playerUUID))
    }

    /**
     * Increases the playtime by a certain amount, and updates all displays
     * to match the new amount
     */
    fun increasePlaytime(amount: Int, playerUUID: UUID) {
        playtime += amount
        plugin.playerDisplayManagers[playerUUID]?.updateDisplays()
    }

    /**
     * Increases the xp by a certain amount, and updates all displays
     * to match the new amount.
     * Levels up the player if needed.
     */
    fun increaseXP(amount: Int, playerUUID: UUID) {
        xp += amount
        levelData = LevelUtil.getLevelData(xp)
        plugin.playerDisplayManagers[playerUUID]?.updateDisplays()
    }

    /**
     * Increases the fishes caught for a certain variant by a certain amount, and updates all displays
     * to match the new amount
     */
    fun increaseFishesCaught(fishVariant: FishVariant, amount: Int, playerUUID: UUID) {
        fishesCaught[fishVariant]?.let {
            fishesCaught[fishVariant] = it + amount
        }
        plugin.playerDisplayManagers[playerUUID]?.updateDisplays()
    }

    /**
     * Increases the fishes uncaught for a certain variant by a certain amount, and updates all displays
     * to match the new amount
     */
    fun increaseFishesUncaught(fishVariant: FishVariant, amount: Int, playerUUID: UUID) {
        fishesUncaught[fishVariant]?.let {
            fishesUncaught[fishVariant] = it + amount
        }
        plugin.playerDisplayManagers[playerUUID]?.updateDisplays()
    }

    override fun toString(): String {
        return "PlayerData(xp=$xp, playtime=$playtime, fishesCaught=$fishesCaught, fishesUncaught=$fishesUncaught, levelData=$levelData)"
    }
}