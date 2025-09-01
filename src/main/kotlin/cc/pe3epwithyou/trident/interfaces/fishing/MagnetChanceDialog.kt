package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.fishing.PerkStateCalculator
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import net.minecraft.client.gui.components.Button
import cc.pe3epwithyou.trident.state.fishing.UpgradeLine
import cc.pe3epwithyou.trident.state.fishing.UpgradeType
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
import cc.pe3epwithyou.trident.state.FishRarityColor
import cc.pe3epwithyou.trident.state.FishWeightColor
import cc.pe3epwithyou.trident.state.PearlQualityColor
import cc.pe3epwithyou.trident.state.SpiritPurityColor
import cc.pe3epwithyou.trident.state.TreasureRarityColor

class MagnetChanceDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    private companion object {
        private val TITLE_COLOR: Int = 0x54fcfc opacity 127
    }

    private fun getWidgetTitle(): DialogTitleWidget {
        val icon = Component.literal("\uE279").mccFont("icon").withStyle(Style.EMPTY.withShadowColor(0x0 opacity 0))
        val text = Component.literal(" MAGNET CHANCES".uppercase()).mccFont()
        return DialogTitle(this, icon.append(text), TITLE_COLOR)
    }

    override var title = getWidgetTitle()

    private val expanded: MutableMap<UpgradeLine, Boolean> = mutableMapOf(
        UpgradeLine.STRONG to false,
        UpgradeLine.WISE to false,
        UpgradeLine.GLIMMERING to false,
        UpgradeLine.GREEDY to false,
        UpgradeLine.LUCKY to false,
    )

    override fun layout(): GridLayout = grid {
        val font = Minecraft.getInstance().font
        TridentClient.playerState.perkState = PerkStateCalculator.recompute(
            TridentClient.playerState
        )
        val ps = TridentClient.playerState.perkState

        var row = 0
        StringWidget(Component.literal("MAGNETS").mccFont().withStyle(ChatFormatting.AQUA), font)
            .at(row++, 0, settings = LayoutConstants.LEFT)

        val spot = TridentClient.playerState.spot
        fun baseColorForLine(line: UpgradeLine): Int = when (line) {
            UpgradeLine.STRONG -> FishWeightColor.baseColor
            UpgradeLine.WISE -> FishRarityColor.baseColor
            UpgradeLine.GLIMMERING -> PearlQualityColor.baseColor
            UpgradeLine.GREEDY -> TreasureRarityColor.baseColor
            UpgradeLine.LUCKY -> SpiritPurityColor.baseColor
        }
        listOf(
            "XP Magnet" to UpgradeLine.STRONG,
            "Fish Magnet" to UpgradeLine.WISE,
            "Pearl Magnet" to UpgradeLine.GLIMMERING,
            "Treasure Magnet" to UpgradeLine.GREEDY,
            "Spirit Magnet" to UpgradeLine.LUCKY,
        ).forEach { (label, line) ->
            val basePts = ps.totals[line]?.get(UpgradeType.MAGNET)?.total ?: 0
            val pylon = TridentClient.playerState.magnetPylonBonus
            val pts = basePts + pylon
            val spotPct = if (spot.hasSpot) (spot.magnetPercents[line] ?: 0.0) else 0.0
            val tidePct = if (TridentClient.playerState.tideLines.contains(line)) 20.0 else 0.0
            val effPts = pts * (1.0 + (spotPct + tidePct) / 100.0)
            val percent = if (label == "Fish Magnet") (effPts * 10) else (effPts * 5)
            val deltaSpot = pts * (spotPct / 100.0)
            val deltaTide = pts * (tidePct / 100.0)

            val caret = if (expanded[line] == true) "v" else ">"
            val headerBase = Component.literal("$caret ").mccFont()
                .append(Component.literal(label).mccFont().withColor(baseColorForLine(line)))
                .append(Component.literal(": ").mccFont())
                .append(Component.literal("${"""%.2f""".format(percent)}% ").mccFont().withStyle(ChatFormatting.AQUA))
            val condensedCalc = Component.literal(" ${pts}*(${"""%.0f""".format(spotPct)}%+${"""%.0f""".format(tidePct)}%)")
                .mccFont().withStyle(ChatFormatting.GRAY)
            val buttonLabel = if (expanded[line] == true) headerBase else headerBase.copy().append(condensedCalc)

            Button.builder(buttonLabel) {
                expanded[line] = !(expanded[line] ?: false)
                this@MagnetChanceDialog.refresh()
            }.bounds(0, 0, 200, 12).build().at(row++, 0, settings = LayoutConstants.LEFT)

            if (expanded[line] == true) {
                val detail = Component.literal("pts ").mccFont().withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("${pts}").mccFont().withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(" + ").mccFont().withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("${"""%.2f""".format(spotPct)}% ").mccFont().withStyle(ChatFormatting.DARK_AQUA))
                    .append(Component.literal("(+${"""%.2f""".format(deltaSpot)}) ").mccFont().withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("+ ").mccFont().withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("${"""%.2f""".format(tidePct)}% ").mccFont().withStyle(ChatFormatting.DARK_AQUA))
                    .append(Component.literal("(+${"""%.2f""".format(deltaTide)}) ").mccFont().withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("= ${"""%.2f""".format(effPts)}").mccFont().withStyle(ChatFormatting.GRAY))
                StringWidget(detail, font).at(row++, 0, settings = LayoutConstants.LEFT)

                val guaranteed = 1 + kotlin.math.floor(percent / 100.0).toInt()
                val extraChance = percent % 100.0
                val stoch = Component.literal("Per Catch: ").mccFont().withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("x${guaranteed}").mccFont().withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(" + ${"""%.0f""".format(extraChance)}% ").mccFont().withStyle(ChatFormatting.AQUA))
                StringWidget(stoch, font).at(row++, 0, settings = LayoutConstants.LEFT)
            }

            row++
        }

        // Footnote
        StringWidget(Component.literal("Module Credit: Hydrogen").mccFont().withStyle(ChatFormatting.GRAY), font)
            .atBottom(0, settings = LayoutConstants.LEFT)
    }

    override fun refresh() {
        title = getWidgetTitle()
        super.refresh()
    }
}


