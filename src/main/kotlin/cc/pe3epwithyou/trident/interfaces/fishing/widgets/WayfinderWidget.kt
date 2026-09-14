package cc.pe3epwithyou.trident.interfaces.fishing.widgets

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.WayfinderStatus
import cc.pe3epwithyou.trident.utils.ProgressBar
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.TridentColor
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.offset
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.GridLayout
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.TextColor
import net.minecraft.resources.Identifier

class WayfinderWidget(
    private val wayfinderStatus: WayfinderStatus, private val themed: Themed
) : CompoundWidget(0, 0, 0, 0) {
    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    private data class Grotto(val icon: Identifier, val color: Int)

    private companion object {
        val GROTTOS = hashMapOf(
            "Temperate" to Grotto(
                Resources.mcc("island_interface/fishing/island/grotto_temperate"), 0x7ed85c
            ), "Tropical" to Grotto(
                Resources.mcc("island_interface/fishing/island/grotto_tropical"), 0xf095b5
            ), "Barren" to Grotto(
                Resources.mcc("island_interface/fishing/island/grotto_barren"), 0xff5f6e
            )
        )
    }

    private val grottoLerpColors = listOf(
        TridentColor(TextColor.GREEN.value),
        TridentColor(TextColor.YELLOW.value),
        TridentColor(0xFFA500),
        TridentColor(TextColor.RED.value),
    )

    fun grottoColorAt(percent: Float, progress: Float): Int {
        val color = TridentColor.lerpList(grottoLerpColors.reversed(), progress).color
        if (wayfinderStatus.hasGrotto) {
            return if (percent <= progress) color else 0x686969
        }
        return if (percent <= progress) 0x6cfe6e else 0x686969
    }

    private val fullLayout: Layout
        get() = GridLayout(themed.theme.dimensions.paddingInner) {
            val mcFont = minecraft().font
            val grotto = GROTTOS[wayfinderStatus.island]!!
            val islandName =
                Component.literal(" ${wayfinderStatus.island.uppercase()}").withColor(grotto.color)
                    .mccFont()
            val title = FontCollection.texture(grotto.icon).offset(y = 1f).append(islandName)

            StringWidget(title, mcFont).at(0, 0, settings = LayoutConstants.LEFT)
            StringWidget(progressLabelComponent(wayfinderStatus), mcFont).at(
                0, 1, settings = LayoutConstants.RIGHT
            )

            val currentProgress = if (wayfinderStatus.hasGrotto) {
                wayfinderStatus.grottoStability / 100f
            } else {
                wayfinderStatus.data / 2000f
            }

            fun colorFormatFunc(
                progress: Float, leftHalfPercent: Float, rightHalfPercent: Float
            ): Pair<Int, Int> {
                return grottoColorAt(leftHalfPercent, progress) to grottoColorAt(
                    rightHalfPercent, progress
                )
            }

            val progressComponent = ProgressBar.progressComponent(
                currentProgress, 40, 10, ::colorFormatFunc
            )

            StringWidget(progressComponent, mcFont).at(1, 0, 1, 2, LayoutConstants.LEFT)
        }

    private val compactLayout: Layout
        get() = GridLayout(themed.theme.dimensions.paddingInner) {
            val mcFont = minecraft().font
            val grotto = GROTTOS[wayfinderStatus.island]!!
            val islandName =
                Component.literal(" ${wayfinderStatus.island.uppercase()}").withColor(grotto.color)
                    .mccFont()
            val title = FontCollection.texture(grotto.icon).offset(y = 1f).append(islandName)
            if (wayfinderStatus.hasGrotto) {
                val percent = wayfinderStatus.grottoStability / 100f
                val color = TridentColor.lerpList(grottoLerpColors.reversed(), percent).color
                title.append(
                    Component.literal(" • ").withColor(TextColor.GRAY)
                )
                    .append(Component.literal("${wayfinderStatus.grottoStability}%").withColor(color))
            } else {
                title.append(Component.literal(" • ").withColor(TextColor.GRAY)).append(
                    Component.literal(wayfinderStatus.data.toString()).withColor(TextColor.WHITE)
                ).append(Component.literal("/2K").withColor(TextColor.GRAY))
            }

            StringWidget(title, mcFont).at(0, 0, settings = LayoutConstants.LEFT)
        }

    override val layout = if (Config.Fishing.wayfinderModuleCompact) compactLayout else fullLayout

    private fun progressLabelComponent(
        wayfinderStatus: WayfinderStatus,
    ): MutableComponent {
        if (wayfinderStatus.hasGrotto) return Component.literal("${wayfinderStatus.grottoStability}%")
            .withColor(
                TridentColor.lerpList(
                    grottoLerpColors.reversed(), wayfinderStatus.grottoStability / 100f
                ).color,
            )

        val progressPercentage = (wayfinderStatus.data.toDouble() / 2000) * 100
        val isCompleted = progressPercentage >= 100

        val c = Component.literal("${wayfinderStatus.data}")
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