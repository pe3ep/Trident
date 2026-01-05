package cc.pe3epwithyou.trident.utils.extensions

import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.TridentFont.SwatchType
import com.noxcrew.noxesium.core.util.OffsetStringFormatter
import net.minecraft.network.chat.Component
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
        swatch: TridentFont.Swatch, type: SwatchType = SwatchType.BASE
    ): MutableComponent = this.withStyle(type.getStyle(swatch))

    fun MutableComponent.offset(x: Float = 0f, y: Float = 0f): MutableComponent {
        val styledChild = this.copy().withStyle(Style.EMPTY.withInsertion(getOffset(x, y)))
        val root = Component.literal("").withStyle(Style.EMPTY)
        return root.append(styledChild)
    }

    private fun getOffset(x: Float, y: Float) = OffsetStringFormatter.write(
        OffsetStringFormatter.ComponentOffset(x, y)
    )
}