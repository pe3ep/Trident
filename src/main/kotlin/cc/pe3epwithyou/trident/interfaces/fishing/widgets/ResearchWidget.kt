package cc.pe3epwithyou.trident.interfaces.fishing.widgets

import cc.pe3epwithyou.trident.feature.fishing.FishingType
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.Research
import cc.pe3epwithyou.trident.utils.ProgressBar
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
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

class ResearchWidget(
    research: Research, themed: Themed
) : CompoundWidget(0, 0, 0, 0) {
    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    companion object {
        fun getTexture(type: FishingType): Identifier =
            Resources.mcc("island_interface/fishing/${type.name.lowercase()}_research")
    }

    override val layout = GridLayout(themed.theme.dimensions.paddingInner) {
        val font = minecraft().font

        val component = FontCollection.texture(getTexture(research.type)).offset(y = 1f)
            .append(
                Component.literal(" ${research.type.name.uppercase()}").mccFont()
            )
        StringWidget(component, font).atBottom(0, settings = LayoutConstants.LEFT)

        val progress = research.progressThroughTier.toFloat() / research.totalForTier
        val progressBarComponent = ProgressBar.progressComponent(progress, 20, 4)

        StringWidget(
            Component.literal("${research.tier - 1} ").defaultFont().append(progressBarComponent)
                .append(
                    Component.literal(" ${research.tier}").defaultFont()
                        .withStyle(ChatFormatting.WHITE)
                ).append(
                    Component.literal(" (${"%.2f".format(progress * 100)}%)").defaultFont()
                        .withStyle(ChatFormatting.GRAY)
                ), font
        ).atBottom(0, settings = LayoutConstants.LEFT)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}