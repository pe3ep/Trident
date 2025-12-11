package cc.pe3epwithyou.trident.interfaces.debug

import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.widget.ThemedButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.Component

class StateDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    override val title = DialogTitle(
        dialog = this, component = Component.literal("DEBUG").mccFont()
    )

    override fun layout() = grid {
        val font = Minecraft.getInstance().font

        ThemedButton(
            message = Component.literal("Refresh")
        ) {
            this@StateDialog.refresh()
        }.atBottom(0, settings = LayoutConstants.LEFT)

        StringWidget(Component.literal("Current game: ${MCCIState.game.title}"), font).atBottom(0)
    }
}