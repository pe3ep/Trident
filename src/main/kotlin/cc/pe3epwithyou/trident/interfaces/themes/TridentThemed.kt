package cc.pe3epwithyou.trident.interfaces.themes

import cc.pe3epwithyou.trident.config.Config
import com.noxcrew.sheeplib.theme.Theme
import com.noxcrew.sheeplib.theme.Themed

object TridentThemed : Themed {
    override val theme: Theme get() = Config.Global.currentTheme.theme
}