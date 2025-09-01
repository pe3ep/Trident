package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.fishing.PerkStateCalculator
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.dialog.title.DialogTitleWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class PotChanceDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    private companion object {
        private val TITLE_COLOR: Int = 0x54fcfc opacity 127
    }

    private fun getWidgetTitle(): DialogTitleWidget {
        val icon = Component.literal("\uE279").mccFont("icon").withStyle(Style.EMPTY.withShadowColor(0x0 opacity 0))
        val text = Component.literal(" POT CHANCES".uppercase()).mccFont()
        return DialogTitle(this, icon.append(text), TITLE_COLOR)
    }

    override var title = getWidgetTitle()

    override fun layout(): GridLayout = grid {
        val font = Minecraft.getInstance().font
        TridentClient.playerState.perkState = PerkStateCalculator.recompute(
            TridentClient.playerState
        )

        var row = 0
        StringWidget(Component.literal("POTS").mccFont().withStyle(ChatFormatting.AQUA), font)
            .at(row++, 0, settings = LayoutConstants.LEFT)

        MultiLineTextWidget(Component.literal("POTS: TODO").mccFont().withStyle(ChatFormatting.GRAY), font)
            .at(row++, 0, settings = LayoutConstants.LEFT)

        // Footnote
        StringWidget(Component.literal("Module Credit: Hydrogen").mccFont().withStyle(ChatFormatting.GRAY), font)
            .atBottom(0, settings = LayoutConstants.LEFT)
    }

    override fun refresh() {
        title = getWidgetTitle()
        super.refresh()
    }
}


