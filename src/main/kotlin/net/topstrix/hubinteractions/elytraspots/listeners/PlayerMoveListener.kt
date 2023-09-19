package net.topstrix.hubinteractions.elytraspots.listeners

import net.topstrix.hubinteractions.elytraspots.ElytraSpotsUtil
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffectType

object PlayerMoveListener: Listener {

    @EventHandler
    fun onEntityMoveEvent(e: PlayerMoveEvent) {
        //todo: move this to task
        val player = e.player
        if (!ElytraSpotsUtil.playersWithElytra.contains(player)) return
        if (e.to.block.getRelative(BlockFace.DOWN).type == Material.AIR ||
            player.hasPotionEffect(PotionEffectType.LEVITATION)) return
        ElytraSpotsUtil.deactivateElytra(player)
    }
}