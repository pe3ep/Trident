package cc.pe3epwithyou.trident.state

enum class Game(
    val title: String,
    val gameID: String,
    val types: List<String>? = null,
    val primaryColor: Int,
    val icon: String
) {
    HUB(
        "Hub",
        "lobby",
        icon = "_fonts/icon/game_icons/island_main_small.png",
        primaryColor = 0x038AFF
    ),
    FISHING("Fishing", "fishing", icon = HUB.icon, primaryColor = 0xFF8F4E),
    SKY_BATTLE(
        "Sky Battle",
        "sky_battle",
        types = listOf("sky_battle", "team", "quad"),
        primaryColor = 0xEE2700,
        icon = "_fonts/icon/game_icons/game_sky_battle_small.png"
    ),
    BATTLE_BOX(
        "Battle Box",
        "battle_box",
        types = listOf("battle_box", "team"),
        primaryColor = 0x88B932,
        icon = "_fonts/icon/game_icons/game_battle_box_small.png"
    ),
    BATTLE_BOX_ARENA(
        "Battle Box Arena",
        "battle_box",
        types = listOf("battle_box", "team", "arena"),
        primaryColor = 0xbf0023,
        icon = "_fonts/icon/game_icons/game_battle_box_arena_small.png"
    ),
    PARKOUR_WARRIOR_SURVIVOR(
        "Parkour Warrior Survivor",
        "parkour_warrior",
        types = listOf("parkour_warrior", "solo", "survival"),
        primaryColor = 0x6ABD19,
        icon = "_fonts/icon/game_icons/game_parkour_warrior_small.png"
    ),
    PARKOUR_WARRIOR_DOJO(
        "Parkour Warrior Dojo",
        "dojo",
        primaryColor = 0xEC9B33,
        icon = "_fonts/icon/game_icons/game_parkour_warrior_solo_small.png"
    ),
    TGTTOS(
        "TGTTOS",
        "tgttos",
        types = listOf("tgttos", "solo"),
        primaryColor = 0xC11D1D,
        icon = "_fonts/icon/game_icons/game_tgttos_small.png"
    ),
    ROCKET_SPLEEF_RUSH(
        "Rocket Spleef Rush",
        "rocket_spleef",
        types = listOf("rocket_spleef", "solo"),
        primaryColor = 0xC1CCFF,
        icon = "_fonts/icon/game_icons/game_rocket_spleef_small.png"
    ),
    DYNABALL(
        "Dynaball",
        "dynaball",
        types = listOf("dynaball", "team"),
        primaryColor = 0x4A42A0,
        icon = "_fonts/icon/game_icons/game_dynaball_small.png"
    ),
    HITW(
        "HITW",
        "hole_in_the_wall",
        types = listOf("hole_in_the_wall", "solo"),
        primaryColor = 0x42D940,
        icon = "_fonts/icon/game_icons/game_hole_in_the_wall_small.png"
    ),
}