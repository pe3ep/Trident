package cc.pe3epwithyou.trident.interfaces.themes

import com.noxcrew.sheeplib.theme.DefaultTheme
import com.noxcrew.sheeplib.theme.StaticColorReference
import com.noxcrew.sheeplib.theme.Theme
import com.noxcrew.sheeplib.util.lighten
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor

@Suppress("MagicNumber")
object TabbedDialogTheme : Theme by DefaultTheme {
    override val theme: Theme = this
    override val dialogBorders: Boolean = false
    override val colors = object : Theme.Colors by DefaultTheme.colors {
        override val dialogBackgroundAlt: Int = 0x404040 opacity 128
        override val border: Int = 0x202020 opacity 0
    }
    override val dimensions: Theme.Dimensions = Theme.Dimensions(
        buttonWidth = 70,
        buttonHeight = 14,
        paddingInner = 1,
        paddingOuter = 1,
    )
    override val buttonStyles: Theme.ButtonStyles = Theme.ButtonStyles(
        standard = Theme.ButtonStyle(
            StaticColorReference(0x323232.opaqueColor()),
            StaticColorReference(0x323232.opaqueColor() lighten 0.25f),
            StaticColorReference(0x323232.opaqueColor() lighten -0.25f),
        ),

        positive = Theme.ButtonStyle(
            StaticColorReference(0x404040.opaqueColor()),
            StaticColorReference(0x404040.opaqueColor() lighten 0.25f),
            StaticColorReference(0x404040.opaqueColor() lighten -0.5f),
        ),

        negative = Theme.ButtonStyle(
            StaticColorReference(0xC21C1E.opaqueColor()),
            StaticColorReference(0xC21C1E.opaqueColor() lighten 0.25f),
            StaticColorReference(0xC21C1E.opaqueColor() lighten -0.25f),
        ),
    )
}