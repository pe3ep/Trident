package cc.pe3epwithyou.trident.state.fishing

import net.minecraft.resources.ResourceLocation



enum class Augment(
    val augmentName: String,
    val texturePath: ResourceLocation,
    val textureWidth: Int = 16,
    val textureHeight: Int = textureWidth,
) {
   // Hook Augments (can be used by the hook overclock)
    STRONG_HOOK(
       "Strong Hook",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/strong_hook.png")
    ),
    WISE_HOOK(
        "Wise Hook",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/wise_hook.png")
    ),
    GLIMMERING_HOOK(
        "Glimmering Hook",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/glimmering_hook.png")
    ),
    GREEDY_HOOK(
        "Greedy Hook",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/greedy_hook.png")
    ),
    LUCKY_HOOK(
        "Lucky Hook",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/lucky_hook.png")
    ),

    // Magnet Augments (can be used by the magnet overclock)
    XP_MAGNET(
        "XP Magnet",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/xp_magnet.png")
    ),
    FISH_MAGNET(
        "Fish Magnet",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/fish_magnet.png")
    ),
    PEARL_MAGNET(
        "Pearl Magnet",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/pearl_magnet.png")
    ),
    TREASURE_MAGNET(
        "Treasure Magnet",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/treasure_magnet.png")
    ),
    SPIRIT_MAGNET(
        "Spirit Magner",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/spirit_magnet.png")
    ),

    // Rod Augments (can be used by the rod overclock)
    BOOSTED_ROD(
        "Boosted Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/boosted_rod.png")
    ),
    SPEEDY_ROD(
        "Speedy Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/speedy_rod.png")
    ),
    GRACEFUL_ROD(
        "Graceful Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/graceful_rod.png")
    ),
    GLITCHED_ROD(
        "Glitched Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/glitched_rod.png")
    ),
    STABLE_ROD(
        "Stable Rod",
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/perk_icon/stable_rod.png")
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
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_items/infinibag/fishing_item/anglr_lure_wise.png"),
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
    )
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
    "Glitched Rod" to Augment.GLITCHED_ROD,
    "Stable Rod" to Augment.STABLE_ROD,

    "Elusive Lure" to Augment.ELUSUVE_LURE,
    "Wayfinder Lure" to Augment.WAYFINDER_LURE,
    "Pearl Lure" to Augment.PEARL_LURE,
    "Treasure Lure" to Augment.TREASURE_LURE,
    "Spirit Lure" to Augment.SPIRIT_LURE,

    "Elusive Ultralure" to Augment.ELUSUVE_ULTRALURE,
    "Wayfinder Ultralure" to Augment.WAYFINDER_ULTRALURE,
    "Pearl Ultralure" to Augment.PEARL_ULTRALURE,
    "Treasure Ultralure" to Augment.TREASURE_ULTRALURE,
    "Spirit Ultralure" to Augment.SPIRIT_ULTRALURE,

    "Elusive Soda" to Augment.ELUSIVE_SODA,
    "Rarity Rod" to Augment.RARITY_ROD,
    "Pure Beacon" to Augment.PURE_BEACON,
    "Lure Battery" to Augment.LURE_BATTERY,
    "Stock Replenisher" to Augment.STOCK_REPLENISHER,
    "Auto Rod" to Augment.AUTO_ROD,

    "Strong Amulet" to Augment.STRONG_AMULET,
    "Wise Amulet" to Augment.WISE_AMULET,
    "Glimmering Amulet" to Augment.GLIMMERING_AMULET,
    "Greedy Amulet" to Augment.GREEDY_AMULET,
    "Lucky Amulet" to Augment.LUCKY_AMULET,
)