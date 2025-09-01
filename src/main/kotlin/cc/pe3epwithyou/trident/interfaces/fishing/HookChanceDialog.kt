package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.state.FishWeightColor
import cc.pe3epwithyou.trident.state.PearlQualityColor
import cc.pe3epwithyou.trident.state.SpiritPurityColor
import cc.pe3epwithyou.trident.state.fishing.PerkStateCalculator
import cc.pe3epwithyou.trident.state.fishing.UpgradeLine
import cc.pe3epwithyou.trident.state.fishing.UpgradeType
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.state.fishing.Augment
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.dialog.title.DialogTitleWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class HookChanceDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    private companion object {
        private val TITLE_COLOR: Int = 0x54fcfc opacity 127
    }

    private fun getWidgetTitle(): DialogTitleWidget {
        val icon = Component.literal("\uE279").mccFont("icon").withStyle(Style.EMPTY.withShadowColor(0x0 opacity 0))
        val text = Component.literal(" HOOK CHANCES".uppercase()).mccFont()
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

    private fun hookMultiplier(points: Int): Double = 1.0 + (points * 0.1)

    private data class Cat(val name: String, val pct: Double)

    private fun hookBase(line: UpgradeLine): List<Cat> = when (line) {
        UpgradeLine.STRONG -> listOf(
            Cat("Average", 90.3), Cat("Large", 7.5), Cat("Massive", 2.0), Cat("Gargantuan", 0.2)
        )
        UpgradeLine.WISE -> listOf(
            Cat("Common", 54.8), Cat("Uncommon", 25.0), Cat("Rare", 15.0), Cat("Epic", 4.0), Cat("Legendary", 1.0), Cat("Mythic", 0.2)
        )
        UpgradeLine.GLIMMERING -> listOf(
            Cat("Rough", 94.9), Cat("Polished", 5.0), Cat("Pristine", 0.1)
        )
        UpgradeLine.GREEDY -> listOf(
            Cat("Common", 60.6), Cat("Uncommon", 29.85), Cat("Rare", 7.0), Cat("Epic", 2.0), Cat("Legendary", 0.5), Cat("Mythic", 0.05)
        )
        UpgradeLine.LUCKY -> listOf(
            Cat("Normal", 96.95), Cat("Refined", 3.0), Cat("Pure", 0.05)
        )
    }

    private fun hookTargets(line: UpgradeLine): Set<String> = when (line) {
        UpgradeLine.STRONG -> setOf("Large", "Massive", "Gargantuan")
        UpgradeLine.WISE -> setOf("Epic", "Legendary", "Mythic")
        UpgradeLine.GLIMMERING -> setOf("Polished", "Pristine")
        UpgradeLine.GREEDY -> setOf("Rare", "Epic", "Legendary", "Mythic")
        UpgradeLine.LUCKY -> setOf("Refined", "Pure")
    }

    private fun applyMultiplier(line: UpgradeLine, base: List<Cat>, targets: Set<String>, mult: Double): List<Cat> {
        if (mult == 1.0 || base.isEmpty()) return base
        // Rarity Rod augment: Guarantees Uncommon+ and triples Legendary/Mythic for Wise hook
        val augments = TridentClient.playerState.supplies.augments.map { it.augment }
        if (line == UpgradeLine.WISE && augments.any { it == Augment.RARITY_ROD }) {
            val map = base.associate { it.name to it.pct }.toMutableMap()
            // Triple Legendary/Mythic; take from Common first, then Rare if needed, then proportionally from remaining non-targets, add remainder to Uncommon
            val legendaryNames = setOf("Legendary", "Mythic")
            val tripleTargets = base.filter { it.name in legendaryNames }
            val increase = tripleTargets.sumOf { it.pct * 2.0 }
            var remainingRemoval = increase
            // remove from Common
            val commonBase = map["Common"] ?: 0.0
            val takeCommon = kotlin.math.min(commonBase, remainingRemoval)
            map["Common"] = commonBase - takeCommon
            remainingRemoval -= takeCommon
            if (remainingRemoval > 1e-9) {
                val rareBase = map["Rare"] ?: 0.0
                val takeRare = kotlin.math.min(rareBase, remainingRemoval)
                map["Rare"] = rareBase - takeRare
                remainingRemoval -= takeRare
            }
            if (remainingRemoval > 1e-9) {
                val others = listOf("Uncommon", "Epic").associateWith { map[it] ?: 0.0 }
                val sum = others.values.sum()
                if (sum > 0) {
                    val scale = (sum - remainingRemoval).coerceAtLeast(0.0) / sum
                    others.keys.forEach { k -> map[k] = (map[k] ?: 0.0) * scale }
                    remainingRemoval = 0.0
                }
            }
            // apply triple
            tripleTargets.forEach { t -> map[t.name] = (map[t.name] ?: 0.0) * 3.0 }
            // move any leftover removal to Uncommon add
            val uncommonBase = map["Uncommon"] ?: 0.0
            map["Uncommon"] = (uncommonBase + takeCommon + (map["Rare"] ?: 0.0))
            return base.map { Cat(it.name, map[it.name] ?: 0.0) }
        }
        val baseSum = base.sumOf { it.pct }
        val targetSet = targets.toSet()
        val targetBaseSum = base.filter { it.name in targetSet }.sumOf { it.pct }
        if (targetBaseSum == 0.0) return base
        val targetNewSum = targetBaseSum * mult

        if (targetNewSum >= baseSum - 1e-9) {
            return base.map { c ->
                val pct = if (c.name in targetSet) (c.pct / targetBaseSum) * baseSum else 0.0
                Cat(c.name, pct)
            }
        }

        if (line == UpgradeLine.WISE) {
            val pctMap = base.associate { it.name to it.pct }.toMutableMap()
            targetSet.forEach { t -> pctMap[t]?.let { pctMap[t] = it * mult } }
            var delta = targetNewSum - targetBaseSum
            val commonBase = pctMap["Common"] ?: 0.0
            val takeCommon = kotlin.math.min(commonBase, delta)
            pctMap["Common"] = commonBase - takeCommon
            delta -= takeCommon
            if (delta > 1e-9) {
                val unBase = pctMap["Uncommon"] ?: 0.0
                val takeUn = kotlin.math.min(unBase, delta)
                pctMap["Uncommon"] = unBase - takeUn
                delta -= takeUn
            }
            if (delta > 1e-9) {
                val currentTargetsSum = targetSet.sumOf { pctMap[it] ?: 0.0 }
                val scale = if (currentTargetsSum <= 0.0) 0.0 else (currentTargetsSum - delta) / currentTargetsSum
                targetSet.forEach { t -> pctMap[t]?.let { pctMap[t] = it * scale } }
            }
            return base.map { c -> Cat(c.name, pctMap[c.name] ?: 0.0) }
        }

        val nonTargetBaseSum = baseSum - targetBaseSum
        val nonTargetNewSum = baseSum - targetNewSum
        val nonTargetScale = if (nonTargetBaseSum <= 0.0) 0.0 else nonTargetNewSum / nonTargetBaseSum

        return base.map { c ->
            val pct = if (c.name in targetSet) c.pct * mult else c.pct * nonTargetScale
            Cat(c.name, pct)
        }
    }

    private fun rarityColorFor(name: String): Int? = when (name.lowercase()) {
        "common" -> Rarity.COMMON.color
        "uncommon" -> Rarity.UNCOMMON.color
        "rare" -> Rarity.RARE.color
        "epic" -> Rarity.EPIC.color
        "legendary" -> Rarity.LEGENDARY.color
        "mythic" -> Rarity.MYTHIC.color
        // Fish weight (Strong)
        "average" -> FishWeightColor.AVERAGE.color
        "large" -> FishWeightColor.LARGE.color
        "massive" -> FishWeightColor.MASSIVE.color
        "gargantuan" -> FishWeightColor.GARGANTUAN.color
        // Pearls (Glimmering)
        "rough" -> PearlQualityColor.ROUGH.color
        "polished" -> PearlQualityColor.POLISHED.color
        "pristine" -> PearlQualityColor.PRISTINE.color
        // Spirits (Lucky)
        "normal" -> SpiritPurityColor.NORMAL.color
        "refined" -> SpiritPurityColor.REFINED.color
        "pure" -> SpiritPurityColor.PURE.color
        else -> null
    }

    private fun renderChancesRow(label: String, cats: List<Cat>, accent: ChatFormatting, font: net.minecraft.client.gui.Font): StringWidget {
        var comp: net.minecraft.network.chat.MutableComponent = Component.literal(label).mccFont().withStyle(ChatFormatting.GRAY)
            .append(Component.literal(" ").mccFont())

        cats.forEachIndexed { idx, cat ->
            if (idx > 0) {
                comp = comp.append(Component.literal(" | ").mccFont().withStyle(ChatFormatting.GRAY))
            }
            val abbrev = cat.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            val nameColored = rarityColorFor(cat.name)?.let { color ->
                Component.literal(abbrev).mccFont().withColor(color)
            } ?: Component.literal(abbrev).mccFont().withStyle(accent)

            val pctComp = Component.literal(" ${"""%.2f""".format(cat.pct)}%").mccFont().withStyle(accent)
            comp = comp.append(nameColored).append(pctComp)
        }

        return StringWidget(comp, font)
    }

    override fun layout(): GridLayout = grid {
        val font = Minecraft.getInstance().font
        TridentClient.playerState.perkState = PerkStateCalculator.recompute(
            TridentClient.playerState
        )
        val ps = TridentClient.playerState.perkState

        var row = 0
        StringWidget(Component.literal("HOOKS").mccFont().withStyle(ChatFormatting.AQUA), font)
            .at(row++, 0, settings = LayoutConstants.LEFT)

        UpgradeLine.entries.forEach { line ->
            val basePts = ps.totals[line]?.get(UpgradeType.HOOK)?.total ?: 0
            val spot = TridentClient.playerState.spot
            val tides = TridentClient.playerState.tideLines
            val spotPct = if (spot.hasSpot) (spot.hookPercents[line] ?: 0.0) else 0.0
            val tidePct = if (tides.contains(line)) 20.0 else 0.0
            val combinedPct = spotPct + tidePct
            val effectivePts = basePts * (1.0 + combinedPct / 100.0)
            val mult = 1.0 + (effectivePts * 0.1)
            val deltaSpot = basePts * (spotPct / 100.0)
            val deltaTide = basePts * (tidePct / 100.0)
            val lineLabel = line.name.lowercase().replaceFirstChar { it.uppercase() }
            val caret = if (expanded[line] == true) "v" else ">"
            val condensedCalc = Component.literal(" ${basePts}*(${"""%.0f""".format(spotPct)}%+${"""%.0f""".format(tidePct)}%)")
                .mccFont().withStyle(ChatFormatting.GRAY)
            val headerBase = Component.literal("$caret ").mccFont()
                .append(Component.literal(lineLabel).mccFont().withColor(
                    when(line){
                        UpgradeLine.STRONG -> FishWeightColor.baseColor
                        UpgradeLine.WISE -> Rarity.RARE.color
                        UpgradeLine.GLIMMERING -> PearlQualityColor.baseColor
                        UpgradeLine.GREEDY -> Rarity.LEGENDARY.color
                        UpgradeLine.LUCKY -> SpiritPurityColor.baseColor
                    }
                )).append(Component.literal(": ").mccFont())
                .append(Component.literal("x${"""%.1f""".format(mult)}").mccFont().withStyle(ChatFormatting.AQUA))
            val headerCollapsed = headerBase.copy().append(condensedCalc)
            val headerExpanded = headerBase

            val buttonLabel = if (expanded[line] == true) headerExpanded else headerCollapsed

            Button.builder(buttonLabel) {
                expanded[line] = !(expanded[line] ?: false)
                this@HookChanceDialog.refresh()
            }.bounds(0, 0, 180, 12).build().at(row++, 0, settings = LayoutConstants.LEFT)

            if (expanded[line] == true) {
                val baseCats = hookBase(line)
                val boosted = applyMultiplier(line, baseCats, hookTargets(line), mult)
                // Expanded detailed calculation
                val detail = Component.literal("pts ").mccFont().withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("${basePts}").mccFont().withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(" + ").mccFont().withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("${"""%.2f""".format(spotPct)}% ").mccFont().withStyle(ChatFormatting.DARK_AQUA))
                    .append(Component.literal("(+${"""%.2f""".format(deltaSpot)}) ").mccFont().withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("+ ").mccFont().withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("${"""%.2f""".format(tidePct)}% ").mccFont().withStyle(ChatFormatting.DARK_AQUA))
                    .append(Component.literal("(+${"""%.2f""".format(deltaTide)}) ").mccFont().withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("= ${"""%.2f""".format(effectivePts)}").mccFont().withStyle(ChatFormatting.GRAY))
                StringWidget(detail, font).at(row++, 0, settings = LayoutConstants.LEFT)

                val baseRow = renderChancesRow("Base:", baseCats, ChatFormatting.GRAY, font)
                baseRow.at(row++, 0, settings = LayoutConstants.LEFT)
                val realRow = renderChancesRow("Real:", boosted, ChatFormatting.AQUA, font)
                realRow.at(row++, 0, settings = LayoutConstants.LEFT)
            }
            // extra spacing row between types
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


