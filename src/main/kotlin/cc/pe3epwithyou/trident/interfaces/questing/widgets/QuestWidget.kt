package cc.pe3epwithyou.trident.interfaces.questing.widgets

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.questing.Quest
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.feature.questing.QuestSubtype
import cc.pe3epwithyou.trident.utils.ProgressBar
import cc.pe3epwithyou.trident.utils.Resources
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
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation

class QuestWidget(
    quest: Quest,
    themed: Themed
) : CompoundWidget(0, 0, 0, 0) {
    companion object {
        private val COMPLETED_QUEST_SPRITE: ResourceLocation =
            Resources.mcc("textures/island_interface/quest_log/quest_complete.png")
        private const val COMPLETED_QUEST_COLOR: Int = 0x1EFC00
    }

    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    override val layout = GridLayout(themed.theme.dimensions.paddingInner) {
        val mcFont = Minecraft.getInstance().font

        val c = Component.literal(quest.displayName.uppercase())
            .mccFont()
        if (Config.Questing.rarityColorName) {
            c.withColor(quest.rarity.color)
        }
        if (quest.isCompleted) {
            c.withColor(COMPLETED_QUEST_COLOR)
            c.withStyle(ChatFormatting.ITALIC)
        }
        var suffixString = " "
        val dailyRemaining = QuestStorage.dailyRemaining
        val weeklyRemaining = QuestStorage.weeklyRemaining

        if (quest.subtype == QuestSubtype.DAILY) {
            val s = if (Config.Questing.showLeft) "(D: $dailyRemaining LEFT)" else "(D)"
            suffixString += s
        }
        if (quest.subtype == QuestSubtype.WEEKLY) {
            val s = if (Config.Questing.showLeft) "(W: $weeklyRemaining LEFT)" else "(W)"
            suffixString += s
        }

        val suffix = Component.literal(suffixString)
            .withStyle(Style.EMPTY.withItalic(false))
            .withStyle(ChatFormatting.GRAY)
        QuestNameWidget(
            if (!quest.isCompleted) quest.sprite else COMPLETED_QUEST_SPRITE,
            c.append(suffix),
            mcFont
        ).atBottom(0, settings = LayoutConstants.LEFT)

        val progressComponent = ProgressBar.progressComponent(
            quest.progress.toFloat() / quest.totalProgress.toFloat(),
            25,
            5
        )

        val progress = Component.literal(" ${quest.progress}/${quest.totalProgress}")
            .defaultFont()
        if (quest.isCompleted) progress.withColor(COMPLETED_QUEST_COLOR)
        val w = StringWidget(progressComponent.append(progress), mcFont)
        w.alignLeft()
        w.atBottom(0, settings = LayoutConstants.LEFT)
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