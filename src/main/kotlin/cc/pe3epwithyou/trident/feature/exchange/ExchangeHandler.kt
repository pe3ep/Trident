package cc.pe3epwithyou.trident.feature.exchange

import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

object ExchangeHandler {
    data class Listing(
        val name: String,
        val amount: Int,
    )

    val priceMap = hashMapOf<Listing, Long>()

    fun handleScreen(screen: Screen) {
//        10-16 until 52
        val chest = screen as ContainerScreen
        chest.menu.slots.forEach { slot ->
            if (!inSlotBoundary(slot)) return@forEach
            val item = slot.item

            val price = getItemPrice(item) ?: return@forEach

            val itemName = item.displayName.string
            val listing = Listing(itemName, slot.item.count)
            val current = priceMap[listing]
            if (current == null || price < current) {
                priceMap[listing] = price
            }
        }
    }

    fun render(graphics: GuiGraphics, slot: Slot) {
        val screen = Minecraft.getInstance().screen ?: return
        if ("ISLAND EXCHANGE" !in screen.title.string) return
        if (!inSlotBoundary(slot)) return

        val itemName = slot.item.displayName.string
        val price = getItemPrice(slot.item) ?: return
        val listing = Listing(itemName, slot.item.count)
        if (priceMap.contains(listing) && priceMap[listing] == price) {
            Texture(
                Resources.trident("textures/interface/exchange/star.png"),
                width = 7,
                height = 6,
            ).blit(
                graphics, x = slot.x + 10, y = slot.y - 1
            )
        }

    }

    private fun inSlotBoundary(slot: Slot): Boolean {
        return !(slot.index !in 10..16 && slot.index !in 19..25 && slot.index !in 28..34 && slot.index !in 37..43 && slot.index !in 46..52)
    }

    private fun getItemPrice(item: ItemStack): Long? {
        val priceLine = item.getLore().reversed().getOrNull(2)?.string ?: return null
        val match = Regex("""Listed Price: .((?:\d+|,)+)""").matchEntire(priceLine) ?: return null
        val price = match.groups[1]?.value?.replace(",", "")?.toLongOrNull() ?: return null
        return price
    }
}