package cc.pe3epwithyou.trident.feature.doll.slots

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

interface CosmeticSlot {
    fun push(entity: LivingEntity, item: ItemStack)

    fun pop(entity: LivingEntity)
}