package cc.pe3epwithyou.trident.interfaces.fishing.widgets

import cc.pe3epwithyou.trident.state.WayfinderStatus
import cc.pe3epwithyou.trident.utils.ProgressBar
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.GridLayout
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import kotlin.math.round

class WayfinderWidget(
    wayfinderStatus: WayfinderStatus,
    themed: Themed
) : CompoundWidget(0, 0, 0, 0) {
    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    val ISLAND_ICONS = hashMapOf<String, ResourceLocation>(
        "Temperate" to ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/island/grotto_temperate.png"),
        "Tropical" to ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/island/grotto_tropical.png"),
        "Barren" to ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/island/grotto_barren.png")
    )

    override val layout = GridLayout(themed.theme.dimensions.paddingInner) {
        val mcFont = Minecraft.getInstance().font
        val islandName = Component.literal(wayfinderStatus.island.uppercase()).mccFont()

        val progressPercentage = (wayfinderStatus.data.toDouble() / 2000) * 100.00
        val progress = Component.literal(" ${wayfinderStatus.data} / 2000 (${round(progressPercentage)}%)")
            .defaultFont()
            .withStyle(if (progressPercentage >= 100) ChatFormatting.GREEN else ChatFormatting.GRAY)
        val progressBarComponent = ProgressBar.createProgressBarComponent((wayfinderStatus.data / 2000).toFloat() , 25, 5)

        WayfinderNameWidget(ISLAND_ICONS[wayfinderStatus.island], islandName, mcFont)
        StringWidget(progressBarComponent.append(progress), mcFont)
            .alignLeft()
            .atBottom(0, settings = LayoutConstants.LEFT)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }

    class WayfinderNameWidget(
        private val sprite: ResourceLocation?,
        private val text: Component,
        val font: Font
    ) : AbstractWidget(
        0, 0,
        font.width(text.visualOrderText) + ICON_WIDTH + SPACE_ADVANCE,
        9,
        text
    ) {
        companion object {
            private const val ICON_WIDTH = 8
            private const val SPACE_ADVANCE = 4
        }

        override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
            Texture(
                sprite ?: ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/island/grotto_temperate.png"),
                ICON_WIDTH,
                ICON_WIDTH
            ).blit(guiGraphics, x, y)
            guiGraphics.drawString(font, text, x + ICON_WIDTH + SPACE_ADVANCE, y, 0xFFFFFF.opaqueColor())
        }

        override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit
    }
}