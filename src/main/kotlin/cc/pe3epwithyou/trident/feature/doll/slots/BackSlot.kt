package cc.pe3epwithyou.trident.feature.doll.slots

import cc.pe3epwithyou.trident.feature.doll.back.Back
import cc.pe3epwithyou.trident.feature.doll.back.BackRenderLayer
import cc.pe3epwithyou.trident.utils.minecraft
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

class BackSlot(override var item: ItemStack?) : CosmeticSlot {
    override fun push(entity: LivingEntity) {
        val player = minecraft().player ?: return
        BackRenderLayer.playerForNextPass = player
        BackRenderLayer.itemForNextPass = item
    }

    override fun pop(entity: LivingEntity) {}

    override fun setRealCurrent(entity: LivingEntity) {
        item = Back.getPlayerBackItem(entity)
    }
}