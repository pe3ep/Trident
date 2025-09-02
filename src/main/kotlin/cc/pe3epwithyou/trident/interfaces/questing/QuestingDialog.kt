package cc.pe3epwithyou.trident.interfaces.questing

import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.interfaces.questing.widgets.QuestWidget
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withTridentFont
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class QuestingDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    companion object {
        var currentGame = MCCIState.game

        /** If true, the UI will display a warning sign in the title indicating that the progress might be inaccurate */
        var isDesynced = false
    }

    private fun getTitleWidget(): QuestDialogTitle {
        val icon = FontCollection.get("_fonts/quest_log.png")
            .withStyle(
                Style.EMPTY
                    .withShadowColor(0x0 opacity 0)
            )
        val titleText = if (isDesynced) " DESYNCED ⚠" else " QUESTS"
        val title = Component.literal(titleText)
            .withTridentFont("hud_title")
        if (isDesynced) {
            title.withStyle(ChatFormatting.GOLD)
        }

        val backgroundColor = 0x38AFF opacity 127
        val gameIcon = FontCollection.get(currentGame.icon)
            .withStyle(
                Style.EMPTY
                    .withShadowColor(0x0 opacity 0)
                    .withColor(ChatFormatting.WHITE)
            )

        return QuestDialogTitle(
            this,
            icon.append(title),
            backgroundColor,
            true,
            game = gameIcon,
            gameColor = currentGame.primaryColor opacity 127,
            tooltip = if (!isDesynced) null else Tooltip.create(
                Component.literal(
                    """
                    Module is not synced.
                    Trident has detected that quest progress is not up to date. Please open your Quest Log to update it.
                """.trimIndent()
                )
                    .withStyle(ChatFormatting.GRAY)
            )
        )
    }

    override var title = getTitleWidget()

    override fun layout(): GridLayout = grid {
        val font = Minecraft.getInstance().font

        val quests = QuestStorage.getActiveQuests(currentGame)

        if (currentGame == Game.HUB || currentGame == Game.FISHING) {
            MultiLineTextWidget(
                Component.literal("Join a game to\nview quests".uppercase()).mccFont()
                    .withColor(ChatFormatting.GRAY.color!!),
                font
            ).atBottom(0, settings = LayoutConstants.LEFT)
            return@grid
        }

        if (quests.isEmpty()) {
            StringWidget(
                Component.literal("No quests detected".uppercase())
                    .mccFont()
                    .withStyle(ChatFormatting.GRAY),
                font
            ).atBottom(0)
            return@grid
        }
        quests.forEach { q ->
            QuestWidget(q, this@QuestingDialog).atBottom(0, settings = LayoutConstants.LEFT)
        }
    }

    override fun refresh() {
        title = getTitleWidget()
        super.refresh()
    }
}