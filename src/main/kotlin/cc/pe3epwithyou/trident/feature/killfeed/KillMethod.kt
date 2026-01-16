package cc.pe3epwithyou.trident.feature.killfeed

import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.offset
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withTridentFont
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

enum class KillMethod {
    GENERIC, MELEE, RANGE, ORB, POTION, MAGIC, VOID, DISCONNECT, EXPLOSION, LAVA, FIRE, REVIVE;

    val icon: MutableComponent
        get() = when (this) {
            GENERIC -> FontCollection.get("_fonts/icon/skull.png")
            MELEE -> FontCollection.get("_fonts/icon/kills.png")
            RANGE -> trident('\uE00E')
            ORB -> trident('\uE00F')
            POTION -> trident('\uE015')
            MAGIC -> FontCollection.get("_fonts/icon/skull.png")
            VOID -> trident('\uE015')
            DISCONNECT -> trident('\uE010')
            EXPLOSION -> trident('\uE011')
            LAVA -> trident('\uE014')
            FIRE -> trident('\uE013')
            REVIVE -> FontCollection.texture("island_items/battle_box/kit/hero")
                .offset(y = 1f)
        }

}

fun trident(c: Char) = Component.literal(c.toString()).withTridentFont()