package cc.pe3epwithyou.trident.feature.doll.slots

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

class SkinSlot(override var item: ItemStack?) : CosmeticSlot {
    var storedItem: ItemStack? = null

    override fun push(entity: LivingEntity) {
        storedItem = entity.getItemBySlot(EquipmentSlot.MAINHAND)
        val slotItem = item
        if (slotItem == ItemStack.EMPTY || slotItem == null) return
        entity.equipment.set(EquipmentSlot.MAINHAND, slotItem)
    }

    override fun pop(entity: LivingEntity) {
        entity.equipment.set(EquipmentSlot.MAINHAND, ItemStack.EMPTY)
        storedItem = null
    }
}