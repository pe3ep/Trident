package cc.pe3epwithyou.trident.feature.doll.back

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.item.ItemStack

object Back {
    fun getPlayerBackItem(player: LivingEntity): ItemStack? {
        val armorStand = (player.passengers.firstOrNull()?.passengers?.firstOrNull() ?: return null) as ArmorStand
        return armorStand.getItemBySlot(EquipmentSlot.HEAD)
    }
}