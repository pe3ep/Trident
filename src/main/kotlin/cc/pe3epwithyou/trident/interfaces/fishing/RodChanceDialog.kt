package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.fishing.UpgradeLine
import cc.pe3epwithyou.trident.state.fishing.UpgradeType
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.dialog.title.DialogTitleWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class RodChanceDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    private companion object {
        private val TITLE_COLOR: Int = 0x54fcfc opacity 127
    }

    private fun getWidgetTitle(): DialogTitleWidget {
        val icon = Component.literal("\uE279").mccFont("icon").withStyle(Style.EMPTY.withShadowColor(0x0 opacity 0))
        val text = Component.literal(" ROD CHANCES".uppercase()).mccFont()
        return DialogTitle(this, icon.append(text), TITLE_COLOR)
    }

    override var title = getWidgetTitle()

    override fun layout(): GridLayout = grid {
        val font = Minecraft.getInstance().font
        TridentClient.playerState.perkState = cc.pe3epwithyou.trident.state.fishing.PerkStateCalculator.recompute(
            TridentClient.playerState
        )
        val ps = TridentClient.playerState.perkState

        var row = 0
        StringWidget(Component.literal("RODS").mccFont().withStyle(ChatFormatting.AQUA), font)
            .at(row++, 0, settings = LayoutConstants.LEFT)

        UpgradeLine.entries.forEach { line ->
            val ptsRod = ps.totals[line]?.get(UpgradeType.ROD)?.total ?: 0
            val t = Component.literal("${line.name.lowercase().replaceFirstChar { it.uppercase() }} Rod: ").mccFont()
                .append(Component.literal("${ptsRod}%").mccFont().withStyle(ChatFormatting.AQUA))
            StringWidget(t, font).at(row++, 0, settings = LayoutConstants.LEFT)
        }
    }

    override fun refresh() {
        title = getWidgetTitle()
        super.refresh()
    }
}


