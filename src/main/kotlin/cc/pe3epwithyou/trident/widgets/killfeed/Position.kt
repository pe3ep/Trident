package cc.pe3epwithyou.trident.widgets.killfeed

import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.network.chat.Component

enum class Position : NameableEnum {
    LEFT {
        override fun getDisplayName(): Component = Component.literal("Left side")
    },
    RIGHT {
        override fun getDisplayName(): Component = Component.literal("Right side")
    }
}