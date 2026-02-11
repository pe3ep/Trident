package cc.pe3epwithyou.trident.state

import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.ItemStack

enum class Rarity(
    val color: Int
) {
    COMMON(
        0xFFFFFF
    ),
    UNCOMMON(
        0x1EFF00
    ),
    RARE(
        0x0070DD
    ),
    EPIC(
        0xA335EE
    ),
    LEGENDARY(
        0xFF8000
    ),
    MYTHIC(
        0xF94242
    );

    companion object {
        fun getFromItem(item: ItemStack): Rarity? {
            item.hoverName.toFlatList().firstOrNull()?.style?.color?.let { color ->
                Rarity.entries.find { TextColor.fromRgb(it.color) == color }?.let { return it }
            }
            return null
        }

        fun fromString(string: String): Rarity = entries.find { it.name.equals(string, ignoreCase = true) } ?: COMMON
    }
}