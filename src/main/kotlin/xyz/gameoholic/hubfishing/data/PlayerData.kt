package xyz.gameoholic.hubfishing.data

import xyz.gameoholic.hubfishing.data.sql.SQLUtil
import xyz.gameoholic.hubfishing.fish.FishVariant
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.util.LoggerUtil
import java.util.*
import kotlin.collections.HashMap


//todo: if doesn't load but enter players fishing area, kick him.
/**
 * Stores fishing related player data.
 *
 * Upon initialization, loads data for this player from the database,
 * or creates an entry in the database in case there's no data.
 *
 * All parameters will be null until they've been properly retrieved
 * from the database.
 */
class PlayerData(val playerUUID: UUID) {
    private val plugin: HubFishingPlugin by inject()

    var fishesCaught: HashMap<FishVariant, Int>? = null
    var fishesUncaught: HashMap<FishVariant, Int>? = null
    var xp: Int? = null
    var levelData: LevelUtil.LevelData? = null
    var playtime: Int? = null


    /**
     * Fetches the data from the database, and updates this
     * instance of PlayerData with the data.
     * @return Whether the operation was successful or not.
     */
    fun fetchData(): Boolean {
        SQLUtil.fetchPlayerData(this)
        xp?.let {
            levelData = LevelUtil.getLevelData(it)
        }

        return !(fishesCaught == null || fishesUncaught == null || xp == null || playtime == null
            || fishesCaught?.size != plugin.config.fishVariants.size ||
            fishesUncaught?.size != plugin.config.fishVariants.size)
    }

    /**
     * Uploads the data of this PlayerData instance to the database.
     */
    suspend fun uploadData() {
        LoggerUtil.debug("Uploading player data for player ${playerUUID}")
        SQLUtil.uploadPlayerData(this) //todo: better db query wrapper. that returns null, or the result. so if null here we throw error. if not we say success.
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
}