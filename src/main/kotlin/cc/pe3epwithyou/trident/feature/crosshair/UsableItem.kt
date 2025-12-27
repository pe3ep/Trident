package cc.pe3epwithyou.trident.feature.crosshair

import cc.pe3epwithyou.trident.utils.Resources
import net.minecraft.resources.ResourceLocation

@Suppress("unused")
enum class UsableItem(
    val sprite: ResourceLocation
) {
    GLOWING_BALL(Resources.mcc("island_items/battle_box/ball_of_glowing")),
    LEVITATION_BALL(Resources.mcc("island_items/battle_box/ball_of_levitation")),
    CLEANSING_BALL(Resources.mcc("island_items/battle_box/ball_of_milk")),
    REGENERATION_BALL(Resources.mcc("island_items/battle_box/ball_of_regeneration")),
    GENERIC_BALL(Resources.mcc("island_items/battle_box/ball")),

    DETECTOR_BOTTLE(Resources.mcc("island_items/battle_box/detector_bottle_item")),
    ESSENCE(Resources.mcc("island_items/battle_box/essence")),
    SEEK_ORB(Resources.mcc("island_items/battle_box/bubble")),

    LONG_TIMED_BALL(Resources.mcc("island_items/battle_box/long_timed_ball")),
    HARMING_LONG_TIMED_BALL(Resources.mcc("island_items/battle_box/long_timed_ball_of_harming")),
    LEVITATION_LONG_TIMED_BALL(Resources.mcc("island_items/battle_box/long_timed_ball_of_levitation")),

    SPARK(Resources.mcc("island_items/battle_box/spark")),
    ESCAPING_SPARK(Resources.mcc("island_items/battle_box/spark_of_escaing")),
    INVISIBILITY_SPARK(Resources.mcc("island_items/battle_box/spark_of_invisibility")),
    LEVITATION_SPARK(Resources.mcc("island_items/battle_box/spark_of_levitation")),
    REGENERATION_SPARK(Resources.mcc("island_items/battle_box/spark_of_regeneration")),
    SPEED_SPARK(Resources.mcc("island_items/battle_box/spark_of_speed")),

    SHORT_TIMED_ORB(Resources.mcc("island_items/battle_box/short_timed_orb")),
    BLINDNESS_SHORT_TIMED_ORB(Resources.mcc("island_items/battle_box/short_timed_ball_of_blindness")),
    HARMING_SHORT_TIMED_ORB(Resources.mcc("island_items/battle_box/short_timed_ball_of_harming")),
    POISON_SHORT_TIMED_ORB(Resources.mcc("island_items/battle_box/short_timed_ball_of_poison")),
    SLOWNESS_SHORT_TIMED_ORB(Resources.mcc("island_items/battle_box/short_timed_ball_of_slowness")),

    SPLASH_POTION(Resources.minecraft("splash_potion")),
    POTION(Resources.minecraft("potion")),
    COBWEB(Resources.minecraft("cobweb")),
    ARROW(Resources.minecraft("arrow")),
}

@Suppress("unused")
enum class UsableItemEffect(
    val color: Int
) {
    BRIGHT_HARMING(0xFFFF33),
    HOVERING(0xC3D0D4),
    LEVITATION(0x54D6FF),
    CLEANSING(0xDEF5FF),
    ENDER(0x1C432F),
    VOID(0x874FFF),
    SPEED(0x54D6FF),
    REJUVENATION(0xFFBFDE),
    REGENERATION(0xFF6AF0),
    BLINDNESS(0x2F2D34),
    JUMP_BOOST(0x64FF96),
    ABSORPTION(0xDEC450),
    PROTECTION(0x2D8BCF),
    POISON(0xA5CB37),
}