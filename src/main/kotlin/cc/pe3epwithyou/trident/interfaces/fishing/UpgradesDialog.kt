package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.fishing.UpgradeLine
import cc.pe3epwithyou.trident.state.FishRarityColor
import cc.pe3epwithyou.trident.state.FishWeightColor
import cc.pe3epwithyou.trident.state.PearlQualityColor
import cc.pe3epwithyou.trident.state.SpiritPurityColor
import cc.pe3epwithyou.trident.state.TreasureRarityColor
import cc.pe3epwithyou.trident.state.fishing.UpgradeType
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.ItemParser
import cc.pe3epwithyou.trident.state.fishing.OverclockTexture
import cc.pe3epwithyou.trident.state.fishing.PerkStateCalculator
import cc.pe3epwithyou.trident.state.fishing.PerkTotals
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

class UpgradesDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    private companion object {
        private val TITLE_COLOR: Int = 0x54fcfc opacity 127
    }

    private fun getWidgetTitle(): DialogTitleWidget {
        val icon = Component.literal("\uE279")
            .mccFont("icon")
            .withStyle(Style.EMPTY.withShadowColor(0x0 opacity 0))
        val text = Component.literal(" UPGRADES".uppercase()).mccFont()
        return DialogTitle(this, icon.append(text), TITLE_COLOR)
    }

    override var title = getWidgetTitle()

    private fun augmentBonusFor(line: UpgradeLine, type: UpgradeType): Int {
        val augments = TridentClient.playerState.supplies.augments
        var total = 0
        augments.forEach { ma ->
            val a = ma.augment
            if (a.affectsType != type) return@forEach
            // Only apply when both type and line match the upgrade bucket
            if (a.affectsLine == line) total += a.bonusPoints
        }
        return total
    }

    private fun overclockBonusFor(line: UpgradeLine, type: UpgradeType): Int {
        val oc = TridentClient.playerState.supplies.overclocks
        val selected = when (type) {
            UpgradeType.HOOK -> oc.hook
            UpgradeType.MAGNET -> oc.magnet
            UpgradeType.ROD -> oc.rod
            else -> null
        } ?: return 0
        val augLine = ItemParser.parseUpgradeLine(selected.augmentName) ?: return 0
        if (augLine != line) return 0
        val level = when (type) {
            UpgradeType.HOOK -> oc.stableLevels.hook
            UpgradeType.MAGNET -> oc.stableLevels.magnet
            UpgradeType.ROD -> oc.stableLevels.rod
            else -> null
        }
        return level ?: 0
    }

    private fun unstableBonusFor(line: UpgradeLine, type: UpgradeType): Int {
        if (type != UpgradeType.CHANCE) return 0
        val unstable = TridentClient.playerState.supplies.overclocks.unstable
        if (!unstable.isActive) return 0
        val tex = unstable.texture ?: return 0
        val matchLine = when (tex) {
            OverclockTexture.STRONG_UNSTABLE -> UpgradeLine.STRONG
            OverclockTexture.WISE_UNSTABLE -> UpgradeLine.WISE
            OverclockTexture.GLIMMERING_UNSTABLE -> UpgradeLine.GLIMMERING
            OverclockTexture.GREEDY_UNSTABLE -> UpgradeLine.GREEDY
            OverclockTexture.LUCKY_UNSTABLE -> UpgradeLine.LUCKY
            else -> return 0
        }
        if (matchLine != line) return 0
        return unstable.level ?: 0
    }

    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font
        val upgrades = TridentClient.playerState.upgrades
        // Ensure perkState is up to date
        TridentClient.playerState.perkState = PerkStateCalculator.recompute(TridentClient.playerState)

        // Header row: blank corner + types
        StringWidget(Component.literal("").mccFont(), mcFont).at(0, 0)
        UpgradeType.entries.forEachIndexed { idx, type ->
            StringWidget(Component.literal(type.name.lowercase().replaceFirstChar { it.uppercase() }).mccFont(), mcFont)
                .at(0, (idx + 1) * 2, settings = LayoutConstants.CENTRE)
        }

        // Rows per line
        UpgradeLine.entries.forEachIndexed { r, line ->
            val row = r + 1
            val lineColor = when (line) {
                UpgradeLine.STRONG -> FishWeightColor.baseColor
                UpgradeLine.WISE -> FishRarityColor.baseColor
                UpgradeLine.GLIMMERING -> PearlQualityColor.baseColor
                UpgradeLine.GREEDY -> TreasureRarityColor.baseColor
                UpgradeLine.LUCKY -> SpiritPurityColor.baseColor
            }
            StringWidget(
                Component.literal(line.name.lowercase().replaceFirstChar { it.uppercase() }).mccFont().withColor(lineColor),
                mcFont
            )
                .at(row, 0, settings = LayoutConstants.LEFT)

            UpgradeType.entries.forEachIndexed { c, type ->
                val totals = TridentClient.playerState.perkState.totals[line]?.get(type)
                    ?: PerkTotals()
                val base = totals.base
                val augBonus = totals.augment
                val ocBonus = totals.overclock
                val unBonus = totals.unstable
                val eqBonus = totals.equipment
                val pylon = if (type == UpgradeType.MAGNET) TridentClient.playerState.magnetPylonBonus else 0
                val total = base + augBonus + ocBonus + unBonus + eqBonus + pylon
                val baseColor = when (line) {
                    UpgradeLine.STRONG -> FishWeightColor.baseColor
                    UpgradeLine.WISE -> FishRarityColor.baseColor
                    UpgradeLine.GLIMMERING -> PearlQualityColor.baseColor
                    UpgradeLine.GREEDY -> TreasureRarityColor.baseColor
                    UpgradeLine.LUCKY -> SpiritPurityColor.baseColor
                }
                var comp = Component.literal("$base").mccFont().withColor(baseColor)
                if (ocBonus > 0) comp = comp.append(Component.literal("+$ocBonus").mccFont().withStyle(ChatFormatting.AQUA))
                if (augBonus > 0) comp = comp.append(Component.literal("+$augBonus").mccFont().withStyle(ChatFormatting.GREEN))
                if (unBonus > 0) comp = comp.append(Component.literal("+$unBonus").mccFont().withStyle(ChatFormatting.AQUA))
                if (eqBonus > 0) comp = comp.append(Component.literal("+$eqBonus").mccFont().withStyle(ChatFormatting.GOLD))
                if (pylon > 0) comp = comp.append(Component.literal("+$pylon").mccFont().withStyle(ChatFormatting.YELLOW))
                if(ocBonus > 0 || augBonus > 0 || unBonus > 0 || eqBonus > 0 || pylon > 0) comp = comp.append(Component.literal("=$total").mccFont().withStyle(ChatFormatting.WHITE))
                StringWidget(comp, mcFont)
                    .at(row, (c + 1) * 2, settings = LayoutConstants.CENTRE)
            }
        }

        // Footnote
        StringWidget(Component.literal("Module Credit: Hydrogen").mccFont().withStyle(ChatFormatting.GRAY), mcFont)
            .at(UpgradeLine.entries.size + 2, 0, settings = LayoutConstants.LEFT)
    }

    override fun refresh() {
        title = getWidgetTitle()
        super.refresh()
    }
}