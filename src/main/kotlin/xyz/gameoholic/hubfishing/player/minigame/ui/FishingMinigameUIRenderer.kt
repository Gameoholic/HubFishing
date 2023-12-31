package xyz.gameoholic.hubfishing.player.minigame.ui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import xyz.gameoholic.hubfishing.player.minigame.FishingMinigameState
import org.bukkit.Bukkit
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.injection.inject
import java.text.DecimalFormat
import java.time.Duration
import java.util.UUID

/**
 * Represents a UI Renderer, which renders characters on a player's
 * title.
 */
abstract class FishingMinigameUIRenderer {
    private val plugin: HubFishingPlugin by inject()

    abstract val minigameState: FishingMinigameState

    /**
     * The extra width of the long rod in pixels. The rod is longer to make the animation smoother and cover 'holes',
     * but a side effect is that the last position is X pixels to the right. So we discard the last X pixels for the long rod.
     */
    val longRodExtraWidth = 1
    /** The difference in positions between every mini fishing rod character, in UI pixels from the right*/
    protected val minFishingRodsOffset = plugin.config.fishingMinigame.miniRodCharacterHeight
    /** The position of the big fishing rod, in UI pixels from the right */

    /**
     * Renders the fishing minigame UI to the player.
     */
    abstract fun render()

    /**
     * Appends a character with an offset without affecting the already centered text.
     * This renders the character on a "separate layer".
     * This method assumes there are already characters before this one in the component.
     * @param component The component builder.
     * @param character The character to append.
     * @param leftOffset The position of the character to the left in relation to its current position
     * in the component, in UI pixels.
     * @param characterHeight The height (size) of the unicode character, in UI pixels.
     */
    protected fun renderCharacterSeparately(component: TextComponent.Builder, character: Char, leftOffset: Double, characterHeight: Int, removeShadow: Boolean = true) {
        /**
         * Minecraft adds a space after characters, so we add negative space
         * between them of -1, it does not need to be offset.
         * This assumes that there are characters before this one, otherwise there will be
         * a negative space for no reason, which will move the text to the left by 1 pixel.
         *
         * We then move the character to the left with negative space, according
         * to the leftOffset parameter.
         */
        renderSpace(component, -1 -leftOffset)
        renderCharacter(component, character, removeShadow)
        /**
         * Since we added a new character to the right of the text, Minecraft will
         * now move the entire text to the left, so it's centered.
         * Because we want to render the character separately, we offset
         * the character's height (size) to the left, so the text
         * is unaffected by the character being there. It's as if
         * it's on a new layer.
         *
         * Because we moved the character to the left, Minecraft will now
         * move the entire text to the right, so it's centered.
         * For the same reason as before, we'll add positive space
         * to the right, to offset the character's movement to the left.
         */
        renderSpace(component, leftOffset - characterHeight)
    }

    /**
     * Appends spaces to the component.
     * @param component The component builder.
     * @param width The width of the space in UI pixels.
     */
    private fun renderSpace(component: TextComponent.Builder, width: Double) {
        var widthWhole = width.toInt()
        val widthFraction = width - widthWhole
        val decFormat = DecimalFormat("##.00") //Fix bug where due to double precision values like 0.949999999996 (0.85) are treated as 0.84
        var fractionForHundred = (decFormat.format(widthFraction).toDouble() * 100).toInt()

        component.append(
            Component.translatable("space.$widthWhole")
        )
        if (fractionForHundred != 0)
            component.append(
                Component.translatable("space.${fractionForHundred}/100")
            )
    }

    /**
     * Appends a character to the component a number of times, with a color that removes the shadow from them.
     * There will be negative spaces between the characters, so they're not separated.
     * @param component The component builder
     * @param character The character to append
     * @param amount The amount of characters to append
     * @param removeShadow Whether to remove the shadow from the characters
     */
    protected fun renderCharacters(component: TextComponent.Builder, character: Char, amount: Int, removeShadow: Boolean = true) {
        /** We don't want there to be spaces between the characters, so we add
         *  a space after each water character (excluding the last one).
         */
        repeat(amount - 1) {
            renderCharacter(component, character, removeShadow)
            renderSpace(component, -1.0)
        }
        renderCharacter(component, character, removeShadow)
    }

    /**
     * Appends a character to the component, with a color that removes the shadow from it.
     * @param component The component builder
     * @param character The character to append
     * @param removeShadow Whether to remove the shadow from the character
     */
    fun renderCharacter(component: TextComponent.Builder, character: Char, removeShadow: Boolean = true) {
        /** The color 0x4E5C24 removes the shadow from the character */
        if (removeShadow)
            component.append(
                text(character)).color(TextColor.color(0x4E5C24)
            )
        else
            component.append(text(character).color(TextColor.color(0xFFFFFF)))
    }

    /**
     * Renders and displays the component on the player's title.
     * @param playerUUID The UUID of the player to display the title for
     * @param component The component to display
     */
    protected fun display(playerUUID: UUID, component: TextComponent.Builder) {
        Bukkit.getPlayer(playerUUID)?.let {
            it.sendTitlePart(
                TitlePart.TIMES,
                Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(10000), Duration.ofMillis(0))
            )
            it.sendTitlePart(
                TitlePart.TITLE,
                component.build()
            )
        }
    }
}