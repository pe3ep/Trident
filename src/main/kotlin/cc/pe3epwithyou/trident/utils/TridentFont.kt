package cc.pe3epwithyou.trident.utils

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation

object TridentFont {
    const val TRIDENT_COLOR: Int = 0xcfe3fc
    const val TRIDENT_ACCENT: Int = 0x54fcfc

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

    fun tridentPrefix(): MutableComponent {
        val a = Component.literal("<").withColor(TRIDENT_COLOR)
        val b = Component.literal("\uE000").withStyle(Style.EMPTY.withFont(getTridentFont("glyph")))
        val c = Component.literal("> ")
        return a.append(b).append(c).append(Component.empty().withStyle(ChatFormatting.RESET))
    }
}