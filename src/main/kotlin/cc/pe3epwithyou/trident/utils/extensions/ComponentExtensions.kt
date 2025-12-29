package cc.pe3epwithyou.trident.utils.extensions

import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.TridentFont.SwatchType
import net.minecraft.network.chat.FontDescription
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier

object ComponentExtensions {
    fun MutableComponent.withFont(font: Identifier): MutableComponent = this.withStyle(
        Style.EMPTY.withFont(FontDescription.Resource(font))
    )

    fun MutableComponent.mccFont(font: String = "hud", offset: Int = 0): MutableComponent =
        this.withFont(TridentFont.getMCCFont(font, offset))

    fun MutableComponent.defaultFont(): MutableComponent =
        this.withFont(Identifier.withDefaultNamespace("default"))

    fun MutableComponent.withTridentFont(font: String = "icon", offest: Int = 0): MutableComponent =
        this.withFont(TridentFont.getTridentFont(font, offest))

    fun MutableComponent.withSwatch(
        swatch: TridentFont.Swatch,
        type: SwatchType = SwatchType.BASE
    ): MutableComponent =
        this.withStyle(type.getStyle(swatch))

}