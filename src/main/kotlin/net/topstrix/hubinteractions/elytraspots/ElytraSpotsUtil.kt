package net.topstrix.hubinteractions.elytraspots

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.MiniMessage
import net.topstrix.hubinteractions.HubInteractions
import net.topstrix.hubinteractions.elytraspots.config.ElytraSpotsConfig
import net.topstrix.hubinteractions.elytraspots.config.ElytraSpotsFileParser
import net.topstrix.hubinteractions.elytraspots.listeners.EntityToggleGlideListener
import net.topstrix.hubinteractions.elytraspots.listeners.PlayerMoveListener
import net.topstrix.hubinteractions.elytraspots.listeners.PlayerQuitListener
import net.topstrix.hubinteractions.shared.particles.ElytraSpotParticle
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

object ElytraSpotsUtil {
    lateinit var elytraSpotsConfig: ElytraSpotsConfig
    var playersWithElytra = mutableListOf<Player>()


    fun onEnable() {
        elytraSpotsConfig = ElytraSpotsFileParser.parseFile()

        ElytraSpotRunnable().runTaskTimer(HubInteractions.plugin, 0L, 1L)
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener, HubInteractions.plugin)
        Bukkit.getPluginManager().registerEvents(EntityToggleGlideListener, HubInteractions.plugin)
        Bukkit.getPluginManager().registerEvents(PlayerMoveListener, HubInteractions.plugin)

        // Delay particle start until partigon has fully loaded
        object: BukkitRunnable() {
            override fun run() {
                elytraSpotsConfig.elytraSpots.forEach {
                    ElytraSpotParticle.startParticle(it.location)
                }
            }
        }.runTask(HubInteractions.plugin)
    }

    fun activateElytra(player: Player, elytraSpot: ElytraSpot) {
        player.addPotionEffect(
            PotionEffect(
                PotionEffectType.LEVITATION,
                elytraSpot.levitationDuration,
                elytraSpot.levitationAmplifier,
                false,
                false
            )
        )
        player.inventory.chestplate = ItemStack(Material.ELYTRA, 1)
        player.sendActionBar(MiniMessage.miniMessage().deserialize(elytraSpotsConfig.elytraActivateMessage))
        if (elytraSpotsConfig.elytraActivateSound != "")
            player.playSound(Sound.sound(Key.key(elytraSpotsConfig.elytraActivateSound), Sound.Source.MASTER, 1f, 1f))
        if (!playersWithElytra.contains(player))
            playersWithElytra += player
    }
    fun deactivateElytra(player: Player) {
        player.inventory.chestplate = null
        playersWithElytra.remove(player)
        player.sendActionBar(MiniMessage.miniMessage().deserialize(elytraSpotsConfig.elytraDeactivateMessage))
        if (elytraSpotsConfig.elytraDeactivateSound != "")
            player.playSound(Sound.sound(Key.key(elytraSpotsConfig.elytraDeactivateSound), Sound.Source.MASTER, 1f, 1f))
    }
}