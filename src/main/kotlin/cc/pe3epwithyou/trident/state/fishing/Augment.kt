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
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/strong_hook.png"),
        asociatedOverclockTexture = OverclockTexture.STRONG_HOOK
    ),
    WISE_HOOK(
        "Wise Hook",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/wise_hook.png"),
        asociatedOverclockTexture = OverclockTexture.WISE_HOOK
    ),
    GLIMMERING_HOOK(
        "Glimmering Hook",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/glimmering_hook.png"),
        asociatedOverclockTexture = OverclockTexture.GLIMMERING_HOOK
    ),
    GREEDY_HOOK(
        "Greedy Hook",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/greedy_hook.png"),
        asociatedOverclockTexture = OverclockTexture.GREEDY_HOOK
    ),
    LUCKY_HOOK(
        "Lucky Hook",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/lucky_hook.png"),
        asociatedOverclockTexture = OverclockTexture.LUCKY_HOOK
    ),

    // Magnet Augments (can be used by the magnet overclock)
    XP_MAGNET(
        "XP Magnet",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/xp_magnet.png"),
        asociatedOverclockTexture = OverclockTexture.XP_MAGNET
    ),
    FISH_MAGNET(
        "Fish Magnet",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/fish_magnet.png"),
        asociatedOverclockTexture = OverclockTexture.FISH_MAGNET
    ),
    PEARL_MAGNET(
        "Pearl Magnet",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/pearl_magnet.png"),
        asociatedOverclockTexture = OverclockTexture.PEARL_MAGNET
    ),
    TREASURE_MAGNET(
        "Treasure Magnet",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/treasure_magnet.png"),
        asociatedOverclockTexture = OverclockTexture.TREASURE_MAGNET
    ),
    SPIRIT_MAGNET(
        "Spirit Magner",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/spirit_magnet.png"),
        asociatedOverclockTexture = OverclockTexture.SPIRIT_MAGNET
    ),

    // Rod Augments (can be used by the rod overclock)
    BOOSTED_ROD(
        "Boosted Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/boosted_rod.png"),
        asociatedOverclockTexture = OverclockTexture.BOOSTED_ROD
    ),
    SPEEDY_ROD(
        "Speedy Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/speedy_rod.png"),
        asociatedOverclockTexture = OverclockTexture.SPEEDY_ROD
    ),
    GRACEFUL_ROD(
        "Graceful Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/graceful_rod.png"),
        asociatedOverclockTexture = OverclockTexture.GRACEFUL_ROD
    ),
    GLITCHED_ROD(
        "Glitched Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/glitched_rod.png"),
        asociatedOverclockTexture = OverclockTexture.GLITCHED_ROD
    ),
    STABLE_ROD(
        "Stable Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/stable_rod.png"),
        asociatedOverclockTexture = OverclockTexture.STABLE_ROD
    ),

    // Lure Augments (can be used by the unstable overclock)
    ELUSUVE_LURE(
        "Elusive Lure",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_lure_strong.png")
    ),
    WAYFINDER_LURE(
        "Wayfinder Lure",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_lure_wise.png")
    ),
    PEARL_LURE(
        "Pearl Lure",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_lure_glimmering.png")
    ),
    TREASURE_LURE(
        "Treasure Lure",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_lure_greedy.png")
    ),
    SPIRIT_LURE(
        "Spirit Lure",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_lure_lucky.png")
    ),

    // Ultralure Augments
    ELUSUVE_ULTRALURE(
        "Elusive Ultralure",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_ultralure_strong.png"),
        16,
        256
    ),
    WAYFINDER_ULTRALURE(
        "Wayfinder Ultralure",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_ultralure_wise.png"),
        16,
        256
    ),
    PEARL_ULTRALURE(
        "Pearl Ultralure",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_ultralure_glimmering.png"),
        16,
        256
    ),
    TREASURE_ULTRALURE(
        "Treasure Ultralure",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_ultralure_greedy.png"),
        16,
        256
    ),
    SPIRIT_ULTRALURE(
        "Spirit Ultralure",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_ultralure_lucky.png"),
        16,
        256
    ),

    // Other Augments (cannot be used by ANY overclocks)
    ELUSIVE_SODA(
        "Elusive Soda",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_elusive_pop.png"),
    ),
    RARITY_ROD(
        "Rarity Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_rarity_rod.png"),
        16,
        240
    ),
    PURE_BEACON(
        "Pure Beacon",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_pure_beacon.png"),
    ),
    LURE_BATTERY(
        "Lure Battery",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_lure_battery.png"),
    ),
    STOCK_REPLENISHER(
        "Stock Replenisher",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_stock_replenisher.png"),
    ),
    AUTO_ROD(
        "Auto Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_auto_rod.png"),
    ),


    // Amulets Augments
    STRONG_AMULET(
    "Strong Amulet",
    ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/amulet_strong.png"),
    16,
    208
    ),
    WISE_AMULET(
    "Wise Amulet",
    ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/amulet_wise.png"),
    16,
    208
    ),
    GLIMMERING_AMULET(
    "Glimmering Amulet",
    ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/amulet_glimmering.png"),
    16,
    208
    ),
    GREEDY_AMULET(
    "Greedy Amulet",
    ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/amulet_greedy.png"),
    16,
    208
    ),
    LUCKY_AMULET(
    "Lucky Amulet",
    ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/amulet_lucky.png"),
    16,
    208
    );

}

fun getAugmentByName(name: String): Augment? {
    Augment.entries.forEach { augment ->
        if (augment.augmentName == name) return augment
    }
    return null
}
