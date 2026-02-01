package cc.pe3epwithyou.trident.feature.doll.slots

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

interface CosmeticSlot {
    var item: ItemStack?

    fun push(entity: LivingEntity)

    fun pop(entity: LivingEntity)

    fun setRealCurrent(entity: LivingEntity)
}