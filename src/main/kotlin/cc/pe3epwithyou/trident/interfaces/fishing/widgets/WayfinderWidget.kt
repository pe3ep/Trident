package cc.pe3epwithyou.trident.interfaces.fishing.widgets

import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.WayfinderStatus
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.offset
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.GridLayout
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier

class WayfinderWidget(
    wayfinderStatus: WayfinderStatus,
    themed: Themed
) : CompoundWidget(0, 0, 0, 0) {
    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    private data class Grotto(val icon: Identifier, val color: Int)

    private companion object {
        val GROTTOS = hashMapOf(
            "Temperate" to Grotto(Resources.mcc("island_interface/fishing/island/grotto_temperate"), 0x7ed85c),
            "Tropical" to Grotto(Resources.mcc("island_interface/fishing/island/grotto_tropical"), 0xf095b5),
            "Barren" to Grotto(Resources.mcc("island_interface/fishing/island/grotto_barren"), 0xff5f6e)
        )
    }

    override val layout = GridLayout(themed.theme.dimensions.paddingInner) {
        val mcFont = minecraft().font
        val grotto = GROTTOS[wayfinderStatus.island]!!
        val islandName = Component.literal(" ${wayfinderStatus.island.uppercase()}").withColor(grotto.color).mccFont()
        val title = FontCollection.texture(grotto.icon).offset(y = 1f)
            .append(islandName)

        title.append(Component.literal("∙").withStyle(ChatFormatting.GRAY).defaultFont())
        title.append(progressLabelComponent(wayfinderStatus))

        StringWidget(title, mcFont).atBottom(0, settings = LayoutConstants.LEFT)
    }

    private fun progressLabelComponent(
        wayfinderStatus: WayfinderStatus,
    ): MutableComponent {
        if (wayfinderStatus.hasGrotto) return Component.literal("${wayfinderStatus.grottoStability}%")
            .withStyle(
                when {
                    wayfinderStatus.grottoStability >= 50 -> ChatFormatting.GREEN
                    wayfinderStatus.grottoStability >= 20 -> ChatFormatting.YELLOW
                    else -> ChatFormatting.RED
                }
            )

        val progressPercentage = (wayfinderStatus.data.toDouble() / 2000) * 100
        val isCompleted = progressPercentage >= 100

        val c = Component.literal(" ${wayfinderStatus.data}")
            .withStyle(if (isCompleted) ChatFormatting.GREEN else ChatFormatting.WHITE).append(
                Component.literal("/2K")
                    .withStyle(if (isCompleted) ChatFormatting.GREEN else ChatFormatting.GRAY)
            )

        return c
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit
}