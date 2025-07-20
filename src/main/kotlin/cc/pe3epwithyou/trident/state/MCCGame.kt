package cc.pe3epwithyou.trident.state

enum class MCCGame(
    val title: String,
    val server: String,
    val subtype: String? = null
) {
    HUB("Hub", "lobby"),
    FISHING("Fishing", "lobby"),
    SKY_BATTLE("Sky Battle", "sky_battle"),
    BATTLE_BOX("Battle Box", "battle_box"),
    PARKOUR_WARRIOR_SURVIVOR("Parkour Warrior Survivor", "parkour_warrior", "survival"),
    PARKOUR_WARRIOR_DOJO("Parkour Warrior Dojo", "parkour_warrior"),
    TGTTOS("TGTTOS", "tgttos"),
    ROCKET_SPLEEF_RUSH("Rocket Spleef Rush", "rocket_spleef"),
    DYNABALL("Dynaball", "dynaball"),
    HITW("Hole in the Wall", "hole_in_the_wall"),
}