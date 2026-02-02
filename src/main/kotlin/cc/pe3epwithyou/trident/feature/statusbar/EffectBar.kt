package cc.pe3epwithyou.trident.feature.statusbar

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.offset
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.Minecraft
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

    private val hiddenEffects = buildSet {
        add(Resources.minecraft("hunger"))
        add(Resources.minecraft("dolphins_grace"))
    }

    @JvmStatic
    fun render(graphics: GuiGraphics) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.effectBar) return
        if (MCCIState.game == Game.FISHING || MCCIState.game == Game.HUB) return

        val x = graphics.guiWidth() / 2
        val font = Minecraft.getInstance().font

        val c = Component.empty()
        val effects = getCurrentActiveEffects()
        if (effects.isEmpty()) return
        effects.forEachIndexed { index, effect ->
            if (index != 0) c.append(Component.literal(", "))
            c.append(effect.name())
        }
        val width = font.width(c) / 2
        graphics.drawString(font, c, x - width, graphics.guiHeight() - 80, 0xFFFFFF.opaqueColor())
    }

    fun getCurrentActiveEffects(): List<Effect> {
        val player = Minecraft.getInstance().player ?: return emptyList()
        val effects = mutableListOf<Effect>()
        player.activeEffects.forEach {
            val unwrappedId = it.effect.unwrapKey()
            val unwrappedMobEffect = it.effect.value()
            if (unwrappedId.isEmpty) return@forEach
            val id = unwrappedId.get().identifier()
            if (id in hiddenEffects) return@forEach

            // HITW check
            if (MCCIState.game == Game.HITW && id == Resources.minecraft("jump_boost")) {
                if (it.amplifier <= 2) return@forEach // Don't display the constant jump boost effect
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