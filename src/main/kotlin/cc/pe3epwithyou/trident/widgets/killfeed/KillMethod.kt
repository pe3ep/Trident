package cc.pe3epwithyou.trident.widgets.killfeed

enum class KillMethod(
    val icon: Char
) {
    MELEE('\uE00D'),
    RANGE('\uE00E'),
    POTION('\uE00F'),
    VOID('\uE00D'),
    DISCONNECT('\uE010'),
    GENERIC('\uE00D'),
    EXPLOSION('\uE011'),
    LAVA('\uE00D'),
}