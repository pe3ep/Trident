package cc.pe3epwithyou.trident.widgets.questing

import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.ComponentExtensions.withHudMCC
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.TridentFont
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
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class QuestWidget(
    quest: Quest,
    themed: Themed
) : CompoundWidget(0, 0, 0, 0) {
    companion object {
        private const val PROGRESS_BLANK = ''
        private const val PROGRESS_HALF = ''
        private const val PROGRESS_FULL = ''

        private val segmentStyle = Style.EMPTY.withFont(TridentFont.getMCCFont("icon"))
        private val spaceStyle = Style.EMPTY.withFont(ResourceLocation.withDefaultNamespace("padding"))
        // Prebuilt Component literals to avoid per-character allocation
        private val COMP_BLANK: Component = Component.literal(PROGRESS_BLANK.toString()).withStyle(segmentStyle)
        private val COMP_HALF: Component = Component.literal(PROGRESS_HALF.toString()).withStyle(segmentStyle)
        private val COMP_FULL: Component = Component.literal(PROGRESS_FULL.toString()).withStyle(segmentStyle)
        private val COMP_SPACE: Component = Component.literal("\uE001").withStyle(spaceStyle)
    }

    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    override val layout = GridLayout(themed.theme.dimensions.paddingInner) {
        val mcFont = Minecraft.getInstance().font

//        StringWidget(Component.literal("yi"), mcFont).atBottom(0, 2, LayoutConstants.LEFT)
        val c = Component.literal(quest.display_name.uppercase()).withHudMCC()
        QuestNameWidget(
            quest.sprite,
            c,
            mcFont
        ).at(0, 0, colSpan = 2, settings = LayoutConstants.LEFT)

        val progressComponent = progressComponent(
            quest.progress.toFloat() / quest.totalProgress.toFloat(),
            25,
            5
        )
        StringWidget(progressComponent, mcFont).at(1, 0)
        StringWidget(Component.literal("${quest.progress}/${quest.totalProgress}"), mcFont).alignLeft().at(1, 1, settings = LayoutConstants.LEFT)
    }

    /**
     * Build a progress bar Component using preallocated Component pieces.
     *
     * @param progress 0.0..1.0 progress fraction
     * @param width number of characters in the bar (excludes grouping spaces)
     * @param groups number of groups to split the width into; when > 0 a
     *   space is inserted after each group (e.g. width=20, divisions=4 ->
     *   space every 5 chars). If <= 0 no spaces.
     */
    private fun progressComponent(
        progress: Float,
        width: Int,
        groups: Int = 0
    ): Component {
        if (width <= 0) return Component.empty()

        val subPerChar = 2 // so we have empty/half/full
        val clamped = progress.coerceIn(0f, 1f)
        val totalSubUnits = width * subPerChar
        val filledSubUnits = (clamped * totalSubUnits).roundToInt()

        val groupSize = if (groups > 0) max(1, width / groups) else Int.MAX_VALUE

        var component = Component.empty()
        for (i in 0 until width) {
            val startUnit = i * subPerChar
            val remain = max(0, min(subPerChar, filledSubUnits - startUnit))

            val piece = when {
                remain >= subPerChar -> COMP_FULL
                remain * 2 >= subPerChar -> COMP_HALF
                else -> COMP_BLANK
            }

            component = component.append(piece)

            if (groups > 0 && (i + 1) % groupSize == 0 && i != width - 1) {
                component = component.append(COMP_SPACE)
            }
        }

        return component
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }

    class QuestNameWidget(
        private val sprite: ResourceLocation,
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
        init {
            ChatUtils.sendMessage("""
                font.width(text.visualOrderText): ${font.width(text.visualOrderText)}
                font.width(text.visualOrderText) + ICON_WIDTH + SPACE_ADVANCE: ${font.width(text.visualOrderText) + ICON_WIDTH + SPACE_ADVANCE}
                text: ${text.string}
                sprite: ${sprite.path}
            """.trimIndent())
        }
        override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
            Texture(
                sprite,
                ICON_WIDTH,
                ICON_WIDTH
            ).blit(guiGraphics, x, y)
            guiGraphics.drawString(font, text, x + ICON_WIDTH + SPACE_ADVANCE, y, 0xFFFFFF.opaqueColor())
        }

        override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit
    }
}