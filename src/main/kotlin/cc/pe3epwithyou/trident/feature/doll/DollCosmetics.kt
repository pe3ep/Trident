package cc.pe3epwithyou.trident.feature.doll

import cc.pe3epwithyou.trident.feature.doll.chroma.Chroma
import cc.pe3epwithyou.trident.feature.doll.slots.*
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

object DollCosmetics {
    var currentCosmetics = mutableMapOf<CosmeticType, Cosmetic>(
        CosmeticType.HAT to Cosmetic(HatSlot(null)),
        CosmeticType.ACCESSORY to Cosmetic(AccessorySlot(null)),
        CosmeticType.CLOAK to Cosmetic(BackSlot(null)),
        CosmeticType.SKIN to Cosmetic(SkinSlot(null)),
    )

    var currentChroma: Chroma? = null

    var lockedSlots = mutableMapOf<CosmeticType, Cosmetic>()

    fun setShownCosmetics() {
        val player = Minecraft.getInstance().player ?: return
        currentCosmetics.forEach { (_, v) -> v.slot.setRealCurrent(player) }
        lockedSlots.forEach { (k, v) -> currentCosmetics[k]?.slot?.item = v.slot.item }
    }

    @JvmStatic
    fun resetCosmetics() {
        lockedSlots.clear()
        setShownCosmetics()
        currentChroma = null
    }

    fun setCosmetic(item: ItemStack) {
        setShownCosmetics()
        val type = findCosmeticType(item) ?: return
        currentCosmetics[type] = Cosmetic(type.slot(item), item.hoverName)
    }

    fun validItem(item: ItemStack): Boolean {
        return findCosmeticType(item) != null
    }

    fun findCosmeticType(item: ItemStack): CosmeticType? {
        val path = item.components.get(DataComponents.ITEM_MODEL)?.path
        CosmeticType.entries.forEach { if (it.pathPrefixes.any { prefix -> path?.startsWith(prefix) == true }) return it }
        return null
    }

    @Suppress("unused")
    enum class CosmeticType(
        val pathPrefixes: List<String>,
        val slot: (ItemStack) -> CosmeticSlot
    ) {
        HAT(listOf("island_cosmetics/general/hat"), ::HatSlot),
        ACCESSORY(listOf("island_cosmetics/general/accessory"), ::AccessorySlot),
        CLOAK(listOf("island_cosmetics/general/back"), ::BackSlot),
        SKIN(listOf("island_lobby/fishing/rods", "island_cosmetics/weapon_skins"), ::SkinSlot),
    }

    fun isWeaponSkin(item: ItemStack) = item.components.get(DataComponents.ITEM_MODEL)?.path?.startsWith("island_cosmetics/weapon_skins") ?: false

    data class Cosmetic(
        val slot: CosmeticSlot,
        val name: Component = Component.empty(),
    )
}