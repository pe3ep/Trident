package cc.pe3epwithyou.trident.widgets.killfeed

enum class KillMethod(
    val icon: Char
) {
    MELEE('m'),
    RANGE('r'),
    POTION('p'),
    VOID('v'),
    DISCONNECT('d'),
    GENERIC('g'),
    EXPLOSION('e'),
}