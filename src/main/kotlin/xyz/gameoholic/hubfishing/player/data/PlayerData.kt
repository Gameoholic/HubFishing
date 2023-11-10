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
 *
 * Upon initialization, loads data for this player from the database,
 * or creates an entry in the database in case there's no data.
 *
 * All parameters will be null until they've been properly retrieved
 * from the database.
 */
class PlayerData(
    val playerUUID: UUID,
    var xp: Int,
    var playtime: Int,
    var fishesCaught: HashMap<FishVariant, Int>,
    var fishesUncaught: HashMap<FishVariant, Int>
) {
    private val plugin: HubFishingPlugin by inject()

    var levelData = LevelUtil.getLevelData(xp)

    /**
     * Uploads the data of this PlayerData instance to the database.
     * @return Whether the operation succeeded or not.
     */
    fun uploadData(): Boolean {
        LoggerUtil.debug("Uploading player data for player $playerUUID")
        return (plugin.sqlManager.uploadPlayerData(this))
    }

    /**
     * Increases the playtime by a certain amount, and updates all displays
     * to match the new amount
     */
    fun increasePlaytime(amount: Int) {
        if (playtime == null) return
        playtime?.let { playtime = it + amount }
        plugin.playerDisplayManagers[playerUUID]?.updateDisplays()
    }

    /**
     * Increases the xp by a certain amount, and updates all displays
     * to match the new amount.
     * Levels up the player if needed.
     */
    fun increaseXP(amount: Int) {
        if (xp == null) return
        xp?.let {
            xp = it + amount
            levelData = LevelUtil.getLevelData(it + amount)
        }
        plugin.playerDisplayManagers[playerUUID]?.updateDisplays()
    }

    /**
     * Increases the fishes caught for a certain variant by a certain amount, and updates all displays
     * to match the new amount
     */
    fun increaseFishesCaught(fishVariant: FishVariant, amount: Int) {
        if (fishesCaught == null) return
        fishesCaught!![fishVariant]?.let { fishesCaught!![fishVariant] = it + amount }
        plugin.playerDisplayManagers[playerUUID]?.updateDisplays()
    }

    /**
     * Increases the fishes uncaught for a certain variant by a certain amount, and updates all displays
     * to match the new amount
     */
    fun increaseFishesUncaught(fishVariant: FishVariant, amount: Int) {
        if (fishesUncaught == null) return
        fishesUncaught!![fishVariant]?.let { fishesUncaught!![fishVariant] = it + amount }
        plugin.playerDisplayManagers[playerUUID]?.updateDisplays()
    }

    override fun toString(): String {
        return "PlayerData(playerUUID=$playerUUID, xp=$xp, playtime=$playtime, fishesCaught=$fishesCaught, fishesUncaught=$fishesUncaught, levelData=$levelData)"
    }
}