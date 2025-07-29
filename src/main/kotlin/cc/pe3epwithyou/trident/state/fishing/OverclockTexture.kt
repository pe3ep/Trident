package cc.pe3epwithyou.trident.state.fishing

import net.minecraft.resources.ResourceLocation

enum class OverclockTexture(
    val texturePath: ResourceLocation,
    val textureWidth: Int = 16,
    val textureHeight: Int = textureWidth
) {
//    Hook Overclock
    WISE_HOOK(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/wise_hook.png")
    ),
    STRONG_HOOK(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/strong_hook.png")
    ),
    GREEDY_HOOK(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/greedy_hook.png")
    ),
    GLIMMERING_HOOK(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/glimmering_hook.png")
    ),
    LUCKY_HOOK(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/lucky_hook.png")
    ),

//    Magnet Overclock
    FISH_MAGNET(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/fish_magnet.png")
    ),
    TREASURE_MAGNET(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/treasure_magnet.png")
    ),
    SPIRIT_MAGNET(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/spirit_magnet.png")
    ),
    PEARL_MAGNET(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/pearl_magnet.png")
    ),
    XP_MAGNET(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/xp_magnet.png")
    ),

//    Rod Overclock
    GLITCHED_ROD(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/glitched_rod.png")
    ),
    GRACEFUL_ROD(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/graceful_rod.png")
    ),
    STABLE_ROD(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/stable_rod.png")
    ),
    BOOSTED_ROD(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/boosted_rod.png")
    ),
    SPEEDY_ROD(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/speedy_rod.png")
    ),

//    Timed overclocks
    GLIMMERING_UNSTABLE(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/glimmering_unstable.png"),
        16,
        176
    ),
    GREEDY_UNSTABLE(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/greedy_unstable.png"),
        16,
        176
    ),
    LUCKY_UNSTABLE(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/lucky_unstable.png"),
        16,
        176
    ),
    STRONG_UNSTABLE(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/strong_unstable.png"),
        16,
        176
    ),
    WISE_UNSTABLE(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/wise_unstable.png"),
        16,
        176
    ),

    SUPREME(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/supreme.png"),
        16,
        80
    ),

//    Misc
    ACTIVATED(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/activated.png"),
        16,
        48
    ),
    COOLDOWN(
        ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/fishing/overclock/cooldown.png"),
    ),
}