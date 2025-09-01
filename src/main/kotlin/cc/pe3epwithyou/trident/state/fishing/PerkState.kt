package cc.pe3epwithyou.trident.state.fishing

import cc.pe3epwithyou.trident.state.PlayerState
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.state.fishing.OverclockTexture
import cc.pe3epwithyou.trident.state.fishing.UpgradeLine
import cc.pe3epwithyou.trident.state.fishing.UpgradeType
import cc.pe3epwithyou.trident.utils.ItemParser


data class PerkTotals(
    val base: Int = 0,
    val augment: Int = 0,
    val overclock: Int = 0,
    val unstable: Int = 0,
    val equipment: Int = 0,
) {
    val total: Int get() = base + augment + overclock + unstable + equipment
}

data class PerkState(
    val totals: MutableMap<UpgradeLine, MutableMap<UpgradeType, PerkTotals>> = mutableMapOf()
)

object PerkStateCalculator {
    fun recompute(player: PlayerState): PerkState {
        val result = PerkState()
        val upgrades = player.upgrades
        val supplies = player.supplies

        // Equipment bonuses only if equipped (non-null and > 0)
        val baitBonus = if ((supplies.bait.amount ?: 0) > 0) {
            when (supplies.bait.type) {
                Rarity.COMMON -> 1
                Rarity.UNCOMMON -> 2
                Rarity.RARE -> 4
                Rarity.EPIC -> 7
                Rarity.LEGENDARY -> 10
                Rarity.MYTHIC -> 15
            }
        } else 0
        val lineBonus = if ((supplies.line.uses ?: 0) > 0) {
            when (supplies.line.type) {
                Rarity.COMMON -> 2
                Rarity.UNCOMMON -> 4
                Rarity.RARE -> 7
                Rarity.EPIC -> 10
                Rarity.LEGENDARY -> 15
                Rarity.MYTHIC -> 20
            }
        } else 0

        val selectedHook = supplies.overclocks.hook
        val selectedMagnet = supplies.overclocks.magnet
        val selectedRod = supplies.overclocks.rod

        val overclockLineForType: (UpgradeType) -> UpgradeLine? = { type ->
            val aug = when (type) {
                UpgradeType.HOOK -> selectedHook
                UpgradeType.MAGNET -> selectedMagnet
                UpgradeType.ROD -> selectedRod
                else -> null
            }
            aug?.let { ItemParserLike.parseLineFromName(it.augmentName) }
        }

        UpgradeLine.entries.forEach { line ->
            val bucket = result.totals.getOrPut(line) { mutableMapOf() }
            UpgradeType.entries.forEach { type ->
                val base = upgrades.getLevel(line, type)
                val augment = supplies.augments.sumOf { ma ->
                    if (ma.paused) 0 else {
                        val a = ma.augment
                        if (a.affectsType == type && a.affectsLine == line) a.bonusPoints else 0
                    }
                }
                val ocLevel = when (type) {
                    UpgradeType.HOOK -> supplies.overclocks.stableLevels.hook
                    UpgradeType.MAGNET -> supplies.overclocks.stableLevels.magnet
                    UpgradeType.ROD -> supplies.overclocks.stableLevels.rod
                    else -> null
                } ?: 0
                val oc = if (overclockLineForType(type) == line) ocLevel else 0

                val unstable = if (type == UpgradeType.CHANCE && player.supplies.overclocks.unstable.isActive) {
                    val tex = supplies.overclocks.unstable.texture
                    val lineMatch = when (tex) {
                        OverclockTexture.STRONG_UNSTABLE -> UpgradeLine.STRONG
                        OverclockTexture.WISE_UNSTABLE -> UpgradeLine.WISE
                        OverclockTexture.GLIMMERING_UNSTABLE -> UpgradeLine.GLIMMERING
                        OverclockTexture.GREEDY_UNSTABLE -> UpgradeLine.GREEDY
                        OverclockTexture.LUCKY_UNSTABLE -> UpgradeLine.LUCKY
                        else -> null
                    }
                    if (lineMatch == line) supplies.overclocks.unstable.level ?: 0 else 0
                } else 0

                val equipment = when (type) {
                    UpgradeType.HOOK -> baitBonus
                    UpgradeType.ROD -> lineBonus
                    else -> 0
                }

                bucket[type] = PerkTotals(base, augment, oc, unstable, equipment)
            }
        }

        return result
    }
}

// Minimal helper to map names to lines to avoid importing ItemParser here
private object ItemParserLike {
    fun parseLineFromName(name: String): UpgradeLine? {
        val n = name.lowercase()
        for (line in UpgradeLine.entries) {
            for (label in line.allLabels()) {
                if (n.contains(label.lowercase())) return line
            }
        }
        return null
    }
}


