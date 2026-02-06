package cc.pe3epwithyou.trident.feature.doll

import cc.pe3epwithyou.trident.feature.doll.slots.AccessorySlot
import cc.pe3epwithyou.trident.feature.doll.slots.BackSlot
import cc.pe3epwithyou.trident.feature.doll.slots.CosmeticSlot
import cc.pe3epwithyou.trident.feature.doll.slots.HatSlot
import cc.pe3epwithyou.trident.feature.doll.slots.SkinSlot
import net.minecraft.world.item.ItemStack

@Suppress("unused")
enum class CosmeticType(
    val pathPrefixes: List<String>,
    val slot: (ItemStack) -> CosmeticSlot
) {
    HAT(listOf("island_cosmetics/general/hat", "island_cosmetics/faction/hat"), ::HatSlot),
    ACCESSORY(listOf("island_cosmetics/general/accessory", "island_cosmetics/faction/accessory"), ::AccessorySlot),
    CLOAK(listOf("island_cosmetics/general/back"), ::BackSlot),
    SKIN(listOf("island_lobby/fishing/rods", "island_cosmetics/weapon_skins"), ::SkinSlot),
}