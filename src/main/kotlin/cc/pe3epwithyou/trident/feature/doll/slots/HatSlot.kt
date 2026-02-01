package cc.pe3epwithyou.trident.feature.doll.slots

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

class HatSlot(override var item: ItemStack?) : CosmeticSlot {
    var storedItem: ItemStack? = null

    override fun push(entity: LivingEntity) {
        storedItem = entity.getItemBySlot(EquipmentSlot.HEAD)
        val slotItem = item
        if (slotItem == ItemStack.EMPTY || slotItem == null) return
        entity.equipment.set(EquipmentSlot.HEAD, slotItem)
    }

    override fun pop(entity: LivingEntity) {
        val item = storedItem ?: return
        entity.equipment.set(EquipmentSlot.HEAD, item)
        storedItem = null
    }
}