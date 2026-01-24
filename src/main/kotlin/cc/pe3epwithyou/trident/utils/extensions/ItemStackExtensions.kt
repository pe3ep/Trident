package cc.pe3epwithyou.trident.utils.extensions

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

object ItemStackExtensions {
    fun ItemStack.getLore(): List<Component> {
        val player = Minecraft.getInstance().player ?: return emptyList()

        return this.getTooltipLines(
            Item.TooltipContext.EMPTY,
            player,
            TooltipFlag.Default.NORMAL
        )
    }

    fun ItemStack.safeGetLine(index: Int): Component? = this.getLore().getOrNull(index)

    fun ItemStack.findInLore(predicate: Regex): MatchResult? {
        val c = this.getLore().find { predicate.containsMatchIn(it.string) }
        if (c == null) {
            return null
        }
        return predicate.find(c.string)
    }
}