package cc.pe3epwithyou.trident.state.fishing

import net.minecraft.resources.ResourceLocation

enum class OverclockTexture(
    val texturePath: ResourceLocation,
    val textureWidth: Int = 16,
    val textureHeight: Int = textureWidth
) {
//    Hook Overclock
    WISE_HOOK(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/wise_hook")
    ),
    STRONG_HOOK(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/strong_hook")
    ),
    GREEDY_HOOK(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/greedy_hook")
    ),
    GLIMMERING_HOOK(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/glimmering_hook")
    ),
    LUCKY_HOOK(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/lucky_hook")
    ),

//    Magnet Overclock
    FISH_MAGNET(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/fish_magnet")
    ),
    TREASURE_MAGNET(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/treasure_magnet")
    ),
    SPIRIT_MAGNET(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/spirit_magnet")
    ),
    PEARL_MAGNET(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/pearl_magnet")
    ),
    XP_MAGNET(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/xp_magnet")
    ),

//    Rod Overclock
    GLITCHED_ROD(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/glitched_rod")
    ),
    GRACEFUL_ROD(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/graceful_rod")
    ),
    STABLE_ROD(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/stable_rod")
    ),
    BOOSTED_ROD(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/boosted_rod")
    ),
    SPEEDY_ROD(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/speedy_rod")
    ),

//    Timed overclocks
    GLIMMERING_UNSTABLE(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/glimmering_unstable"),
        16,
        176
    ),
    GREEDY_UNSTABLE(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/greedy_unstable"),
        16,
        176
    ),
    LUCKY_UNSTABLE(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/lucky_unstable"),
        16,
        176
    ),
    STRONG_UNSTABLE(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/strong_unstable"),
        16,
        176
    ),
    WISE_UNSTABLE(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/wise_unstable"),
        16,
        176
    ),

    SUPREME(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/supreme"),
        16,
        80
    ),

//    Misc
    ACTIVATED(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/activated"),
        16,
        48
    ),
    COOLDOWN(
        ResourceLocation.fromNamespaceAndPath("mcc", "island_interface/fishing/overclock/cooldown"),
    ),
}