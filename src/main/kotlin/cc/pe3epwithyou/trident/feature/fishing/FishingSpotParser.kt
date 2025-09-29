package cc.pe3epwithyou.trident.feature.fishing

import cc.pe3epwithyou.trident.state.fishing.Perk
import net.minecraft.network.chat.Component

object FishingSpotParser {
    fun parse(text: Component): List<Pair<Perk, Double>> {
        val stripped = text.string.split("\n")
        val perks = mutableListOf<Pair<Perk, Double>>()
        stripped.subList(4, stripped.lastIndex + 1).forEach { l ->
            val match = Regex("""^.\+(\d+)(?:% | )(.+)""").matchEntire(l) ?: return@forEach
            val perkData = (match.groups[1]?.value?.toDoubleOrNull()) ?: return@forEach
            val perkString = match.groups[2]?.value ?: return@forEach

            val perk = Perk.get(perkString) ?: return@forEach
            val perkValue: Double = if (perk.usesInt) perkData else (perkData / 100)
            perks.add(Pair(perk, perkValue))
        }
        return perks
    }
}