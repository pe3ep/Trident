package cc.pe3epwithyou.trident.interfaces.themes

import com.noxcrew.sheeplib.theme.Theme
import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.network.chat.Component

@Suppress("unused")
enum class TridentThemes(val theme: Theme) : NameableEnum {
    DEFAULT(DefaultTheme) {
        override fun getDisplayName(): Component = Component.literal("Default theme")
    },
    OPAQUE(OpaqueTheme) {
        override fun getDisplayName(): Component = Component.literal("Opaque theme")
    }
}