package xyz.gameoholic.hubfishing.elytraspots

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class ElytraSpotRunnable : BukkitRunnable() { //todo: fix this?
    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (ElytraSpotsUtil.playersWithElytra.contains(player) && !ElytraSpotsUtil.elytraSpotsConfig.allowSpotReuse) continue

            val elytraSpot = ElytraSpotsUtil.elytraSpotsConfig.elytraSpots.firstOrNull {
                player.location.x > it.location.x - it.radius && player.location.x < it.location.x + it.radius &&
                    player.location.z > it.location.z - it.radius && player.location.z < it.location.z + it.radius &&
                    player.location.y == it.location.y
            } ?: continue

            ElytraSpotsUtil.activateElytra(player, elytraSpot)
        }
    }
}