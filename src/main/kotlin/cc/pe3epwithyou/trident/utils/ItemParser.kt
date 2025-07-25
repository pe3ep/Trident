package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.state.fishing.AUGMENT_NAMES
import cc.pe3epwithyou.trident.state.fishing.Augment
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag

object ItemParser {
    fun getLore(item: ItemStack): List<Component>? {
        if (Minecraft.getInstance().player == null) return null
        return item.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.Default.NORMAL)
    }

    fun getActiveOverclock(item: ItemStack): Augment {
        val options = getLore(item)?.subList(12, 16)
        var active = options?.first()
        for (option in options!!) {
//            ChatUtils.info(option.string)
            if (option.string.contains(">")) {
                active = option
            }
        }
        return AUGMENT_NAMES.getValue(active?.string!!.split("> ")[1])
    }
}