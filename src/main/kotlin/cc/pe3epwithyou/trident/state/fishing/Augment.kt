package cc.pe3epwithyou.trident.state.fishing

import net.minecraft.resources.ResourceLocation

enum class Augment(
    val augmentName: String,
    val texturePath: ResourceLocation,
    val textureWidth: Int = 16,
    val textureHeight: Int = textureWidth,
    val asociatedOverclockTexture: OverclockTexture? = null
) {
   // Hook Augments (can be used by the hook overclock)
    STRONG_HOOK(
       "Strong Hook",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/strong_hook"),
        asociatedOverclockTexture = OverclockTexture.STRONG_HOOK
    ),
    WISE_HOOK(
        "Wise Hook",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/wise_hook"),
        asociatedOverclockTexture = OverclockTexture.WISE_HOOK
    ),
    GLIMMERING_HOOK(
        "Glimmering Hook",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/glimmering_hook"),
        asociatedOverclockTexture = OverclockTexture.GLIMMERING_HOOK
    ),
    GREEDY_HOOK(
        "Greedy Hook",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/greedy_hook"),
        asociatedOverclockTexture = OverclockTexture.GREEDY_HOOK
    ),
    LUCKY_HOOK(
        "Lucky Hook",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/lucky_hook"),
        asociatedOverclockTexture = OverclockTexture.LUCKY_HOOK
    ),

    // Magnet Augments (can be used by the magnet overclock)
    XP_MAGNET(
        "XP Magnet",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/xp_magnet"),
        asociatedOverclockTexture = OverclockTexture.XP_MAGNET
    ),
    FISH_MAGNET(
        "Fish Magnet",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/fish_magnet"),
        asociatedOverclockTexture = OverclockTexture.FISH_MAGNET
    ),
    PEARL_MAGNET(
        "Pearl Magnet",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/pearl_magnet"),
        asociatedOverclockTexture = OverclockTexture.PEARL_MAGNET
    ),
    TREASURE_MAGNET(
        "Treasure Magnet",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/treasure_magnet"),
        asociatedOverclockTexture = OverclockTexture.TREASURE_MAGNET
    ),
    SPIRIT_MAGNET(
        "Spirit Magnet",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/spirit_magnet"),
        asociatedOverclockTexture = OverclockTexture.SPIRIT_MAGNET
    ),

    // Rod Augments (can be used by the rod overclock)
    BOOSTED_ROD(
        "Boosted Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/boosted_rod"),
        asociatedOverclockTexture = OverclockTexture.BOOSTED_ROD
    ),
    SPEEDY_ROD(
        "Speedy Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/speedy_rod"),
        asociatedOverclockTexture = OverclockTexture.SPEEDY_ROD
    ),
    GRACEFUL_ROD(
        "Graceful Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/graceful_rod"),
        asociatedOverclockTexture = OverclockTexture.GRACEFUL_ROD
    ),
    GLITCHED_ROD(
        "Glitched Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/glitched_rod"),
        asociatedOverclockTexture = OverclockTexture.GLITCHED_ROD
    ),
    STABLE_ROD(
        "Stable Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/perk_icon/stable_rod"),
        asociatedOverclockTexture = OverclockTexture.STABLE_ROD
    ),

    // Lure Augments (can be used by the unstable overclock)
    ELUSUVE_LURE(
        "Elusive Lure",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_lure_strong")
    ),
    WAYFINDER_LURE(
        "Wayfinder Lure",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_lure_wise")
    ),
    PEARL_LURE(
        "Pearl Lure",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_lure_glimmering")
    ),
    TREASURE_LURE(
        "Treasure Lure",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_lure_greedy")
    ),
    SPIRIT_LURE(
        "Spirit Lure",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_lure_lucky")
    ),

    // Ultralure Augments
    ELUSUVE_ULTRALURE(
        "Elusive Ultralure",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_ultralure_strong"),
        16,
        256
    ),
    WAYFINDER_ULTRALURE(
        "Wayfinder Ultralure",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_ultralure_wise"),
        16,
        256
    ),
    PEARL_ULTRALURE(
        "Pearl Ultralure",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_ultralure_glimmering"),
        16,
        256
    ),
    TREASURE_ULTRALURE(
        "Treasure Ultralure",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_ultralure_greedy"),
        16,
        256
    ),
    SPIRIT_ULTRALURE(
        "Spirit Ultralure",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_ultralure_lucky"),
        16,
        256
    ),

    // Other Augments (cannot be used by ANY overclocks)
    ELUSIVE_SODA(
        "Elusive Soda",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_elusive_pop"),
    ),
    RARITY_ROD(
        "Rarity Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_rarity_rod"),
        16,
        240
    ),
    PURE_BEACON(
        "Pure Beacon",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_pure_beacon"),
    ),
    LURE_BATTERY(
        "Lure Battery",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_lure_battery"),
    ),
    STOCK_REPLENISHER(
        "Stock Replenisher",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_stock_replenisher"),
    ),
    AUTO_ROD(
        "Auto Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/anglr_auto_rod"),
    ),


    // Amulets Augments
    STRONG_AMULET(
    "Strong Amulet",
    ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/amulet_strong"),
    16,
    208
    ),
    WISE_AMULET(
    "Wise Amulet",
    ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/amulet_wise"),
    16,
    208
    ),
    GLIMMERING_AMULET(
    "Glimmering Amulet",
    ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/amulet_glimmering"),
    16,
    208
    ),
    GREEDY_AMULET(
    "Greedy Amulet",
    ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/amulet_greedy"),
    16,
    208
    ),
    LUCKY_AMULET(
    "Lucky Amulet",
    ResourceLocation.fromNamespaceAndPath("mcc", "island_items/infinibag/fishing_item/amulet_lucky"),
    16,
    208
    ),

    EMPTY_AUGMENT(
        "Empty Augment",
        ResourceLocation.fromNamespaceAndPath("trident", "interface/empty_augment")
    )

}

fun getAugmentByName(name: String): Augment? {
    Augment.entries.forEach { augment ->
        if (augment.augmentName == name) return augment
    }
    return null
}
