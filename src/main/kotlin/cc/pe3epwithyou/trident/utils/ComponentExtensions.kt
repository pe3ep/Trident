package cc.pe3epwithyou.trident.utils

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

object ComponentExtensions {
    fun MutableComponent.withHudMCC(offset: Int = 0): MutableComponent = this.withStyle(Style.EMPTY
        .withFont(TridentFont.getMCCFont(offset = offset))
    )
    fun MutableComponent.withDefault(): MutableComponent = this.withStyle(Style.EMPTY
        .withFont(Style.DEFAULT_FONT)
    )
}