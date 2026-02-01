package cc.pe3epwithyou.trident.feature.doll.slots

import cc.pe3epwithyou.trident.feature.doll.DollCosmetics
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomModelData

class SkinSlot(override var item: ItemStack?) : CosmeticSlot {
    var storedItem: ItemStack? = null

    override fun push(entity: LivingEntity) {
        storedItem = entity.getItemBySlot(EquipmentSlot.MAINHAND)
        val slotItem = item?.copy()
        if (slotItem == ItemStack.EMPTY || slotItem == null) return
        val savedModelData = slotItem.get(DataComponents.CUSTOM_MODEL_DATA)
        if (DollCosmetics.isWeaponSkin(slotItem)) {
            DollCosmetics.currentChroma?.let {
                slotItem.set(DataComponents.CUSTOM_MODEL_DATA, CustomModelData(
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    it.colors,
                ))
            } ?: run {
                slotItem.set(DataComponents.CUSTOM_MODEL_DATA, savedModelData)
            }
        }
        entity.equipment.set(EquipmentSlot.MAINHAND, slotItem)
    }

    override fun pop(entity: LivingEntity) {
        entity.equipment.set(EquipmentSlot.MAINHAND, ItemStack.EMPTY)
        storedItem = null
    }

    override fun setRealCurrent(entity: LivingEntity) {
        item = entity.equipment.get(EquipmentSlot.MAINHAND)
    }
}