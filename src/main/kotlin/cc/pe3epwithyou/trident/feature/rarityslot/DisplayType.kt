package cc.pe3epwithyou.trident.feature.rarityslot

import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.network.chat.Component

enum class DisplayType : NameableEnum {
    OUTLINE {
        override fun getDisplayName(): Component = Component.literal("Outline")
    },
    U_SHAPED {
        override fun getDisplayName(): Component = Component.literal("U-Shaped")
    },
    FILL {
        override fun getDisplayName(): Component = Component.literal("Filled background")
    }
}