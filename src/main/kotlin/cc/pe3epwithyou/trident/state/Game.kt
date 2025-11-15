package cc.pe3epwithyou.trident.state

enum class Game(
    val title: String,
    val server: String,
    val subtype: String? = null,
    val primaryColor: Int,
    val icon: String
) {
    HUB("Hub", "lobby", icon = "_fonts/icon/game_icons/island_main_small.png", primaryColor = 0xFF8F4E),
    FISHING("Fishing", "lobby", icon = HUB.icon, primaryColor = 0xFF8F4E),
    SKY_BATTLE(
        "Sky Battle",
        "sky_battle",
        primaryColor = 0xEE2700,
        icon = "_fonts/icon/game_icons/game_sky_battle_small.png"
    ),
    BATTLE_BOX(
        "Battle Box",
        "battle_box",
        primaryColor = 0x88B932,
        icon = "_fonts/icon/game_icons/game_battle_box_small.png"
    ),
    PARKOUR_WARRIOR_SURVIVOR(
        "Parkour Warrior Survivor",
        "parkour_warrior",
        "survival",
        primaryColor = 0x6ABD19,
        icon = "_fonts/icon/game_icons/game_parkour_warrior_small.png"
    ),
    PARKOUR_WARRIOR_DOJO(
        "Parkour Warrior Dojo",
        "parkour_warrior",
        primaryColor = 0xEC9B33,
        icon = "_fonts/icon/game_icons/game_parkour_warrior_solo_small.png"
    ),
    TGTTOS("TGTTOS", "tgttos", primaryColor = 0xC11D1D, icon = "_fonts/icon/game_icons/game_tgttos_small.png"),
    ROCKET_SPLEEF_RUSH(
        "Rocket Spleef Rush",
        "rocket_spleef",
        primaryColor = 0xC1CCFF,
        icon = "_fonts/icon/game_icons/game_rocket_spleef_small.png"
    ),
    DYNABALL("Dynaball", "dynaball", primaryColor = 0x4A42A0, icon = "_fonts/icon/game_icons/game_dynaball_small.png"),
    HITW(
        "HITW",
        "hole_in_the_wall",
        primaryColor = 0x42D940,
        icon = "_fonts/icon/game_icons/game_hole_in_the_wall_small.png"
    ),
}