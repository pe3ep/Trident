package cc.pe3epwithyou.trident.dialogs.themes

import com.noxcrew.sheeplib.theme.DefaultTheme
import com.noxcrew.sheeplib.theme.Theme
import com.noxcrew.sheeplib.util.opacity

@Suppress("MagicNumber")
object BorderlessTheme: Theme by DefaultTheme {
    override val theme: Theme = this
    override val dialogBorders: Boolean = false
    override val colors = object : Theme.Colors by DefaultTheme.colors {
        override val dialogBackgroundAlt: Int = 0x111111 opacity 191
        override val border: Int = 0x202020 opacity 0
    }
}