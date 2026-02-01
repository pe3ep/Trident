package cc.pe3epwithyou.trident.feature.doll.slots

import cc.pe3epwithyou.trident.feature.doll.back.BackRenderLayer
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

class BackSlot(override var item: ItemStack?) : CosmeticSlot {
    override fun push(entity: LivingEntity) {
        val player = Minecraft.getInstance().player ?: return
        BackRenderLayer.playerForNextPass = player
        BackRenderLayer.itemForNextPass = item
    }

    override fun pop(entity: LivingEntity) {}
}