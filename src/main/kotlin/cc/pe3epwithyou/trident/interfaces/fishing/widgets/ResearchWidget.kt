package cc.pe3epwithyou.trident.interfaces.fishing.widgets

import cc.pe3epwithyou.trident.state.Research
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

class ResearchWidget(
    research: Research,
    themed: Themed
) : CompoundWidget(0, 0, 0, 0) {
    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    companion object {
        val RESEARCH_ICONS = hashMapOf<String, ResourceLocation>(
            "Strong" to ResourceLocation.fromNamespaceAndPath(
                "mcc",
                "textures/island_interface/fishing/strong_research.png"
            ),
            "Wise" to ResourceLocation.fromNamespaceAndPath(
                "mcc",
                "textures/island_interface/fishing/wise_research.png"
            ),
            "Glimmering" to ResourceLocation.fromNamespaceAndPath(
                "mcc",
                "textures/island_interface/fishing/glimmering_research.png"
            ),
            "Greedy" to ResourceLocation.fromNamespaceAndPath(
                "mcc",
                "textures/island_interface/fishing/greedy_research.png"
            ),
            "Lucky" to ResourceLocation.fromNamespaceAndPath(
                "mcc",
                "textures/island_interface/fishing/lucky_research.png"
            )
        )
    }

    override val layout = GridLayout(themed.theme.dimensions.paddingInner) {
        val mcFont = Minecraft.getInstance().font
        val researchName = Component.literal(research.type.uppercase()).mccFont()
        WayfinderNameWidget(RESEARCH_ICONS[research.type], researchName, mcFont).atBottom(
            0,
            settings = LayoutConstants.LEFT
        )

        val progressPercentage = (research.progressThroughTier.toDouble() / research.totalForTier.toDouble()) * 100
        val progressBarComponent = ProgressBar.progressComponent(
            research.progressThroughTier.toFloat() / research.totalForTier.toFloat(),
            20,
            4
        )

        StringWidget(
            Component.literal("${research.tier - 1} ").defaultFont()
                .append(progressBarComponent)
                .append(Component.literal(" ${research.tier}").defaultFont().withStyle(ChatFormatting.WHITE))
                .append(
                    Component.literal(" (${round(progressPercentage * 10) / 10.0}%)").defaultFont()
                        .withStyle(ChatFormatting.GRAY)
                ),
            mcFont
        ).atBottom(0, settings = LayoutConstants.LEFT)
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
                sprite ?: RESEARCH_ICONS.values.first(),
                ICON_WIDTH,
                ICON_WIDTH
            ).blit(guiGraphics, x, y)
            guiGraphics.drawString(font, text, x + ICON_WIDTH + SPACE_ADVANCE, y, 0xFFFFFF.opaqueColor())
        }

        override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit
    }
}