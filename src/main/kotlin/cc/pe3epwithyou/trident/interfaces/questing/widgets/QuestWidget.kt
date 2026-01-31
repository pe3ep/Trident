package cc.pe3epwithyou.trident.interfaces.questing.widgets

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.questing.Quest
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.feature.questing.QuestSubtype
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.utils.ProgressBar
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.offset
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.GridLayout
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier

class QuestWidget(
    quest: Quest,
    themed: Themed
) : CompoundWidget(0, 0, 0, 0) {
    companion object {
        private val COMPLETED_QUEST_SPRITE: Identifier =
            Resources.mcc("island_interface/generic/accept")
        private const val COMPLETED_QUEST_COLOR: Int = 0x80ff82
    }

    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    override val layout = GridLayout(themed.theme.dimensions.paddingInner) {
        val mcFont = Minecraft.getInstance().font
        val isCompleted = quest.isCompleted

        val questName = Component.literal(quest.displayName.uppercase())
            .mccFont()
        if (Config.Questing.rarityColorName) {
            questName.withColor(quest.rarity.color)
        }
        if (isCompleted) {
            questName.withColor(COMPLETED_QUEST_COLOR)
            questName.withStyle(ChatFormatting.ITALIC)
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
            .mccFont()

        val icon = if (!isCompleted) quest.sprite else COMPLETED_QUEST_SPRITE
        val component = FontCollection.texture(icon)
            .offset(y = 1f)
            .append(Component.literal(" "))
            .append(questName)
            .append(suffix)
        StringWidget(component, mcFont).atBottom(0, settings = LayoutConstants.LEFT)

        val progressComponent = ProgressBar.progressComponent(
            quest.progress.toFloat() / quest.totalProgress.toFloat(),
            25,
            5
        )

        val progress = Component.literal(" ${quest.progress}/${quest.totalProgress}")
            .defaultFont()
        if (!isCompleted) {
            StringWidget(progressComponent.append(progress), mcFont).atBottom(
                0,
                settings = LayoutConstants.LEFT
            )
        }
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}