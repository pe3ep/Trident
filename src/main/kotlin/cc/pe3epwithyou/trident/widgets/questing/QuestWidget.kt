package cc.pe3epwithyou.trident.widgets.questing

import cc.pe3epwithyou.trident.utils.ComponentExtensions.withDefault
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
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
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

        private val COMPLETED_QUEST_SPRITE: ResourceLocation = ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/quest_log/quest_complete.png")
        private const val COMPLETED_QUEST_COLOR: Int = 0x1EFC00
    }

    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    override val layout = GridLayout(themed.theme.dimensions.paddingInner) {
        val mcFont = Minecraft.getInstance().font

        val c = Component.literal(quest.display_name.uppercase())
            .withHudMCC()
        if (quest.isCompleted) {
            c.withColor(COMPLETED_QUEST_COLOR)
        }
        val suffix = Component.literal(" " + quest.subtype.suffix)
            .withStyle(ChatFormatting.GRAY)
        QuestNameWidget(
            if (!quest.isCompleted) quest.sprite else COMPLETED_QUEST_SPRITE,
            c.append(suffix),
            mcFont
        ).atBottom(0, settings = LayoutConstants.LEFT)

        val progressComponent = progressComponent(
            quest.progress.toFloat() / quest.totalProgress.toFloat(),
            25,
            5
        )

        if (!quest.criteria.isTracked) {
            val progress = Component.literal(" ${quest.progress}/${quest.totalProgress} ⚠")
                .withDefault()
                .withStyle(ChatFormatting.GRAY)
            val w = StringWidget(progressComponent.append(progress), mcFont)
            w.setTooltip(Tooltip.create(Component.literal("""
                 Due to this quest's objective, Trident is unable to live-update the progress.
                 You can open the Journal to update it
            """.trimIndent())
                .withStyle(ChatFormatting.RESET)
                .withStyle(ChatFormatting.GRAY)
            ))
            w.atBottom(0)
            return@GridLayout
        }
        val progress = Component.literal(" ${quest.progress}/${quest.totalProgress}")
            .withDefault()
        if (quest.isCompleted) progress.withColor(COMPLETED_QUEST_COLOR)
        val w = StringWidget(progressComponent.append(progress), mcFont)
        w.atBottom(0)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
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
    fun progressComponent(
        progress: Float,
        width: Int,
        groups: Int = 0
    ): MutableComponent {
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

        override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
//            guiGraphics.fill(
//                x,
//                y,
//                x + this.getWidth(),
//                y + this.getHeight(),
//                0xFF0000.opaqueColor()
//            )
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