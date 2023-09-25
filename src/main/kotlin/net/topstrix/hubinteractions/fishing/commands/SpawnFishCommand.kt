package net.topstrix.hubinteractions.fishing.commands

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.topstrix.hubinteractions.fishing.fish.FishVariant
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


/**
 * Used to spawn a fish manually in a lake.
 * Parameters for the command are all optional:
 * /spawnfish <variant_id> <alive_time>
 */
object SpawnFishCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return true
        val fishLakeManager = FishingUtil.fishLakeManagers.firstOrNull { it.allPlayers.contains(sender.uniqueId) }
        if (fishLakeManager == null) {
            sender.sendMessage(
                text().content("You can only use this command in a lake.").color(NamedTextColor.RED).build()
            )
            return true
        }

        //Arg 0 - fish variant ID
        var fishVariant: FishVariant? = null
        if (args != null && args.isNotEmpty()) {
            fishVariant = FishingUtil.fishingConfig.fishVariants.firstOrNull { it.id == args[0] }
            if (fishVariant == null) {
                var fishVariantsString = ""
                FishingUtil.fishingConfig.fishVariants.forEach {
                    fishVariantsString += "${it.id}, "
                }
                fishVariantsString = fishVariantsString.dropLast(2)
                sender.sendMessage(
                    text().content("Invalid variant ID! Valid variant ID's are: $fishVariantsString")
                        .color(NamedTextColor.RED).build()
                )
                return true
            }
        }

        //Arg 1 - fish alive time
        var fishAliveTime: Int? = null
        if (args != null && args.size > 1) {
            fishAliveTime = args[1].toIntOrNull()
            if (fishAliveTime == null) {
                sender.sendMessage(
                    text().content("Invalid fish alive time provided! Must be whole number.")
                        .color(NamedTextColor.RED).build()
                )
                return true
            }
        }

        //Arg 2 - fish speed
        if (args != null && args.size > 2 && fishVariant != null) {
            val fishSpeed = args[2].toDoubleOrNull()
            if (fishSpeed == null) {
                sender.sendMessage(
                    text().content("Invalid fish speed provided! Must be decimal number.")
                        .color(NamedTextColor.RED).build()
                )
                return true
            }
            //Clone fishvariant but change speed
            fishVariant = FishVariant(
                fishVariant.id, fishVariant.material, fishVariant.modelData, fishSpeed,
                fishVariant.rarity, fishVariant.name, fishVariant.hitboxSize, fishVariant.hitboxOffset,
                fishVariant.minigameHitboxWidth
            )
        }

        if (fishVariant != null && fishAliveTime != null)
            fishLakeManager.spawnFish(fishVariant = fishVariant, fishAliveTime = fishAliveTime)
        else if (fishVariant != null)
            fishLakeManager.spawnFish(fishVariant = fishVariant)
        else
            fishLakeManager.spawnFish()
        return true
    }
}