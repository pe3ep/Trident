package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.fishing.UpgradeLine
import cc.pe3epwithyou.trident.state.FishWeightColor
import cc.pe3epwithyou.trident.state.PearlQualityColor
import cc.pe3epwithyou.trident.state.SpiritPurityColor
import cc.pe3epwithyou.trident.state.TreasureRarityColor
import cc.pe3epwithyou.trident.state.FishRarityColor
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

class SpotDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    private companion object {
        private val TITLE_COLOR: Int = 0x54fcfc opacity 127
    }

    private fun getWidgetTitle(): DialogTitleWidget {
        val icon = Component.literal("\uE279").mccFont("icon").withStyle(Style.EMPTY.withShadowColor(0x0 opacity 0))
        val text = Component.literal(" SPOT".uppercase()).mccFont()
        return DialogTitle(this, icon.append(text), TITLE_COLOR)
    }

    override var title = getWidgetTitle()

    override fun layout(): GridLayout = grid {
        val font = Minecraft.getInstance().font
        val spot = TridentClient.playerState.spot

        var row = 0
        val status = if (spot.hasSpot) Component.literal("ACTIVE").mccFont().withStyle(ChatFormatting.AQUA) else Component.literal("NONE").mccFont().withStyle(ChatFormatting.GRAY)
        StringWidget(Component.literal("SPOT").mccFont().withStyle(ChatFormatting.AQUA), font).at(row++, 0, settings = LayoutConstants.LEFT)
        StringWidget(Component.literal("Status: ").mccFont().append(status), font).at(row++, 0, settings = LayoutConstants.LEFT)

        if (!spot.hasSpot) return@grid

        fun lineLabel(l: UpgradeLine): String = l.name.lowercase().replaceFirstChar { it.uppercase() }

        if (spot.hookPercents.isNotEmpty()) {
            StringWidget(Component.literal("Hooks").mccFont().withStyle(ChatFormatting.GRAY), font).at(row++, 0, settings = LayoutConstants.LEFT)
            UpgradeLine.entries.forEach { l ->
                val v = spot.hookPercents[l] ?: return@forEach
                val baseColor = when(l){
                    UpgradeLine.STRONG -> FishWeightColor.baseColor
                    UpgradeLine.WISE -> FishRarityColor.baseColor
                    UpgradeLine.GLIMMERING -> PearlQualityColor.baseColor
                    UpgradeLine.GREEDY -> TreasureRarityColor.baseColor
                    UpgradeLine.LUCKY -> SpiritPurityColor.baseColor
                }
                val t = Component.literal("${lineLabel(l)} Hook: ").mccFont()
                    .append(Component.literal("+${"""%.2f""".format(v)}% ").mccFont().withColor(baseColor))
                StringWidget(t, font).at(row++, 0, settings = LayoutConstants.LEFT)
            }
        }

        if (spot.magnetPercents.isNotEmpty()) {
            StringWidget(Component.literal("Magnets").mccFont().withStyle(ChatFormatting.GRAY), font).at(row++, 0, settings = LayoutConstants.LEFT)
            UpgradeLine.entries.forEach { l ->
                val v = spot.magnetPercents[l] ?: return@forEach
                val label = when (l) {
                    UpgradeLine.STRONG -> "XP Magnet"
                    UpgradeLine.WISE -> "Fish Magnet"
                    UpgradeLine.GLIMMERING -> "Pearl Magnet"
                    UpgradeLine.GREEDY -> "Treasure Magnet"
                    UpgradeLine.LUCKY -> "Spirit Magnet"
                }
                val baseColor = when(l){
                    UpgradeLine.STRONG -> FishWeightColor.baseColor
                    UpgradeLine.WISE -> FishRarityColor.baseColor
                    UpgradeLine.GLIMMERING -> PearlQualityColor.baseColor
                    UpgradeLine.GREEDY -> TreasureRarityColor.baseColor
                    UpgradeLine.LUCKY -> SpiritPurityColor.baseColor
                }
                val t = Component.literal("$label: ").mccFont()
                    .append(Component.literal("+${"""%.2f""".format(v)}% ").mccFont().withColor(baseColor))
                StringWidget(t, font).at(row++, 0, settings = LayoutConstants.LEFT)
            }
        }

        // Chance perks: show only if modified
        val chanceRows = listOf(
            "Fish Chance" to spot.fishChanceBonusPercent,
            "Elusive Chance" to spot.elusiveChanceBonusPercent,
            "Pearl Chance" to spot.pearlChanceBonusPercent,
            "Treasure Chance" to spot.treasureChanceBonusPercent,
            "Spirit Chance" to spot.spiritChanceBonusPercent,
        ).filter { it.second != 0.0 }
        val showWayfinder = spot.wayfinderDataBonus != 0.0
        if (chanceRows.isNotEmpty() || showWayfinder) {
            StringWidget(Component.literal("Chances").mccFont().withStyle(ChatFormatting.GRAY), font).at(row++, 0, settings = LayoutConstants.LEFT)
            chanceRows.forEach { (label, value) ->
                StringWidget(
                    Component.literal("$label: ").mccFont()
                        .append(Component.literal("+${"""%.2f""".format(value)}% ").mccFont().withStyle(ChatFormatting.AQUA)),
                    font
                ).at(row++, 0, settings = LayoutConstants.LEFT)
            }
            if (showWayfinder) {
                StringWidget(
                    Component.literal("Wayfinder Data: ").mccFont()
                        .append(Component.literal("+${"""%.2f""".format(spot.wayfinderDataBonus)}").mccFont().withStyle(ChatFormatting.AQUA)),
                    font
                ).at(row++, 0, settings = LayoutConstants.LEFT)
            }
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


