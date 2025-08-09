package cc.pe3epwithyou.trident.dialogs.killfeed

import cc.pe3epwithyou.trident.dialogs.TridentDialog
import cc.pe3epwithyou.trident.dialogs.themes.TridentThemed
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.widget.ThemedButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.Component
import com.noxcrew.sheeplib.LayoutConstants

class KillFeedSetup(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    private fun setSide(side: String) {

    }
    override fun layout() = grid {
        val font = Minecraft.getInstance().font
        val w = StringWidget(Component.literal("Position your kill feed thingy idk"), font)
        w.atBottom(0, 2)
        val buttons = listOf(
            Pair(Component.literal("Left")) { setSide("left") },
            Pair(Component.literal("Right")) { setSide("right") }
        )
        buttons.forEachIndexed { index, (text, action) ->
            ThemedButton(
                text,
                theme = this@KillFeedSetup,
                clickHandler = action
            ).at(1, index, settings = LayoutConstants.CENTRE)
        }
        StringWidget(Component.empty(), font).atBottom(0)
        ThemedButton(
            Component.literal("Done"),
            theme = this@KillFeedSetup,
            clickHandler = { }
        ).atBottom(0, 2, settings = LayoutConstants.CENTRE)
    }
}
