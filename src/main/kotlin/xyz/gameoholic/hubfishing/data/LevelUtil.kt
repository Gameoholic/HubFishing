package xyz.gameoholic.hubfishing.data

import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject

object LevelUtil {
    private val plugin: HubFishingPlugin by inject()

    /**
     * @param level The current level of the player, starting from 1.
     * @param remainderXP How much XP the player has after deducting the amount from the levels.
     * @param neededXPToLevelUp The total amount of remainder XP needed to level up.
     * @param remainingXPToLevelUp The total amount of remainder XP needed to level up, minus the remainder XP the player already has.
     */
    data class LevelData(val level: Int, val remainderXP: Int, val neededXPToLevelUp: Int, val remainingXPToLevelUp: Int)

    /**
     * Calculates the level, remainder XP and XP needed to level up given an amount of XP.
     * @return The level data given an amount of XP.
     */
    fun getLevelData(xp: Int): LevelData {
        var remainingXP = xp
        var level = 1
        var levelUpRequiredXP = plugin.config.levelInitialXPRequirement
        var levelUpGrowthXP = plugin.config.levelXPRequirementGrowth
        while (remainingXP >= levelUpRequiredXP) {
            level++
            remainingXP -= levelUpRequiredXP
            if (level % plugin.config.levelGrowthDelay == 0)
                levelUpGrowthXP = (levelUpGrowthXP * plugin.config.levelXPRequirementGrowthMultiplier).toInt()
            levelUpRequiredXP += levelUpGrowthXP
            if (levelUpRequiredXP > plugin.config.levelXPRequirementGrowthCap)
                levelUpRequiredXP = plugin.config.levelXPRequirementGrowthCap
        }
        return LevelData(level, remainingXP, levelUpRequiredXP, levelUpRequiredXP - remainingXP)
    }
}