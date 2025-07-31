package cc.pe3epwithyou.trident.dialogs.questing

import cc.pe3epwithyou.trident.dialogs.TridentDialog
import cc.pe3epwithyou.trident.dialogs.themes.DialogTitle
import cc.pe3epwithyou.trident.dialogs.themes.TridentThemed
import cc.pe3epwithyou.trident.utils.TridentFont
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class QuestingDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    private fun getTitleWidget(): DialogTitle {
        val icon = Component.literal("\uE10C")
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

        return DialogTitle(this, icon.append(title), backgroundColor, false)
    }
    override var title = getTitleWidget()

    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font
        val c = Component.literal("yoo quests soonTM les gooo")
            .withStyle(Style.EMPTY
                .withFont(TridentFont.getMCCFont())
            )
        StringWidget(c, mcFont).atBottom(0, settings = LayoutConstants.LEFT)
    }

    override fun refresh() {
        title = getTitleWidget()
        super.refresh()
    }
}