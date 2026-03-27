package cc.pe3epwithyou.trident.feature.questing.lock

import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import cc.pe3epwithyou.trident.utils.gridLayout
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class QuestLockWidget(x: Int, y: Int) : CompoundWidget(x, y, 0, 0) {
    override val layout = gridLayout(1, x, y) {
        val games = Game.entries.toMutableSet()
        games.remove(Game.HUB)
        games.remove(Game.FISHING)
        games.remove(Game.BATTLE_BOX_ARENA)
        games.remove(Game.SKY_BATTLE_SOLO)

        var col = 0
        games.forEach {
            GameWidget(it).at(0, col)
            col++
        }
    }

    override fun renderWidget(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
        graphics.fillRoundedAll(x - 1, y - 1, width + 2, height + 2, 0x111111 opacity 128)
        super.renderWidget(graphics, i, j, f)
        val x = graphics.guiWidth() / 2

        var c =
            Component.literal("${QuestLock.lockedGames.size} Game${if (QuestLock.lockedGames.size == 1) "" else "s"} Locked")
                .withStyle(
                    ChatFormatting.WHITE
                )

        if (QuestLock.lockedGames.isEmpty()) {
            c = Component.literal("Select Game to Lock").withStyle(ChatFormatting.GRAY)
        }

        graphics.drawCenteredString(
            minecraft().font,
            c,
            x,
            y + 20,
            0xffffff.opaqueColor()
        )
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)

        width = layout.width
        height = layout.height
    }
}