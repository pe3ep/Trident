package cc.pe3epwithyou.trident.dialogs.questing

import cc.pe3epwithyou.trident.dialogs.TridentDialog
import cc.pe3epwithyou.trident.dialogs.themes.TridentThemed
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.utils.ComponentExtensions.withHudMCC
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.widgets.questing.QuestStorage
import cc.pe3epwithyou.trident.widgets.questing.QuestWidget
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
        var currentGame = MCCIslandState.game
        /** If true, the UI will display a warning sign in the title indicating that the progress might be inaccurate */
        var isDesynced = false
    }
    private fun getTitleWidget(): QuestDialogTitle {
        val icon = Component.literal("\uE279")
            .withStyle(
                Style.EMPTY
                    .withFont(TridentFont.getMCCFont("icon"))
                    .withShadowColor(0x0 opacity 0)
            )
        val titleText = if (isDesynced) " DESYNCED ⚠" else " QUESTS"
        val title = Component.literal(titleText)
            .withStyle(
                Style.EMPTY
                    .withFont(TridentFont.getTridentFont("hud_title"))
            )
        if (isDesynced) {
            title.withStyle(ChatFormatting.GOLD)
        }

        val backgroundColor = 0x38AFF opacity 127
        val gameIcon = Component.literal(currentGame.icon.toString())
            .withStyle(
                Style.EMPTY
                    .withFont(TridentFont.getMCCFont("icon"))
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
                Component.literal("""
                    Module is not synced.
                    Trident has detected that quest progress is not up to date. Please open your Quest Log to update it.
                """.trimIndent())
                    .withStyle(ChatFormatting.GRAY)
            )
        )
    }
    override var title = getTitleWidget()

    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font

        val quests = QuestStorage.getActiveQuests(currentGame)

        if (currentGame == MCCGame.HUB || currentGame == MCCGame.FISHING) {
            MultiLineTextWidget(
                Component.literal("Join a game to\nview quests".uppercase()).withHudMCC().withColor(ChatFormatting.GRAY.color!!),
                mcFont
            ).atBottom(0, settings = LayoutConstants.LEFT)
            return@grid
        }

        if (quests.isEmpty()) {
            StringWidget(
                Component.literal("No quests detected".uppercase()).withStyle(Style.EMPTY
                    .withColor(ChatFormatting.GRAY)
                    .withFont(TridentFont.getMCCFont())
                ),
                mcFont
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