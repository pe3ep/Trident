package cc.pe3epwithyou.trident.utils.extensions

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

object ItemStackExtensions {
    fun ItemStack.getLore(): List<Component> {
        if (Minecraft.getInstance().player == null) return listOf(Component.empty())
        return this.getTooltipLines(
            Item.TooltipContext.EMPTY,
            Minecraft.getInstance().player,
            TooltipFlag.Default.NORMAL
        )
    }
}