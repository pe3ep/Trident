package cc.pe3epwithyou.trident.feature.doll.slots

import cc.pe3epwithyou.trident.mixin.accessors.LivingEntityAccessor
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

class AccessorySlot(override var item: ItemStack?) : CosmeticSlot {
    var storedItem: ItemStack? = null

    override fun push(entity: LivingEntity) {
        storedItem = entity.getItemBySlot(EquipmentSlot.OFFHAND)
        val slotItem = item
        if (slotItem == ItemStack.EMPTY || slotItem == null) return
        (entity as LivingEntityAccessor).equipment.set(EquipmentSlot.OFFHAND, slotItem)
    }

    override fun pop(entity: LivingEntity) {
        val item = storedItem ?: return
        (entity as LivingEntityAccessor).equipment.set(EquipmentSlot.OFFHAND, item)
        storedItem = null
    }

    override fun setRealCurrent(entity: LivingEntity) {
        item = (entity as LivingEntityAccessor).equipment.get(EquipmentSlot.OFFHAND)
    }
}