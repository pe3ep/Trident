package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.PerkStateCalculator
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import net.minecraft.client.gui.components.Button
import cc.pe3epwithyou.trident.state.fishing.UpgradeLine
import cc.pe3epwithyou.trident.state.fishing.UpgradeType
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.dialog.title.DialogTitleWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import cc.pe3epwithyou.trident.state.FishRarityColor
import cc.pe3epwithyou.trident.state.PearlQualityColor
import cc.pe3epwithyou.trident.state.TreasureRarityColor
import cc.pe3epwithyou.trident.state.SpiritPurityColor
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

    private val expanded: MutableMap<String, Boolean> = mutableMapOf(
        "Fish Chance" to false,
        "Elusive Chance" to false,
        "Wayfinder Data" to false,
        "Pearl Chance" to false,
        "Treasure Chance" to false,
        "Spirit Chance" to false,
    )

    override fun layout(): GridLayout = grid {
        val font = Minecraft.getInstance().font
        TridentClient.playerState.perkState = PerkStateCalculator.recompute(
            TridentClient.playerState
        )
        val ps = TridentClient.playerState.perkState

        var row = 0
        StringWidget(Component.literal("CHANCE PERKS").mccFont().withStyle(ChatFormatting.AQUA), font)
            .at(row++, 0, settings = LayoutConstants.LEFT)

        val ptsStrong = ps.totals[UpgradeLine.STRONG]?.get(UpgradeType.CHANCE)?.total ?: 0
        val ptsWise = ps.totals[UpgradeLine.WISE]?.get(UpgradeType.CHANCE)?.total ?: 0
        val ptsGlim = ps.totals[UpgradeLine.GLIMMERING]?.get(UpgradeType.CHANCE)?.total ?: 0
        val ptsGreedy = ps.totals[UpgradeLine.GREEDY]?.get(UpgradeType.CHANCE)?.total ?: 0
        val ptsLucky = ps.totals[UpgradeLine.LUCKY]?.get(UpgradeType.CHANCE)?.total ?: 0

        data class ChanceRow(val label: String, val base: Double, val bonus: Double, val isPercent: Boolean)
        val spot = TridentClient.playerState.spot
        val winds = TridentClient.playerState.windLines
        // Lure/Ultralure and other augment effects
        val augmentList = TridentClient.playerState.supplies.augments.filterNot { it.paused }.map { it.augment }
        val hasElusiveLure = augmentList.any {
            it == Augment.ELUSIVE_LURE || it == Augment.ELUSIVE_ULTRALURE
        }
        val pearlLureActive = augmentList.any {
            it == Augment.PEARL_LURE || it == Augment.PEARL_ULTRALURE
        }
        val treasureLureActive = augmentList.any {
            it == Augment.TREASURE_LURE || it == Augment.TREASURE_ULTRALURE
        }
        val spiritLureActive = augmentList.any {
            it == Augment.SPIRIT_LURE || it == Augment.SPIRIT_ULTRALURE
        }
        val wayfinderFromLures: Int = augmentList.count { it == Augment.WAYFINDER_LURE } * 10 +
                augmentList.count { it == Augment.WAYFINDER_ULTRALURE } * 20
        val hasElusiveSoda = augmentList.any { it == Augment.ELUSIVE_SODA }

        // Show Fish Chance separately: 40% base normally; 100% if 100% fish spot; 0% if another guaranteed type
        val fishBase = 40.0
        val fishSpot100 = spot.hasSpot && spot.fishChanceBonusPercent >= 100.0
        val guaranteedType: String? = when {
            spot.hasSpot && spot.pearlChanceBonusPercent >= 100.0 -> "Pearl Chance"
            spot.hasSpot && spot.treasureChanceBonusPercent >= 100.0 -> "Treasure Chance"
            spot.hasSpot && spot.spiritChanceBonusPercent >= 100.0 -> "Spirit Chance"
            (!TridentClient.playerState.inGrotto && pearlLureActive) -> "Pearl Chance"
            (!TridentClient.playerState.inGrotto && treasureLureActive) -> "Treasure Chance"
            (!TridentClient.playerState.inGrotto && spiritLureActive) -> "Spirit Chance"
            else -> null
        }
        val fishTotal = when {
            guaranteedType != null -> 0.0
            fishSpot100 -> 100.0
            else -> fishBase
        }
        run {
            val label = "Fish Chance"
            val caret = if (expanded[label] == true) "v" else ">"
            val header = Component.literal("$caret $label: ").mccFont()
                .append(Component.literal("${"""%.2f""".format(fishTotal)}% ").mccFont().withStyle(ChatFormatting.AQUA))
            Button.builder(header) {
                expanded[label] = !(expanded[label] ?: false)
                this@ChancePerksDialog.refresh()
            }.bounds(0, 0, 220, 12).build().at(row++, 0, settings = LayoutConstants.LEFT)

            if (expanded[label] == true) {
                val detail = when {
                    guaranteedType != null -> Component.literal("Zeroed due to guaranteed $guaranteedType").mccFont().withStyle(ChatFormatting.GRAY)
                    fishSpot100 -> Component.literal("Guaranteed by spot").mccFont().withStyle(ChatFormatting.GRAY)
                    else -> Component.literal("Base: ${"""%.2f""".format(fishBase)}% ").mccFont().withStyle(ChatFormatting.GRAY)
                }
                StringWidget(detail, font).at(row++, 0, settings = LayoutConstants.LEFT)
            }
        }

        val rows = listOf(
            ChanceRow("Elusive Chance", base = 0.0, bonus = ptsStrong * 0.5 + (if (spot.hasSpot) spot.elusiveChanceBonusPercent else 0.0) + (if (winds.contains(UpgradeLine.STRONG)) 5.0 else 0.0) + (if (hasElusiveLure) 100.0 else 0.0) + (if (hasElusiveSoda) 10.0 else 0.0), isPercent = true),
            ChanceRow("Wayfinder Data", base = 10.0, bonus = ptsWise * 1.0 + (if (spot.hasSpot) spot.wayfinderDataBonus else 0.0) + (if (winds.contains(UpgradeLine.WISE)) 10.0 else 0.0) + wayfinderFromLures, isPercent = false),
            ChanceRow("Pearl Chance", base = 5.0, bonus = ptsGlim * 0.5 + (if (spot.hasSpot) spot.pearlChanceBonusPercent else 0.0) + (if (winds.contains(UpgradeLine.GLIMMERING)) 5.0 else 0.0), isPercent = true),
            ChanceRow("Treasure Chance", base = 1.0, bonus = ptsGreedy * 0.1 + (if (spot.hasSpot) spot.treasureChanceBonusPercent else 0.0) + (if (winds.contains(UpgradeLine.GREEDY)) 0.1 * 100 else 0.0), isPercent = true),
            ChanceRow("Spirit Chance", base = 2.0, bonus = ptsLucky * 0.2 + (if (spot.hasSpot) spot.spiritChanceBonusPercent else 0.0) + (if (winds.contains(UpgradeLine.LUCKY)) 5.0 else 0.0), isPercent = true),
        )

        rows.forEach { r ->
            var total = r.base + r.bonus
            val guaranteedLabel: String? = when {
                spot.hasSpot && spot.pearlChanceBonusPercent >= 100.0 -> "Pearl Chance"
                spot.hasSpot && spot.treasureChanceBonusPercent >= 100.0 -> "Treasure Chance"
                spot.hasSpot && spot.spiritChanceBonusPercent >= 100.0 -> "Spirit Chance"
                fishSpot100 -> "Fish Chance"
                else -> null
            }
            if (r.isPercent && r.label.endsWith("Chance") && r.label != "Elusive Chance") {
                if (guaranteedLabel == "Fish Chance") {
                    // Fish guarantees zero all other percent chances (elusive unaffected)
                    total = 0.0
                } else if (guaranteedLabel != null) {
                    total = if (r.label == guaranteedLabel) 100.0 else 0.0
                }
            }
            // Cap Elusive Chance at 100%
            if (r.isPercent && r.label == "Elusive Chance") {
                total = total.coerceAtMost(100.0)
            }
            val baseStr = if (r.isPercent) "${"""%.2f""".format(r.base)}%" else r.base.toInt().toString()
            val spotPart = when (r.label) {
                "Elusive Chance" -> if (spot.hasSpot) spot.elusiveChanceBonusPercent else 0.0
                "Pearl Chance" -> if (spot.hasSpot) spot.pearlChanceBonusPercent else 0.0
                "Treasure Chance" -> if (spot.hasSpot) spot.treasureChanceBonusPercent else 0.0
                "Spirit Chance" -> if (spot.hasSpot) spot.spiritChanceBonusPercent else 0.0
                else -> 0.0
            }
            val windPart = when (r.label) {
                "Elusive Chance" -> if (winds.contains(UpgradeLine.STRONG)) 5.0 else 0.0
                "Wayfinder Data" -> if (winds.contains(UpgradeLine.WISE)) 10.0 else 0.0
                "Pearl Chance" -> if (winds.contains(UpgradeLine.GLIMMERING)) 5.0 else 0.0
                "Treasure Chance" -> if (winds.contains(UpgradeLine.GREEDY)) 0.1 * 100 else 0.0
                "Spirit Chance" -> if (winds.contains(UpgradeLine.LUCKY)) 5.0 else 0.0
                else -> 0.0
            }
            val otherBonus = r.bonus - (spotPart + windPart)

            val spotStr = if (r.isPercent) "+${"""%.2f""".format(spotPart)}%" else "+${spotPart.toInt()}"
            val windStr = if (r.isPercent) "+${"""%.2f""".format(windPart)}%" else "+${windPart.toInt()}"
            val otherStr = if (r.isPercent) "+${"""%.2f""".format(otherBonus)}%" else "+${otherBonus.toInt()}"
            val totalStr = if (r.isPercent) "${"""%.2f""".format(total)}%" else "${total.toInt()} per catch"

            val caret = if (expanded[r.label] == true) "v" else ">"
            val color = when (r.label) {
                "Elusive Chance" -> ChatFormatting.RED.color!!
                "Wayfinder Data" -> FishRarityColor.baseColor
                "Pearl Chance" -> PearlQualityColor.baseColor
                "Treasure Chance" -> TreasureRarityColor.baseColor
                "Spirit Chance" -> SpiritPurityColor.baseColor
                "Fish Chance" -> ChatFormatting.AQUA.color!!
                else -> ChatFormatting.AQUA.color!!
            }
            val buttonLabel = Component.literal("$caret ").mccFont()
                .append(Component.literal(r.label).mccFont().withColor(color))
                .append(Component.literal(": ").mccFont())
                .append(Component.literal(totalStr).mccFont().withStyle(ChatFormatting.AQUA))

            Button.builder(buttonLabel) {
                expanded[r.label] = !(expanded[r.label] ?: false)
                this@ChancePerksDialog.refresh()
            }.bounds(0, 0, 220, 12).build().at(row++, 0, settings = LayoutConstants.LEFT)

            if (expanded[r.label] == true) {
                val detailed = Component.literal("").mccFont()
                    .append(Component.literal(baseStr).mccFont().withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(" + ").mccFont().withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(spotStr).mccFont().withStyle(ChatFormatting.DARK_AQUA))
                    .append(Component.literal(" + ").mccFont().withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(windStr).mccFont().withStyle(ChatFormatting.BLUE))
                    .append(Component.literal(" + ").mccFont().withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(otherStr).mccFont().withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(" = $totalStr").mccFont().withStyle(ChatFormatting.GRAY))
                StringWidget(detailed, font).at(row++, 0, settings = LayoutConstants.LEFT)
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


