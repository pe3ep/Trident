package cc.pe3epwithyou.trident.state

enum class FishWeightColor(val color: Int) {
    AVERAGE(Rarity.RARE.color),
    LARGE(Rarity.EPIC.color),
    MASSIVE(Rarity.LEGENDARY.color),
    GARGANTUAN(Rarity.MYTHIC.color);

    companion object {
        // Base coloration taken from Strong line (Mythic)
        val baseColor: Int = Rarity.MYTHIC.color
    }
}

enum class FishRarityColor(val color: Int) {
    COMMON(Rarity.COMMON.color),
    UNCOMMON(Rarity.UNCOMMON.color),
    RARE(Rarity.RARE.color),
    EPIC(Rarity.EPIC.color),
    LEGENDARY(Rarity.LEGENDARY.color),
    MYTHIC(Rarity.MYTHIC.color);

    companion object {
        // Base coloration taken from Wise line (Rare)
        val baseColor: Int = Rarity.RARE.color
    }
}

enum class PearlQualityColor(val color: Int) {
    ROUGH(Rarity.COMMON.color),
    POLISHED(Rarity.RARE.color),
    PRISTINE(Rarity.EPIC.color);

    companion object {
        // Base coloration taken from Glimmering line (Epic)
        val baseColor: Int = Rarity.EPIC.color
    }
}

enum class TreasureRarityColor(val color: Int) {
    COMMON(Rarity.COMMON.color),
    UNCOMMON(Rarity.UNCOMMON.color),
    RARE(Rarity.RARE.color),
    EPIC(Rarity.EPIC.color),
    LEGENDARY(Rarity.LEGENDARY.color),
    MYTHIC(Rarity.MYTHIC.color);

    companion object {
        // Base coloration taken from Greedy line (Legendary)
        val baseColor: Int = Rarity.LEGENDARY.color
    }
}

enum class SpiritPurityColor(val color: Int) {
    NORMAL(Rarity.UNCOMMON.color),
    REFINED(Rarity.EPIC.color),
    PURE(Rarity.MYTHIC.color);

    companion object {
        // Base coloration taken from Lucky line (Uncommon)
        val baseColor: Int = Rarity.UNCOMMON.color
    }
}


