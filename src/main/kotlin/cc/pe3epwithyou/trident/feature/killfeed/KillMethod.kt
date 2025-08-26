package cc.pe3epwithyou.trident.feature.killfeed

enum class KillMethod(
    val icon: Char
) {
    GENERIC(''),
    MELEE('\uE00D'),
    RANGE('\uE00E'),
    ORB('\uE00F'),
    POTION('\uE015'),
    MAGIC(POTION.icon),
    VOID(GENERIC.icon),
    DISCONNECT('\uE010'),
    EXPLOSION('\uE011'),
    LAVA('\uE014'),
    FIRE('\uE013')
}