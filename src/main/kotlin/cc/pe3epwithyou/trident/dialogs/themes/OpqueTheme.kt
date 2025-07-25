package cc.pe3epwithyou.trident.dialogs.themes

import com.noxcrew.sheeplib.theme.DefaultTheme
import com.noxcrew.sheeplib.theme.Theme
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor

@Suppress("MagicNumber")
object OpaqueTheme: Theme by DefaultTheme {
    override val theme: Theme = this

    override val colors = object : Theme.Colors by DefaultTheme.colors {
        override val dialogBackgroundAlt: Int = 0x1A1A1A.opaqueColor()
        override val border: Int = 0x2B2B2B.opaqueColor()
    }
}