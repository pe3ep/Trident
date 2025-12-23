package cc.pe3epwithyou.trident.interfaces.themes

import com.noxcrew.sheeplib.theme.DefaultTheme
import com.noxcrew.sheeplib.theme.Theme
import com.noxcrew.sheeplib.util.opacity

@Suppress("MagicNumber")
object DefaultTheme : Theme by DefaultTheme {
    override val theme: Theme = this
    override val dialogBorders: Boolean = false
    override val colors = object : Theme.Colors by DefaultTheme.colors {
        override val dialogBackgroundAlt: Int = 0x000000 opacity 128
        override val border: Int = 0x202020 opacity 0
    }
}