package net.topstrix.hubinteractions.commands

import net.topstrix.hubinteractions.fishing.player.minigame.FishingMinigameManager
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

object TestCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (args != null && args.isNotEmpty()) {
            return true
        }
        for (i in 0 until 100)
            FishingUtil.fishLakeManagers[0].attemptFishSpawnCycle()
        return true
    }
}