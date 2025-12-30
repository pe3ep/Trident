package cc.pe3epwithyou.trident.state.fishing

import cc.pe3epwithyou.trident.state.AugmentContainer
import cc.pe3epwithyou.trident.utils.Resources
import net.minecraft.resources.Identifier
import cc.pe3epwithyou.trident.utils.extensions.StringExt.parseFormattedInt

@Suppress("unused")
enum class Augment(
    val augmentName: String,
    val modelPath: Identifier,
    val uses: Int,
    val useTrigger: AugmentTrigger,
    val textureWidth: Int = 16,
    val textureHeight: Int = textureWidth,
    val texture: OverclockTexture? = null,
    val worksInGrotto: Boolean = true
) {
    // Hook Augments (can be used by the hook overclock)
    STRONG_HOOK(
        "Strong Hook",
        Resources.mcc("island_interface/fishing/perk_icon/strong_hook"),
        100,
        AugmentTrigger.FISH,
        texture = OverclockTexture.STRONG_HOOK
    ),
    WISE_HOOK(
        "Wise Hook",
        Resources.mcc("island_interface/fishing/perk_icon/wise_hook"),
        100,
        AugmentTrigger.FISH,
        texture = OverclockTexture.WISE_HOOK
    ),
    GLIMMERING_HOOK(
        "Glimmering Hook",
        Resources.mcc("island_interface/fishing/perk_icon/glimmering_hook"),
        20,
        AugmentTrigger.PEARL,
        texture = OverclockTexture.GLIMMERING_HOOK
    ),
    GREEDY_HOOK(
        "Greedy Hook",
        Resources.mcc("island_interface/fishing/perk_icon/greedy_hook"),
        5,
        AugmentTrigger.TREASURE,
        texture = OverclockTexture.GREEDY_HOOK
    ),
    LUCKY_HOOK(
        "Lucky Hook",
        Resources.mcc("island_interface/fishing/perk_icon/lucky_hook"),
        10,
        AugmentTrigger.SPIRIT,
        texture = OverclockTexture.LUCKY_HOOK
    ),

    // Magnet Augments (can be used by the magnet overclock)
    XP_MAGNET(
        "XP Magnet",
        Resources.mcc("island_interface/fishing/perk_icon/xp_magnet"),
        100,
        AugmentTrigger.ANYTHING,
        texture = OverclockTexture.XP_MAGNET
    ),
    FISH_MAGNET(
        "Fish Magnet",
        Resources.mcc("island_interface/fishing/perk_icon/fish_magnet"),
        100,
        AugmentTrigger.FISH,
        texture = OverclockTexture.FISH_MAGNET
    ),
    PEARL_MAGNET(
        "Pearl Magnet",
        Resources.mcc("island_interface/fishing/perk_icon/pearl_magnet"),
        20,
        AugmentTrigger.PEARL,
        texture = OverclockTexture.PEARL_MAGNET
    ),
    TREASURE_MAGNET(
        "Treasure Magnet",
        Resources.mcc("island_interface/fishing/perk_icon/treasure_magnet"),
        5,
        AugmentTrigger.TREASURE,
        texture = OverclockTexture.TREASURE_MAGNET
    ),
    SPIRIT_MAGNET(
        "Spirit Magnet",
        Resources.mcc("island_interface/fishing/perk_icon/spirit_magnet"),
        10,
        AugmentTrigger.SPIRIT,
        texture = OverclockTexture.SPIRIT_MAGNET
    ),

    // Rod Augments (can be used by the rod overclock)
    BOOSTED_ROD(
        "Boosted Rod",
        Resources.mcc("island_interface/fishing/perk_icon/boosted_rod"),
        50,
        AugmentTrigger.ANYTHING,
        texture = OverclockTexture.BOOSTED_ROD
    ),
    SPEEDY_ROD(
        "Speedy Rod",
        Resources.mcc("island_interface/fishing/perk_icon/speedy_rod"),
        100,
        AugmentTrigger.ANYTHING,
        texture = OverclockTexture.SPEEDY_ROD
    ),
    GRACEFUL_ROD(
        "Graceful Rod",
        Resources.mcc("island_interface/fishing/perk_icon/graceful_rod"),
        100,
        AugmentTrigger.ANYTHING,
        texture = OverclockTexture.GRACEFUL_ROD
    ),
    GLITCHED_ROD(
        "Glitched Rod",
        Resources.mcc("island_interface/fishing/perk_icon/glitched_rod"),
        100,
        AugmentTrigger.ANYTHING,
        texture = OverclockTexture.GLITCHED_ROD
    ),
    STABLE_ROD(
        "Stable Rod",
        Resources.mcc("island_interface/fishing/perk_icon/stable_rod"),
        50,
        AugmentTrigger.ANYTHING_GROTTO,
        texture = OverclockTexture.STABLE_ROD
    ),

    // Lure Augments (can be used by the unstable overclock)
    ELUSUVE_LURE(
        "Elusive Lure",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_lure_strong"),
        3,
        AugmentTrigger.ELUSIVE
    ),
    WAYFINDER_LURE(
        "Wayfinder Lure",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_lure_wise"),
        100,
        AugmentTrigger.ANYTHING,
        worksInGrotto = false
    ),
    PEARL_LURE(
        "Pearl Lure",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_lure_glimmering"),
        15,
        AugmentTrigger.PEARL,
        worksInGrotto = false
    ),
    TREASURE_LURE(
        "Treasure Lure",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_lure_greedy"),
        2,
        AugmentTrigger.TREASURE,
        worksInGrotto = false
    ),
    SPIRIT_LURE(
        "Spirit Lure",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_lure_lucky"),
        5,
        AugmentTrigger.SPIRIT,
        worksInGrotto = false
    ),

    // Ultralure Augments
    ELUSUVE_ULTRALURE(
        "Elusive Ultralure",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_ultralure_strong"),
        30,
        AugmentTrigger.ELUSIVE,
        16,
        256
    ),
    WAYFINDER_ULTRALURE(
        "Wayfinder Ultralure",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_ultralure_wise"),
        500,
        AugmentTrigger.ANYTHING,
        16,
        256,
        worksInGrotto = false
    ),
    PEARL_ULTRALURE(
        "Pearl Ultralure",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_ultralure_glimmering"),
        150,
        AugmentTrigger.PEARL,
        16,
        256,
        worksInGrotto = false
    ),
    TREASURE_ULTRALURE(
        "Treasure Ultralure",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_ultralure_greedy"),
        20,
        AugmentTrigger.TREASURE,
        16,
        256,
        worksInGrotto = false
    ),
    SPIRIT_ULTRALURE(
        "Spirit Ultralure",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_ultralure_lucky"),
        50,
        AugmentTrigger.SPIRIT,
        16,
        256,
        worksInGrotto = false
    ),

    // Other Augments (cannot be used by ANY overclocks)
    ELUSIVE_SODA(
        "Elusive Soda",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_elusive_pop"),
        100,
        AugmentTrigger.FISH
    ),
    RARITY_ROD(
        "Rarity Rod",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_rarity_rod"),
        100,
        AugmentTrigger.FISH,
        16,
        240
    ),
    PURE_BEACON(
        "Pure Beacon",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_pure_beacon"),
        100,
        AugmentTrigger.SPIRIT,
    ),
    LURE_BATTERY(
        "Lure Battery",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_lure_battery"),
        100,
        AugmentTrigger.ANYTHING,
    ),
    STOCK_REPLENISHER(
        "Stock Replenisher",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_stock_replenisher"),
        1,
        AugmentTrigger.SPOT
    ),
    AUTO_ROD(
        "Auto Rod",
        Resources.mcc("island_items/infinibag/fishing_item/anglr_auto_rod"),
        150,
        AugmentTrigger.ANYTHING
    ),


    // Amulets Augments
    STRONG_AMULET(
        "Strong Amulet",
        Resources.mcc("island_items/infinibag/fishing_item/amulet_strong"),
        10,
        AugmentTrigger.SPIRIT,
        16,
        208
    ),
    WISE_AMULET(
        "Wise Amulet",
        Resources.mcc("island_items/infinibag/fishing_item/amulet_wise"),
        10,
        AugmentTrigger.SPIRIT,
        16,
        208
    ),
    GLIMMERING_AMULET(
        "Glimmering Amulet",
        Resources.mcc("island_items/infinibag/fishing_item/amulet_glimmering"),
        10,
        AugmentTrigger.SPIRIT,
        16,
        208
    ),
    GREEDY_AMULET(
        "Greedy Amulet",
        Resources.mcc("island_items/infinibag/fishing_item/amulet_greedy"),
        10,
        AugmentTrigger.SPIRIT,
        16,
        208
    ),
    LUCKY_AMULET(
        "Lucky Amulet",
        Resources.mcc("island_items/infinibag/fishing_item/amulet_lucky"),
        10,
        AugmentTrigger.SPIRIT,
        16,
        208
    ),

    EMPTY_AUGMENT(
        "Empty Augment",
        Resources.trident("interface/empty_augment"),
        1,
        AugmentTrigger.NONE,
    )

}

fun getAugmentByName(name: String): Augment? {
    Augment.entries.forEach { augment ->
        if (augment.augmentName == name) return augment
    }
    return null
}

fun getAugmentContainer(name: String, lore: List<String>): AugmentContainer? {
    Augment.entries.forEach { augment ->
        if (augment.augmentName == name) {
            val status = when {
                "This item has previously been repaired." in lore -> AugmentStatus.REPAIRED
                lore.find { "This item is out of uses! You can Repair" in it } != null -> AugmentStatus.NEEDS_REPAIRING
                lore.find { "This item is out of uses! You've already" in it } != null -> AugmentStatus.BROKEN
                lore.find { "Paused: This item will not consume uses" in it } != null -> AugmentStatus.PAUSED
                else -> AugmentStatus.NEW
            }
            var durability: Int? = null
            lore.forEach { s ->
                Regex("""Uses Remaining: (.+)/(.+)""").matchEntire(s)?.let {
                    durability = it.groups[1]?.value?.parseFormattedInt()
                    return@forEach
                }
            }
            return AugmentContainer(augment, status, durability ?: augment.uses)
        }
    }
    return null
}

enum class AugmentStatus {
    NEW,
    NEEDS_REPAIRING,
    REPAIRED,
    PAUSED,
    BROKEN
}
