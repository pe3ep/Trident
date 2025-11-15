package cc.pe3epwithyou.trident.state.fishing

enum class Perk(
    val visualName: String, val usesInt: Boolean = false
) {
    // Hooks
    STRONG_HOOK("Strong Hook"),
    WISE_HOOK("Wise Hook"),
    GLIMMERING_HOOK("Glimmering Hook"),
    GREEDY_HOOK("Greedy Hook"),
    LUCKY_HOOK("Lucky Hook"),

    // Magnets
    XP_MAGNET("XP Magnet"),
    FISH_MAGNET("Fish Magnet"),
    PEARL_MAGNET("Pearl Magnet"),
    TREASURE_MAGNET("Treasure Magnet"),
    SPIRIT_MAGNET("Spirit Magnet"),

    // Rods
    BOOSTED_ROD("Boosted Rod"),
    SPEEDY_ROD("Speedy Rod"),
    GRACEFUL_ROD("Graceful Rod"),
    GLITCHED_ROD("Glitched Rod"),
    STABLE_ROD("Stable Rod"),

    // Lures
    ELUSIVE_FISH_CHANCE("Elusive Fish Chance"),
    WAYFINDER_DATA("Wayfinder Data", true),
    PEARL_CHANCE("Pearl Chance"),
    TREASURE_CHANCE("Treasure Chance"),
    SPIRIT_CHANCE("Spirit Chance"),

    ;

    WAYFINDER_DATA("Wayfinder Data", true),

//    Hooks
    LUCKY_HOOK("Lucky Hook"),
    GREEDY_HOOK("Lucky Hook"),
    WISE_HOOK("Lucky Hook"),
    GLIMMERING_HOOK("Lucky Hook"),

//    Magnets
    LUCKY_MAGNET("Lucky Magnet")
    ;

    companion object {
        fun getPerkByName(name: String): Perk? = Perk.entries.filter { perk -> perk.visualName == name }.getOrNull(0)
    }
}