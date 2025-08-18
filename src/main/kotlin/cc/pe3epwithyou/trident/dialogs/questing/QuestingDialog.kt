package cc.pe3epwithyou.trident.dialogs.questing

import cc.pe3epwithyou.trident.dialogs.TridentDialog
import cc.pe3epwithyou.trident.dialogs.themes.DialogTitle
import cc.pe3epwithyou.trident.dialogs.themes.TridentThemed
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.ComponentExtensions.withHudMCC
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.widgets.questing.QuestStorage
import cc.pe3epwithyou.trident.widgets.questing.QuestWidget
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class QuestingDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    private fun getTitleWidget(): DialogTitle {
        val icon = Component.literal("\uE279")
            .withStyle(
                Style.EMPTY
                    .withFont(TridentFont.getMCCFont("icon"))
                    .withShadowColor(0x0 opacity 0)
            )
        val title = Component.literal(" Quests".uppercase())
            .withStyle(
                Style.EMPTY
                    .withFont(TridentFont.getTridentFont("hud_title"))
            )

        val backgroundColor = 0x38AFF opacity 127

        return DialogTitle(this, icon.append(title), backgroundColor, true)
    }
    override var title = getTitleWidget()

    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font

        val currentGame = MCCIslandState.game
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