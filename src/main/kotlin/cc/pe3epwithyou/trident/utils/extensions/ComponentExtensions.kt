package cc.pe3epwithyou.trident.utils.extensions

import cc.pe3epwithyou.trident.utils.TridentFont
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation

object ComponentExtensions {
    fun MutableComponent.withFont(font: ResourceLocation): MutableComponent = this.withStyle(
        Style.EMPTY
            .withFont(font)
    )

    fun MutableComponent.mccFont(font: String = "hud", offset: Int = 0): MutableComponent =
        this.withFont(TridentFont.getMCCFont(font, offset))

    fun MutableComponent.defaultFont(): MutableComponent = this.withFont(Style.DEFAULT_FONT)

    fun MutableComponent.withTridentFont(font: String = "icon", offest: Int = 0): MutableComponent =
        this.withFont(TridentFont.getTridentFont(font, offest))
}