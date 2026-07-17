package cc.pe3epwithyou.trident.interfaces.themes

import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.theme.DefaultTheme
import com.noxcrew.sheeplib.theme.Theme
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor

@Suppress("MagicNumber")
object MatchChatTheme : Theme by DefaultTheme {
    override val theme: Theme = this
    override val dialogBorders: Boolean = false
    override val colors = object : Theme.Colors by DefaultTheme.colors {
        override val dialogBackgroundAlt: Int
            get() = getOpacity()
        override val border: Int = 0x202020 opacity 0
    }

    private fun getOpacity(): Int {
        val opacityValue = minecraft().options?.textBackgroundOpacity()?.get() ?: 0.5
        if (opacityValue == 1.0) {
            return 0x000.opaqueColor()
        }
        return 0x000 opacity (255 * opacityValue).toInt()
    }
}