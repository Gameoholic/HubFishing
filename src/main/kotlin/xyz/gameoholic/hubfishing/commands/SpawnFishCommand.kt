package xyz.gameoholic.hubfishing.commands

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import xyz.gameoholic.hubfishing.fish.FishVariant
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject


/**
 * Used to spawn a fish manually in a lake.
 * Parameters for the command are all optional:
 * /spawnfish <variant_id> <alive_time>
 */
object SpawnFishCommand : CommandExecutor {
    private val plugin: HubFishingPlugin by inject()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return true
        val fishLakeManager =
            plugin.fishLakeManagers.firstOrNull { it.lakePlayers.any { lakePlayer -> lakePlayer.uuid == sender.uniqueId } }
        if (fishLakeManager == null) {
            sender.sendMessage(
                text().content("You can only use this command in a lake.").color(NamedTextColor.RED).build()
            )
            return true
        }

        //Arg 0 - fish variant ID
        var fishVariant: FishVariant? = null
        if (args != null && args.isNotEmpty()) {
            fishVariant = plugin.config.fishVariants.variants.firstOrNull { it.id == args[0] }
            if (fishVariant == null) {
                val fishVariantsString = plugin.config.fishVariants.variants.joinToString { it.id }
                sender.sendMessage(
                    text().content("Invalid variant ID! Valid variant ID's are: $fishVariantsString")
                        .color(NamedTextColor.RED).build()
                )
                return true
            }
        }

        //Arg 1 - fish alive time
        args.getOrNull(1)?.toIntOrNull() ?: run {
            sender.sendMessage(
                text().content("Invalid fish alive time provided! Must be whole number.")
                    .color(NamedTextColor.RED).build()
            )
            return true
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
                fishVariant.minigameHitboxWidth, fishVariant.minigameCharacter, fishVariant.minigameCharacterHeight,
                fishVariant.menuMaterial, fishVariant.menuModelData
            )
        }

        if (fishVariant != null && args[1] != null)
            fishLakeManager.spawnFish(fishVariant = fishVariant, fishAliveTime = args[1].toInt())
        else if (fishVariant != null)
            fishLakeManager.spawnFish(fishVariant = fishVariant)
        else
            fishLakeManager.spawnFish()
        return true
    }
}