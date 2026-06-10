package cc.pe3epwithyou.trident.interfaces.questing.widgets

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.questing.Quest
import cc.pe3epwithyou.trident.feature.questing.QuestHolder
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.feature.questing.QuestSubtype
import cc.pe3epwithyou.trident.state.FontCollection
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
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import kotlin.math.ceil

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
        val mcFont = minecraft().font
        val isCompleted = quest.isCompleted

        val questName = Component.literal(quest.displayName.uppercase())
            .mccFont()
        questName.withColor(quest.rarity.color)
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

        // Calculate actual progress with other games in mind here
        val totalCompletions = quest.totalProgress.coerceAtLeast(1)
        val currentCompletions = quest.progress.coerceIn(0, totalCompletions)

        val holder = quest.questHolder ?: return@GridLayout
        val holderProgress = holder.totalProgress().toFloat().coerceIn(0f, 100f)
        val remainingHolderPercent = (100f - holderProgress).coerceIn(0f, 100f)
        val percentPerCompletion = 100f / totalCompletions.toFloat()
        val usefulExtraCompletions = ceil(remainingHolderPercent / percentPerCompletion).toInt()
            .coerceIn(0, totalCompletions - currentCompletions)
        val effectiveTotalCompletions =
            (currentCompletions + usefulExtraCompletions).coerceAtMost(totalCompletions)
        val currentProgress = currentCompletions.toFloat() / totalCompletions.toFloat()
        val orangeStart = effectiveTotalCompletions.toFloat() / totalCompletions.toFloat()

        val anotherGameProgressProvider: ProgressBar.ProgressColorProvider =
            { _, leftHalfPercent, rightHalfPercent ->
                fun colorAt(percent: Float): Int = when {
                    percent <= currentProgress -> 0x6cfe6e
                    percent < orangeStart -> 0x686969
                    else -> 0xffc211
                }

                colorAt(leftHalfPercent) to colorAt(rightHalfPercent)
            }

        val progressComponent = ProgressBar.progressComponent(
            currentProgress,
            25, 5, anotherGameProgressProvider
        )

        val progress = Component.literal(" ${currentCompletions}/")
            .defaultFont().append(
                Component.literal("$effectiveTotalCompletions").defaultFont()
                    .withColor(if (effectiveTotalCompletions < totalCompletions) 0xffc211 else 0xffffff)
            )

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