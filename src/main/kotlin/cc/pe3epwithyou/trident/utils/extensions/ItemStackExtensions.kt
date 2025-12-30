package cc.pe3epwithyou.trident.utils.extensions

import cc.pe3epwithyou.trident.utils.Logger
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

object ItemStackExtensions {
    fun ItemStack.getLore(): List<Component> {
        val player = Minecraft.getInstance().player ?: return listOf(Component.empty())

        return this.getTooltipLines(
            Item.TooltipContext.EMPTY,
            player,
            TooltipFlag.Default.NORMAL
        )
    }

    fun ItemStack.safeGetLine(index: Int): Component? {
        val c = this.getLore().getOrNull(index)
        if (c == null) {
            Logger.warn("Failed to get line $index on item " + this.hoverName.string)
        }
        return c
    }

    fun ItemStack.findInLore(predicate: Regex): MatchResult? {
        val c = this.getLore().find { component ->
            predicate.containsMatchIn(component.string)
        }
        if (c == null) {
            Logger.warn("Failed to find predicate $predicate in item ${this.hoverName.string}")
            return null
        }
        return predicate.find(c.string)
    }
}