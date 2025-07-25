package cc.pe3epwithyou.trident.dialogs.themes

import com.noxcrew.sheeplib.theme.Theme
import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.network.chat.Component

enum class TridentThemes(val theme: Theme) : NameableEnum {
    DEFAULT(DefaultTheme) {
        override fun getDisplayName(): Component = Component.literal("Default theme")
    },
    BORDERLESS(BorderlessTheme) {
        override fun getDisplayName(): Component = Component.literal("Borderless theme")
    },
    OPAQUE(OpaqueTheme) {
        override fun getDisplayName(): Component = Component.literal("Opaque theme")
    }
}