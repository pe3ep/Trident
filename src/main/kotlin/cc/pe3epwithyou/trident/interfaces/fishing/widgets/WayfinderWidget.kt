package cc.pe3epwithyou.trident.interfaces.fishing.widgets

import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.WayfinderStatus
import cc.pe3epwithyou.trident.utils.ProgressBar
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.offset
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.GridLayout
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import kotlin.math.round

class WayfinderWidget(
    wayfinderStatus: WayfinderStatus,
    themed: Themed
) : CompoundWidget(0, 0, 0, 0) {
    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    companion object {
        val ISLAND_ICONS = hashMapOf(
            "Temperate" to Resources.mcc("island_interface/fishing/island/grotto_temperate"),
            "Tropical" to Resources.mcc("island_interface/fishing/island/grotto_tropical"),
            "Barren" to Resources.mcc("island_interface/fishing/island/grotto_barren")
        )
    }

    override val layout = GridLayout(themed.theme.dimensions.paddingInner) {
        val mcFont = Minecraft.getInstance().font
        val islandName = Component.literal(" ${wayfinderStatus.island.uppercase()}").mccFont()
        StringWidget(
            FontCollection.texture(ISLAND_ICONS[wayfinderStatus.island]!!).offset(y = 0.5f)
                .append(islandName), mcFont
        ).atBottom(0, settings = LayoutConstants.LEFT)

        if (wayfinderStatus.hasGrotto) {
            val progress =
                Component.literal(" ${wayfinderStatus.grottoStability}% Stability")
                    .withStyle(
                        if (wayfinderStatus.grottoStability >= 50) {
                            ChatFormatting.GREEN
                        } else if (wayfinderStatus.grottoStability >= 20) {
                            ChatFormatting.YELLOW
                        } else {
                            ChatFormatting.RED
                        }
                    )
            val progressBarComponent =
                ProgressBar.progressComponent(
                    wayfinderStatus.grottoStability.toFloat() / 100f,
                    25,
                    5
                )

            StringWidget(progressBarComponent.append(progress), mcFont).atBottom(
                0,
                settings = LayoutConstants.LEFT
            )
        } else {
            val progressPercentage = (wayfinderStatus.data.toDouble() / 2000) * 100
            val progress =
                Component.literal(" ${wayfinderStatus.data}")
                    .withStyle(if (progressPercentage >= 100) ChatFormatting.GREEN else ChatFormatting.WHITE)
                    .append(
                        Component.literal("/2000 ")
                            .withStyle(if (progressPercentage >= 100) ChatFormatting.GREEN else ChatFormatting.GRAY)
                    )
                    .append(
                        Component.literal("(${round(progressPercentage * 10) / 10.0}%)")
                            .withStyle(if (progressPercentage >= 100) ChatFormatting.GREEN else ChatFormatting.GRAY)
                    )

            val progressBarComponent =
                ProgressBar.progressComponent((wayfinderStatus.data.toFloat() / 2000f), 25, 5)

            StringWidget(progressBarComponent.append(progress), mcFont).atBottom(
                0,
                settings = LayoutConstants.LEFT
            )
        }
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit
}