package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withTridentFont
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.resources.ResourceLocation


object TridentFont {
    val TRIDENT_COLOR: Swatch = Swatch(
        baseColor = 0xc3e2fc, baseShadow = 0x2d4857, mutedColor = 0xb8befc, mutedShadow = 0x3e3e63
    )
    val TRIDENT_ACCENT: Swatch = Swatch(
        baseColor = 0x59fce8, baseShadow = 0x195257, mutedColor = 0x4ed1db, mutedShadow = 0x1c4957
    )

    val ERROR: Swatch = Swatch(
        baseColor = 0xff3366,
        baseShadow = 0x500f20,
        mutedColor = 0xff3366,
        mutedShadow = 0x500f20,
    )

    fun getMCCFont(font: String = "hud", offset: Int = 0): ResourceLocation {
        if (offset == 0) {
            return Resources.mcc(font)
        }
        return Resources.mcc("${font}_offset_${offset}")
    }

    fun getTridentFont(font: String = "icon", offset: Int = 0): ResourceLocation {
        if (offset == 0) {
            return Resources.trident(font)
        }
        return Resources.trident("${font}_offset_${offset}")
    }

    private val tridentPrefix: MutableComponent
        get() {
            val a = Component.literal("‹")
            val b = Component.literal("\uE000").withTridentFont("glyph")
            val c = Component.literal("› ")
            return a.append(b).append(c)
        }

    fun withPrefix(c: Component): MutableComponent {
        val white = TextColor.fromLegacyFormat(ChatFormatting.WHITE)
        val compStyle = c.toFlatList().firstOrNull()?.style
        val compColor = compStyle?.color ?: white

        var style = Style.EMPTY
        if (compColor == white) {
            // Color is default, we fall back to TRIDENT_COLOR
            style = TRIDENT_COLOR.baseStyle
        } else {
            // Use color from the component to match!
            style = style.withColor(compColor)
            if (compStyle?.shadowColor != null) style = style.withShadowColor(compStyle.shadowColor!!)
        }

        return tridentPrefix.withStyle(style).append(c)
    }

    data class Swatch(
        val baseColor: Int,
        val baseShadow: Int,
        val mutedColor: Int,
        val mutedShadow: Int,
    ) {
        val baseStyle: Style
            get() = Style.EMPTY.withColor(baseColor).withShadowColor(baseShadow.opaqueColor())
        val mutedStyle: Style
            get() = Style.EMPTY.withColor(mutedColor).withShadowColor(mutedShadow.opaqueColor())
    }
}