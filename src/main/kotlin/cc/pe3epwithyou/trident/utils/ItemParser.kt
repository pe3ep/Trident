package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.getAugmentByName
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag

object ItemParser {
    fun getLore(item: ItemStack): List<Component> {
        if (Minecraft.getInstance().player == null) return listOf(Component.empty())
        return item.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.Default.NORMAL)
    }

    fun getActiveOverclock(item: ItemStack): Augment? {
        var beginSearch = false
        getLore(item).forEach { line ->
//            Due to people having different upgrade costs, it's easier to simply go over each line and start
//            searching overclocks once we reach this text
            if ("Overclocked Perk:" in line.string) {
                beginSearch = true
            }
            if (beginSearch && line.string == "") {
                return null
            }
            if (beginSearch && ">" in line.string) {
                val name = line.string.split("> ")[1]
                val active = getAugmentByName(name)
                return active
            }
        }
        return null
    }
}