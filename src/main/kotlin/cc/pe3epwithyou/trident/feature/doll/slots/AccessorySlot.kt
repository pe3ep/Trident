package cc.pe3epwithyou.trident.feature.doll.slots

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

class AccessorySlot : CosmeticSlot {
    var storedItem: ItemStack? = null

    override fun push(entity: LivingEntity, item: ItemStack) {
        storedItem = entity.getItemBySlot(EquipmentSlot.OFFHAND)
        if (item == ItemStack.EMPTY) return
        entity.setItemSlot(EquipmentSlot.OFFHAND, item)
    }

    override fun pop(entity: LivingEntity) {
        val item = storedItem ?: return
        entity.setItemSlot(EquipmentSlot.OFFHAND, item)
        storedItem = null
    }
}