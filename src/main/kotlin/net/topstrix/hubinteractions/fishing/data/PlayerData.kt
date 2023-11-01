package net.topstrix.hubinteractions.fishing.data

import net.topstrix.hubinteractions.fishing.crate.Crate
import net.topstrix.hubinteractions.fishing.data.sql.SQLUtil
import net.topstrix.hubinteractions.fishing.fish.FishVariant
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import net.topstrix.hubinteractions.fishing.util.LoggerUtil
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
    var fishesCaught: HashMap<FishVariant, Int>? = null
    var fishesUncaught: HashMap<FishVariant, Int>? = null
    var crateShards: HashMap<Crate, Int>? = null
    var xp: Int? = null
    var levelData: LevelUtil.LevelData? = null
    var playtime: Int? = null


    /**
     * Fetches the data from the database, and updates this
     * instance of PlayerData with the data.
     * @return Whether the operation was successful or not.
     */
    suspend fun fetchData(): Boolean {
        SQLUtil.fetchPlayerData(this)
        xp?.let {
            levelData = LevelUtil.getLevelData(it)
        }

        return !(fishesCaught == null || fishesUncaught == null || xp == null || playtime == null
            || fishesCaught?.size != FishingUtil.fishingConfig.fishVariants.size ||
            fishesUncaught?.size != FishingUtil.fishingConfig.fishVariants.size ||
            crateShards?.size != FishingUtil.fishingConfig.crates.size
            )
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
        FishingUtil.playerDisplayManagers[playerUUID]?.updateDisplays()
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
        FishingUtil.playerDisplayManagers[playerUUID]?.updateDisplays()
    }

    /**
     * Increases the fishes caught for a certain variant by a certain amount, and updates all displays
     * to match the new amount
     */
    fun increaseFishesCaught(fishVariant: FishVariant, amount: Int) {
        if (fishesCaught == null) return
        fishesCaught!![fishVariant]?.let { fishesCaught!![fishVariant] = it + amount }
        FishingUtil.playerDisplayManagers[playerUUID]?.updateDisplays()
    }

    /**
     * Increases the fishes uncaught for a certain variant by a certain amount, and updates all displays
     * to match the new amount
     */
    fun increaseFishesUncaught(fishVariant: FishVariant, amount: Int) {
        if (fishesUncaught == null) return
        fishesUncaught!![fishVariant]?.let { fishesUncaught!![fishVariant] = it + amount }
        FishingUtil.playerDisplayManagers[playerUUID]?.updateDisplays()
    }

    /**
     * Increases the crate shards for a certain crate by a certain amount.
     */
    fun increaseCrateShards(crate: Crate, amount: Int) {
        if (crateShards == null) return
        crateShards!![crate]?.let { crateShards!![crate] = it + amount }
    }

    /**
     * Sets the crate shards for a certain crate to 0.
     */
    fun resetCrateShards(crate: Crate) {
        if (crateShards == null) return
        crateShards!![crate]?.let { crateShards!![crate] = 0 }
    }
}