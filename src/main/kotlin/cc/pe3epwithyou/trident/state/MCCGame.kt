package cc.pe3epwithyou.trident.state

enum class MCCGame(
    val title: String,
    val server: String,
    val subtype: String? = null,
    val primaryColor: Int,
    val icon: Char
) {
    HUB("Hub", "lobby", icon = '', primaryColor = 0xFF8F4E),
    FISHING("Fishing", "lobby", icon = '', primaryColor = 0xFF8F4E),
    SKY_BATTLE("Sky Battle", "sky_battle", primaryColor = 0xEE2700, icon = ''),
    BATTLE_BOX("Battle Box", "battle_box", primaryColor = 0x88B932, icon = ''),
    PARKOUR_WARRIOR_SURVIVOR(
        "Parkour Warrior Survivor",
        "parkour_warrior",
        "survival",
        primaryColor = 0x6ABD19,
        icon = ''
    ),
    PARKOUR_WARRIOR_DOJO(
        "Parkour Warrior Dojo",
        "parkour_warrior",
        primaryColor = 0xEC9B33,
        icon = ''
    ),
    TGTTOS("TGTTOS", "tgttos", primaryColor = 0xC11D1D, icon = ''),
    ROCKET_SPLEEF_RUSH(
        "Rocket Spleef Rush",
        "rocket_spleef",
        primaryColor = 0xC1CCFF,
        icon = ''
    ),
    DYNABALL("Dynaball", "dynaball", primaryColor = 0x4A42A0, icon = ''),
    HITW("HITW", "hole_in_the_wall", primaryColor = 0x42D940, icon = ''),
}