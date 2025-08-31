package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
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

class ChancePerksDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    private companion object {
        private val TITLE_COLOR: Int = 0x54fcfc opacity 127
    }

    private fun getWidgetTitle(): DialogTitleWidget {
        val icon = Component.literal("\uE279").mccFont("icon").withStyle(Style.EMPTY.withShadowColor(0x0 opacity 0))
        val text = Component.literal(" CHANCE PERKS".uppercase()).mccFont()
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
        StringWidget(Component.literal("CHANCE PERKS").mccFont().withStyle(ChatFormatting.AQUA), font)
            .at(row++, 0, settings = LayoutConstants.LEFT)

        val ptsStrong = ps.totals[cc.pe3epwithyou.trident.state.fishing.UpgradeLine.STRONG]?.get(cc.pe3epwithyou.trident.state.fishing.UpgradeType.CHANCE)?.total ?: 0
        val ptsWise = ps.totals[cc.pe3epwithyou.trident.state.fishing.UpgradeLine.WISE]?.get(cc.pe3epwithyou.trident.state.fishing.UpgradeType.CHANCE)?.total ?: 0
        val ptsGlim = ps.totals[cc.pe3epwithyou.trident.state.fishing.UpgradeLine.GLIMMERING]?.get(cc.pe3epwithyou.trident.state.fishing.UpgradeType.CHANCE)?.total ?: 0
        val ptsGreedy = ps.totals[cc.pe3epwithyou.trident.state.fishing.UpgradeLine.GREEDY]?.get(cc.pe3epwithyou.trident.state.fishing.UpgradeType.CHANCE)?.total ?: 0
        val ptsLucky = ps.totals[cc.pe3epwithyou.trident.state.fishing.UpgradeLine.LUCKY]?.get(cc.pe3epwithyou.trident.state.fishing.UpgradeType.CHANCE)?.total ?: 0

        data class ChanceRow(val label: String, val base: Double, val bonus: Double, val isPercent: Boolean)
        val rows = listOf(
            ChanceRow("Elusive Chance", base = 0.0, bonus = ptsStrong * 0.5, isPercent = true),
            ChanceRow("Wayfinder Data", base = 10.0, bonus = ptsWise * 1.0, isPercent = false),
            ChanceRow("Pearl Chance", base = 5.0, bonus = ptsGlim * 0.5, isPercent = true),
            ChanceRow("Treasure Chance", base = 1.0, bonus = ptsGreedy * 0.1, isPercent = true),
            ChanceRow("Spirit Chance", base = 2.0, bonus = ptsLucky * 0.2, isPercent = true),
        )

        rows.forEach { r ->
            val total = r.base + r.bonus
            val baseStr = if (r.isPercent) "${"""%.2f""".format(r.base)}%" else r.base.toInt().toString()
            val bonusStr = if (r.isPercent) "+${"""%.2f""".format(r.bonus)}%" else "+${r.bonus.toInt()}"
            val totalStr = if (r.isPercent) "${"""%.2f""".format(total)}%" else "${total.toInt()} per catch"
            val t = Component.literal("${r.label}: ").mccFont()
                .append(Component.literal(baseStr).mccFont().withStyle(ChatFormatting.GRAY))
                .append(Component.literal(" $bonusStr").mccFont().withStyle(ChatFormatting.AQUA))
                .append(Component.literal(" = $totalStr").mccFont().withStyle(ChatFormatting.GRAY))
            StringWidget(t, font).at(row++, 0, settings = LayoutConstants.LEFT)
        }
    }

    override fun refresh() {
        title = getWidgetTitle()
        super.refresh()
    }
}


