package cc.pe3epwithyou.trident.feature.doll

import cc.pe3epwithyou.trident.feature.doll.back.Back
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
        CosmeticType.WEAPON_SKIN to Cosmetic(SkinSlot(null)),
        CosmeticType.ROD to Cosmetic(SkinSlot(null)),
    )

    var lockedSlots = mutableMapOf<CosmeticType, Cosmetic>()

    fun setShownCosmetics() {
        val player = Minecraft.getInstance().player ?: return
        currentCosmetics.filter { (k, _) -> k != CosmeticType.CLOAK }
            .forEach { (_, v) -> v.slot.item = ItemStack.EMPTY }
        currentCosmetics[CosmeticType.CLOAK]?.slot?.item = Back.getPlayerBackItem(player)
        lockedSlots.forEach { (k, v) -> currentCosmetics[k]?.slot?.item = v.slot.item }
    }

    @JvmStatic
    fun resetCosmetics() {
        lockedSlots.clear()
        setShownCosmetics()
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
        CosmeticType.entries.forEach { if (path?.startsWith(it.pathPrefix) == true) return it }
        return null
    }

    @Suppress("unused")
    enum class CosmeticType(
        val pathPrefix: String,
        val slot: (ItemStack) -> CosmeticSlot
    ) {
        HAT("island_cosmetics/general/hat", ::HatSlot),
        ACCESSORY("island_cosmetics/general/accessory", ::AccessorySlot),
        CLOAK("island_cosmetics/general/back", ::BackSlot),
        ROD("island_lobby/fishing/rods", ::SkinSlot),
        WEAPON_SKIN("island_cosmetics/weapon_skins", ::SkinSlot)
    }

    data class Cosmetic(
        val slot: CosmeticSlot,
        val name: Component = Component.empty(),
    )
}