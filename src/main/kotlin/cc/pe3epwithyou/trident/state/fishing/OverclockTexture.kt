package cc.pe3epwithyou.trident.state.fishing

import cc.pe3epwithyou.trident.utils.Resources
import net.minecraft.resources.ResourceLocation

enum class OverclockTexture(
    val texturePath: ResourceLocation,
    val textureWidth: Int = 16,
    val textureHeight: Int = textureWidth
) {
//    Hook Overclock
    WISE_HOOK(
        Resources.mcc("island_interface/fishing/overclock/wise_hook")
    ),
    STRONG_HOOK(
        Resources.mcc("island_interface/fishing/overclock/strong_hook")
    ),
    GREEDY_HOOK(
        Resources.mcc("island_interface/fishing/overclock/greedy_hook")
    ),
    GLIMMERING_HOOK(
        Resources.mcc("island_interface/fishing/overclock/glimmering_hook")
    ),
    LUCKY_HOOK(
        Resources.mcc("island_interface/fishing/overclock/lucky_hook")
    ),

//    Magnet Overclock
    FISH_MAGNET(
        Resources.mcc("island_interface/fishing/overclock/fish_magnet")
    ),
    TREASURE_MAGNET(
        Resources.mcc("island_interface/fishing/overclock/treasure_magnet")
    ),
    SPIRIT_MAGNET(
        Resources.mcc("island_interface/fishing/overclock/spirit_magnet")
    ),
    PEARL_MAGNET(
        Resources.mcc("island_interface/fishing/overclock/pearl_magnet")
    ),
    XP_MAGNET(
        Resources.mcc("island_interface/fishing/overclock/xp_magnet")
    ),

//    Rod Overclock
    GLITCHED_ROD(
        Resources.mcc("island_interface/fishing/overclock/glitched_rod")
    ),
    GRACEFUL_ROD(
        Resources.mcc("island_interface/fishing/overclock/graceful_rod")
    ),
    STABLE_ROD(
        Resources.mcc("island_interface/fishing/overclock/stable_rod")
    ),
    BOOSTED_ROD(
        Resources.mcc("island_interface/fishing/overclock/boosted_rod")
    ),
    SPEEDY_ROD(
        Resources.mcc("island_interface/fishing/overclock/speedy_rod")
    ),

//    Timed overclocks
    GLIMMERING_UNSTABLE(
        Resources.mcc("island_interface/fishing/overclock/glimmering_unstable"),
        16,
        176
    ),
    GREEDY_UNSTABLE(
        Resources.mcc("island_interface/fishing/overclock/greedy_unstable"),
        16,
        176
    ),
    LUCKY_UNSTABLE(
        Resources.mcc("island_interface/fishing/overclock/lucky_unstable"),
        16,
        176
    ),
    STRONG_UNSTABLE(
        Resources.mcc("island_interface/fishing/overclock/strong_unstable"),
        16,
        176
    ),
    WISE_UNSTABLE(
        Resources.mcc("island_interface/fishing/overclock/wise_unstable"),
        16,
        176
    ),

    SUPREME(
        Resources.mcc("island_interface/fishing/overclock/supreme"),
        16,
        80
    ),

//    Misc
    ACTIVATED(
        Resources.mcc("island_interface/fishing/overclock/activated"),
        16,
        48
    ),
    COOLDOWN(
        Resources.mcc("island_interface/fishing/overclock/cooldown"),
    ),
}