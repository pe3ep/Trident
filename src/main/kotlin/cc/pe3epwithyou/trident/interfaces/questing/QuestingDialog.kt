package cc.pe3epwithyou.trident.interfaces.questing

import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.interfaces.questing.widgets.QuestWidget
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.shared.widgets.TextureWidget
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.offset
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class QuestingDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key),
    Themed by TridentThemed {
    companion object {
        var currentGame = MCCIState.game
    }

    private fun getTitleWidget(): QuestDialogTitle {
        val icon = FontCollection.get("_fonts/icon/quest_log.png").withStyle(
            Style.EMPTY.withShadowColor(0x0 opacity 0)
        )
        val titleText = " QUESTS"
        val title = Component.literal(titleText).mccFont().offset(y = -0.5f)

        val backgroundColor = 0x038AFF opacity 127
        val gameIcon = FontCollection.get(
            currentGame.icon
        ).withStyle(
            Style.EMPTY.withShadowColor(0x0 opacity 0).withColor(ChatFormatting.WHITE)
        )

        val gameColor = currentGame.primaryColor opacity 127

        return QuestDialogTitle(
            this,
            icon.append(title),
            backgroundColor,
            true,
            game = gameIcon,
            gameColor = gameColor,
        )
    }

    override var title = getTitleWidget()

    override fun layout(): GridLayout = grid {
        val font = Minecraft.getInstance().font
        var game = currentGame
        // Since BB and BBA share quests, we treat BBA as BB
        if (game == Game.BATTLE_BOX_ARENA) game = Game.BATTLE_BOX

        val quests = QuestStorage.getActiveQuests(game)

        if (game == Game.HUB || game == Game.FISHING) {
            TextureWidget(
                Texture(
                    location = Resources.mcc("textures/island_interface/game_mastery/premium_road.png"),
                    width = 12,
                    height = 12,
                    textureWidth = 16,
                    textureHeight = 16,
                )
            ).atBottom(0, settings = LayoutConstants.CENTRE)
            StringWidget(
                Component.literal("Not in a game".uppercase()).mccFont()
                    .withColor(ChatFormatting.GRAY.color!!), font
            ).atBottom(0, settings = LayoutConstants.LEFT)
            return@grid
        }

        if (quests.isEmpty()) {
            TextureWidget(
                Texture(
                    location = Resources.mcc("textures/island_interface/quest_log/daily/icon.png"),
                    width = 12,
                    height = 12,
                    textureWidth = 16,
                    textureHeight = 16,
                )
            ).atBottom(0, settings = LayoutConstants.CENTRE)
            StringWidget(
                Component.literal("No quests found".uppercase()).mccFont()
                    .withStyle(ChatFormatting.GRAY), font
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