package cc.pe3epwithyou.trident.feature.statusbar

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.discord.EventActivity
import cc.pe3epwithyou.trident.mixin.accessors.GuiAccessor
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.offset
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.data.AtlasIds
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import java.util.*
import kotlin.math.floor

object EffectBar {
    private fun Identifier.toEffect(name: String) = Effect(name, this)

    private val CUSTOM_EFFECTS = buildMap {
        put(
            Resources.minecraft("luck"),
            Resources.trident("interface/effects/void").toEffect("Void Harming")
        )
    }

    private val globalHiddenEffects = buildSet {
        add(Resources.minecraft("hunger"))
        add(Resources.minecraft("dolphins_grace"))
        add(Resources.minecraft("night_vision"))
    }

    /**
     * Maps games to a set of effects that should be displayed or hidden based on the amplifier.
     *   - `Identifier`: Represents an identifier of the effect
     *   - `Int`: Represents the minimum effect amplifier required to display the effect.
     *     If the effect has a lower amplifier, it will not be displayed.
     *     If set to -1, the effect will always be hidden.
     */
    private val gameConstants: Map<Game, Map<Identifier, Int>> = buildMap {
        put(Game.ROCKET_SPLEEF_RUSH, buildMap {
            put(Resources.minecraft("jump_boost"), -1)
            put(Resources.minecraft("speed"), -1)
        })
        put(Game.HITW, buildMap {
            put(Resources.minecraft("jump_boost"), 3)
        })
        put(Game.DYNABALL, buildMap {
            put(Resources.minecraft("haste"), -1)
        })
    }

    @JvmStatic
    fun render(graphics: GuiGraphics) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.effectBar) return
        if (MCCIState.game == Game.FISHING) return

        val x = graphics.guiWidth() / 2
        val font = minecraft().font

        val c = Component.empty()
        val effects = getCurrentActiveEffects()
        if (effects.isEmpty()) return
        effects.forEachIndexed { index, effect ->
            if (index != 0) c.append(Component.literal(", "))
            c.append(effect.name())
        }
        val width = font.width(c) / 2
        val offset = if (checkEliminationBanner()) 62 else 0
        graphics.drawString(font, c, x - width, graphics.guiHeight() - 86 - offset, 0xFFFFFF.opaqueColor())
    }

    fun getCurrentActiveEffects(): List<Effect> {
        val player = minecraft().player ?: return emptyList()
        val effects = mutableListOf<Effect>()
        player.activeEffects.forEach forEachEffect@{
            val unwrappedId = it.effect.unwrapKey()
            val unwrappedMobEffect = it.effect.value()
            if (unwrappedId.isEmpty) return@forEachEffect
            val id = unwrappedId.get().identifier()
            if (id in globalHiddenEffects) return@forEachEffect

            val currentGame = MCCIState.game
            gameConstants.forEach { (game, pairs) ->
                if (currentGame == game) {
                    pairs.forEach { (effect, amplifier) ->
                        if (effect == id) {
                            if (amplifier == -1) return@forEachEffect
                            if (it.amplifier < amplifier) return@forEachEffect
                        }
                    }
                }
            }

            EventActivity.fetchedActivities?.forEach { activity ->
                activity.noxesiumServer.let { nox ->
                    if (MCCIState.currentServer != nox.server) return@forEach
                    if (MCCIState.gameTypes != nox.types) return@forEach
                }
                if (!activity.showEffectBar) {
                    return@forEachEffect
                }
            }

            if (id in CUSTOM_EFFECTS) {
                val effect = CUSTOM_EFFECTS[id]!!
                effect.apply {
                    duration = it.duration
                    amplifier = it.amplifier
                }

                effects.add(effect)
            } else {
                effects.add(
                    Effect(
                        unwrappedMobEffect.displayName.string, id.withPrefix("mob_effect/"),
                        AtlasIds.GUI,
                        duration = it.duration,
                        amplifier = it.amplifier
                    )
                )
            }
        }
        return effects
    }

    private fun checkEliminationBanner(): Boolean {
        val actionBar = (minecraft().gui as GuiAccessor).overlayMessageString ?: return false
        val strings = listOf("ELIMINATION", "RAMPAGE", "SPECTATING")
        return strings.any { actionBar.string.contains(it, ignoreCase = true) }
    }

    data class Effect(
        val name: String,
        val icon: Identifier,
        val textureAtlas: Identifier = AtlasIds.BLOCKS,
        var duration: Int = 0,
        var amplifier: Int = 0
    ) {
        fun name(): Component =
            FontCollection.texture(icon, textureAtlas).withoutShadow().offset(y = 1f)
                .append(" $name: ${formattedDuration()}")

        fun formattedDuration(): String {
            if (duration == -1) return "∞"
            val ms = duration * 50
            val totalSeconds = ms / 1000.0

            return when {
                totalSeconds < 5.0 -> {
                    String.format(Locale.US, "%.1fs", totalSeconds)
                }

                totalSeconds < 60.0 -> {
                    "${floor(totalSeconds).toInt()}s"
                }

                else -> {
                    val minutes = (totalSeconds / 60).toInt()
                    val seconds = (totalSeconds % 60).toInt()
                    "${minutes}m ${seconds.toString().padStart(2, '0')}s"
                }
            }
        }
    }
}