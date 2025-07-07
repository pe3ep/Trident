package cc.pe3epwithyou.trident.state.fishing

enum class Augment {
   // Hook Augments (can be used by the hook overclock)
    STRONG_HOOK,
    WISE_HOOK,
    GLIMMERING_HOOK,
    GREEDY_HOOK,
    LUCKY_HOOK,

    // Magnet Augments (can be used by the magnet overclock)
    XP_MAGNET,
    FISH_MAGNET,
    PEARL_MAGNET,
    TREASURE_MAGNET,
    SPIRIT_MAGNET,

    // Rod Augments (can be used by the rod overclock)
    BOOSTED_ROD,
    SPEEDY_ROD,
    GRACEFUL_ROD,
    GLITCHED_ROD,
    STABLE_ROD,

    // Lure Augments (can be used by the unstable overclock)
    ELUSUVE_LURE,
    WAYFINDER_LURE,
    PEARL_LURE,
    TREASURE_LURE,
    SPIRIT_LURE,

    // Other Augments (cannot be used by ANY overclocks)
    ELUSIVE_SODA,
    RARITY_ROD,
    PURE_BEACON,
    LURE_BATTERY,
}

val AUGMENT_NAMES = hashMapOf<String, Augment>(
    "Strong Hook" to Augment.STRONG_HOOK,
    "Wise Hook" to Augment.WISE_HOOK,
    "Glimmering Hook" to Augment.GLIMMERING_HOOK,
    "Greedy Hook" to Augment.GREEDY_HOOK,
    "Lucky Hook" to Augment.LUCKY_HOOK,

    "XP Magnet" to Augment.XP_MAGNET,
    "Fish Magnet" to Augment.FISH_MAGNET,
    "Pearl Magnet" to Augment.PEARL_MAGNET,
    "Treasure Magnet" to Augment.TREASURE_MAGNET,
    "Spirit Magnet" to Augment.SPIRIT_MAGNET,

    "Boosted Rod" to Augment.BOOSTED_ROD,
    "Speedy Rod" to Augment.SPEEDY_ROD,
    "Graceful Rod" to Augment.GRACEFUL_ROD,
    "Glithced Rod" to Augment.GLITCHED_ROD,
    "Stable Rod" to Augment.STABLE_ROD,

    "Elusive Lure" to Augment.ELUSUVE_LURE,
    "Wayfinder Lure" to Augment.WAYFINDER_LURE,
    "Pearl Lure" to Augment.PEARL_LURE,
    "Treasure Lure" to Augment.TREASURE_LURE,
    "Spirit Lure" to Augment.SPIRIT_LURE,

    "Elusive Soda" to Augment.ELUSIVE_SODA,
)