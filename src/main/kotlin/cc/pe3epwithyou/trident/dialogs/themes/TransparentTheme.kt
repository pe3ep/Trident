package cc.pe3epwithyou.trident.dialogs.themes

import com.noxcrew.sheeplib.theme.DefaultTheme
import com.noxcrew.sheeplib.theme.Theme
import com.noxcrew.sheeplib.util.opacity

@Suppress("MagicNumber")
object TransparentTheme: Theme by DefaultTheme {
    private val TRANSPARENT = 0x000000 opacity 0

    override val theme: Theme = this
    override val dialogBorders: Boolean = false
    override val colors = object : Theme.Colors by DefaultTheme.colors {
        override val dialogBackgroundAlt: Int = TRANSPARENT
        override val dialogBackground: Int = TRANSPARENT
        override val widgetBackgroundPrimary: Int = TRANSPARENT
        override val widgetBackgroundSecondary: Int = TRANSPARENT
    }
}