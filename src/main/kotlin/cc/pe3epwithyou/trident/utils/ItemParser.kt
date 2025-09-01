package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.OverclockTexture
import cc.pe3epwithyou.trident.state.fishing.getAugmentByName
import cc.pe3epwithyou.trident.state.fishing.PlayerUpgrades
import cc.pe3epwithyou.trident.state.fishing.UpgradeLine
import cc.pe3epwithyou.trident.state.fishing.UpgradeType
import cc.pe3epwithyou.trident.state.fishing.UseCondition
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import net.minecraft.world.item.ItemStack

object ItemParser {
    fun getActiveOverclock(item: ItemStack): Augment? {
        var beginSearch = false
        item.getLore().forEach { line ->
//            Due to people having different upgrade costs, it's easier to simply go over each line and start
//            searching overclocks once we reach this text
            if ("Overclocked Perk:" in line.string) {
                beginSearch = true
            }
            if (beginSearch && line.string == "") {
                return null
            }
            if (beginSearch && ">" in line.string) {
                val name = line.string.split("> ")[1]
                val active = getAugmentByName(name)
                return active
            }
        }
        return null
    }

    fun getUnstableOverclock(item: ItemStack): OverclockTexture? {
        var beginSearch = false
        item.getLore().forEach { line ->
//            Get overclock duration, as it can be different depending on the upgrade
            if ("Duration: " in line.string) {
                val minutes = line.string.split(": ")[1]
                if (minutes.split(" ").size == 1) {
                    val minuteInt = minutes.dropLast(1).toInt()
                    TridentClient.playerState.supplies.overclocks.unstable.duration = minuteInt * 60L * 20L
                } else {
                    val minuteInt = minutes.split(" ")[1].dropLast(1).toInt()
                    TridentClient.playerState.supplies.overclocks.unstable.duration = minuteInt * 60L * 20L
                }
            }
            if ("Overclocked Perk:" in line.string) {
                beginSearch = true
            }
            if (beginSearch && ">" in line.string) {
                // this is horrible but it works
                val perk = line.string.split("> ")[1].split(" ")[1]
                when {
                    perk == "Elusive" -> return OverclockTexture.STRONG_UNSTABLE
                    perk == "Wayfinder" -> return OverclockTexture.WISE_UNSTABLE
                    perk == "Pearl" -> return OverclockTexture.GLIMMERING_UNSTABLE
                    perk == "Treasure" -> return OverclockTexture.GREEDY_UNSTABLE
                    perk == "Spirit" -> return OverclockTexture.LUCKY_UNSTABLE
                    else -> return null
                }
            }
        }
        return null
    }

    fun getOverclockLevel(item: ItemStack): Int? {
        val nameCandidates = listOf(
            item.displayName.string,
            item.hoverName.string
        )
        val pattern = Regex("""\((\d+)\s*/\s*\d+\)""")
        nameCandidates.forEach { n ->
            val m = pattern.find(n)
            if (m != null) return m.groups[1]?.value?.toIntOrNull()
        }
        return null
    }

    // Fishing Perks (ANGLR -> Fishing Perks) parsing helpers
    fun parseUpgradeLine(name: String): UpgradeLine? {
        val n = name.lowercase()
        for (line in UpgradeLine.entries) {
            for (label in line.allLabels()) {
                if (n.contains(label.lowercase())) return line
            }
        }
        return null
    }

    fun parseUpgradeType(name: String): UpgradeType? = when {
        name.contains("Hook", true) -> UpgradeType.HOOK
        name.contains("Magnet", true) -> UpgradeType.MAGNET
        name.contains("Rod", true) -> UpgradeType.ROD
        name.contains("Pot", true) -> UpgradeType.POT
        name.contains("Chance", true) -> UpgradeType.CHANCE
        name.contains("Wayfinder", true) -> UpgradeType.CHANCE
        else -> null
    }

    fun parseUpgradeLevelFromName(name: String): Int? {
        // Expected formats include: "(8/20)", "Lv 8" etc. Prefer (cur/max)
        val paren = Regex("\\((\\d+)\\s*/\\s*\\d+\\)").find(name)?.groups?.get(1)?.value?.toIntOrNull()
        if (paren != null) return paren
        val lv = Regex("(?i)lv\\s*(\\d+)").find(name)?.groups?.get(1)?.value?.toIntOrNull()
        if (lv != null) return lv
        return null
    }

    fun getAugmentUses(item: ItemStack): Pair<Int?, Int?>? {
        val nameCandidates = listOf(
            item.displayName.string,
            item.hoverName.string
        )
        val pattern = Regex("""(?i)Uses\s*Remaining\s*:?\s*(\d+)\s*/\s*(\d+)""")
        nameCandidates.forEach { n ->
            val m = pattern.find(n)
            if (m != null) {
                val cur = m.groups[1]?.value?.toIntOrNull()
                val max = m.groups[2]?.value?.toIntOrNull()
                return Pair(cur, max)
            }
        }
        // Fallback to lore if needed
        item.getLore().forEach { line ->
            val m = pattern.find(line.string)
            if (m != null) {
                val cur = m.groups[1]?.value?.toIntOrNull()
                val max = m.groups[2]?.value?.toIntOrNull()
                return Pair(cur, max)
            }
        }
        return null
    }

    data class ParsedAugmentMeta(val condition: UseCondition?, val bannedInGrotto: Boolean)

    fun getAugmentUseCondition(item: ItemStack): ParsedAugmentMeta {
        val lore = item.getLore().map { it.string.lowercase() }
        val key = lore.firstOrNull { it.contains("use condition") }
        val condition = when {
            key == null -> null
            key.contains("spirit") -> UseCondition.SPIRIT
            key.contains("pearl") -> UseCondition.PEARL
            key.contains("elusive") -> UseCondition.ELUSIVE_FISH
            key.contains("treasure") -> UseCondition.TREASURE
            key.contains("grotto") -> UseCondition.IN_GROTTO
            key.contains("anything") -> UseCondition.ANYTHING
            key.contains("cast into spot") -> UseCondition.CAST_INTO_SPOT
            key.contains("fish") -> UseCondition.FISH
            else -> null
        }
        val banned = lore.any { it.contains("does not work in grottos") }
        return ParsedAugmentMeta(condition, banned)
    }

    fun isAugmentPaused(item: ItemStack): Boolean {
        val lore = item.getLore().map { it.string.lowercase() }
        return lore.any { it.contains("paused:") }
    }

    data class SpotBonuses(
        val hookPercents: MutableMap<UpgradeLine, Double> = mutableMapOf(),
        val magnetPercents: MutableMap<UpgradeLine, Double> = mutableMapOf(),
        var elusiveChanceBonusPercent: Double = 0.0,
        var pearlChanceBonusPercent: Double = 0.0,
        var treasureChanceBonusPercent: Double = 0.0,
        var spiritChanceBonusPercent: Double = 0.0,
        var wayfinderDataBonus: Double = 0.0,
        var fishChanceBonusPercent: Double = 0.0,
    )

    fun parseSpotBonuses(lines: List<String>): SpotBonuses {
        val res = SpotBonuses()
        val hookRegex = Regex("([-+]?\\d+(?:\\.\\d+)?)%\\s*.*?(Strong|Wise|Glimmering|Greedy|Lucky)\\s*Hook", RegexOption.IGNORE_CASE)
        val magnetRegex = Regex("([-+]?\\d+(?:\\.\\d+)?)%\\s*.*?(XP|Fish|Pearl|Treasure|Spirit)\\s*Magnet", RegexOption.IGNORE_CASE)
        val fishChanceRegex = Regex("([-+]?\\d+(?:\\.\\d+)?)%\\s*Fish\\s*Chance", RegexOption.IGNORE_CASE)
        val elusiveRegex = Regex("([-+]?\\d+(?:\\.\\d+)?)%\\s*Elusive", RegexOption.IGNORE_CASE)
        val pearlRegex = Regex("([-+]?\\d+(?:\\.\\d+)?)%\\s*Pearl\\s*Chance", RegexOption.IGNORE_CASE)
        val treasureRegex = Regex("([-+]?\\d+(?:\\.\\d+)?)%\\s*Treasure\\s*Chance", RegexOption.IGNORE_CASE)
        val spiritRegex = Regex("([-+]?\\d+(?:\\.\\d+)?)%\\s*Spirit\\s*Chance", RegexOption.IGNORE_CASE)
        val wayfinderRegex = Regex("([-+]?\\d+(?:\\.\\d+)?)\\s*Wayfinder\\s*Data", RegexOption.IGNORE_CASE)

        fun mapHookLine(s: String): UpgradeLine? = when (s.lowercase()) {
            "strong" -> UpgradeLine.STRONG
            "wise" -> UpgradeLine.WISE
            "glimmering" -> UpgradeLine.GLIMMERING
            "greedy" -> UpgradeLine.GREEDY
            "lucky" -> UpgradeLine.LUCKY
            else -> null
        }
        fun mapMagnetLine(s: String): UpgradeLine? = when (s.lowercase()) {
            "xp" -> UpgradeLine.STRONG
            "fish" -> UpgradeLine.WISE
            "pearl" -> UpgradeLine.GLIMMERING
            "treasure" -> UpgradeLine.GREEDY
            "spirit" -> UpgradeLine.LUCKY
            else -> null
        }
        

        lines.map { it.trim() }.forEach { line ->
            hookRegex.find(line)?.let { m ->
                val pct = m.groupValues[1].toDoubleOrNull() ?: return@let
                val lineName = m.groupValues[2]
                mapHookLine(lineName)?.let {
                    val prev = res.hookPercents[it] ?: 0.0
                    res.hookPercents[it] = prev + pct
                }
                return@forEach
            }
            magnetRegex.find(line)?.let { m ->
                val pct = m.groupValues[1].toDoubleOrNull() ?: return@let
                val which = m.groupValues[2]
                mapMagnetLine(which)?.let {
                    val prev = res.magnetPercents[it] ?: 0.0
                    res.magnetPercents[it] = prev + pct
                }
                return@forEach
            }
            fishChanceRegex.find(line)?.let { m ->
                res.fishChanceBonusPercent = (m.groupValues[1].toDoubleOrNull() ?: 0.0)
                return@forEach
            }
            elusiveRegex.find(line)?.let { m ->
                res.elusiveChanceBonusPercent = (m.groupValues[1].toDoubleOrNull() ?: 0.0)
                return@forEach
            }
            pearlRegex.find(line)?.let { m ->
                res.pearlChanceBonusPercent = (m.groupValues[1].toDoubleOrNull() ?: 0.0)
                return@forEach
            }
            treasureRegex.find(line)?.let { m ->
                res.treasureChanceBonusPercent = (m.groupValues[1].toDoubleOrNull() ?: 0.0)
                return@forEach
            }
            spiritRegex.find(line)?.let { m ->
                res.spiritChanceBonusPercent = (m.groupValues[1].toDoubleOrNull() ?: 0.0)
                return@forEach
            }
            wayfinderRegex.find(line)?.let { m ->
                res.wayfinderDataBonus = (m.groupValues[1].toDoubleOrNull() ?: 0.0)
                return@forEach
            }
        }

        return res
    }
}