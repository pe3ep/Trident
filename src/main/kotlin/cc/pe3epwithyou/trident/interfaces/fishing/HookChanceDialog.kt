package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.Rarity
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
        TridentClient.playerState.perkState = cc.pe3epwithyou.trident.state.fishing.PerkStateCalculator.recompute(
            TridentClient.playerState
        )
        val ps = TridentClient.playerState.perkState

        var row = 0
        StringWidget(Component.literal("HOOKS").mccFont().withStyle(ChatFormatting.AQUA), font)
            .at(row++, 0, settings = LayoutConstants.LEFT)

        UpgradeLine.entries.forEach { line ->
            val pts = ps.totals[line]?.get(UpgradeType.HOOK)?.total ?: 0
            val mult = hookMultiplier(pts)
            val head = Component.literal("${line.name.lowercase().replaceFirstChar { it.uppercase() }}: ")
                .mccFont()
                .append(Component.literal("x${"""%.1f""".format(mult)}").mccFont().withStyle(ChatFormatting.AQUA))
            StringWidget(head, font).at(row++, 0, settings = LayoutConstants.LEFT)

            val baseCats = hookBase(line)
            val boosted = applyMultiplier(line, baseCats, hookTargets(line), mult)
            val baseRow = renderChancesRow("Base:", baseCats, ChatFormatting.GRAY, font)
            baseRow.at(row++, 0, settings = LayoutConstants.LEFT)
            val realRow = renderChancesRow("Real:", boosted, ChatFormatting.AQUA, font)
            realRow.at(row++, 0, settings = LayoutConstants.LEFT)
        }
    }

    override fun refresh() {
        title = getWidgetTitle()
        super.refresh()
    }
}


