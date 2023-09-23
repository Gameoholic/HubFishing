package net.topstrix.hubinteractions.fishing.data

import net.topstrix.hubinteractions.fishing.data.sql.SQLUtil
import net.topstrix.hubinteractions.fishing.fish.FishVariant
import java.util.*
import kotlin.collections.HashMap


/**
 * Stores fishing related player data.
 *
 * Upon initialization, loads data for this player from the database,
 * or creates an entry in the database in case there's no data.
 */
class PlayerData(val playerUUID: UUID) {

    //Can be null, or empty if data isn't valid. Remember
    var fishesCaught: HashMap<FishVariant, Int>? = null
    var fishesUncaught: HashMap<FishVariant, Int>? = null
    var xp: Int? = null
    var playtime: Int? = null

    init {
        SQLUtil.fetchPlayerFishingData(this)
    }

}