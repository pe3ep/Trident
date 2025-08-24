package cc.pe3epwithyou.trident.feature.killfeed

enum class KillMethod(
    val icon: Char
) {
    MELEE('\uE00D'),
    RANGE('\uE00E'),
    POTION('\uE00F'),
    VOID(''),
    DISCONNECT('\uE010'),
    GENERIC(''),
    EXPLOSION('\uE011'),
    LAVA(''),
}