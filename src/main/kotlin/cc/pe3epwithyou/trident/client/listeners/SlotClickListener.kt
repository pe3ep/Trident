package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.exchange.ExchangeHandler
import cc.pe3epwithyou.trident.feature.exchange.ExchangeLookup
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import cc.pe3epwithyou.trident.utils.minecraft
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object SlotClickListener {
    fun handleClick(slot: Slot, clickType: ClickType, isLeftClick: Boolean, ci: CallbackInfo) {
        val client = minecraft()
        if (client.screen !is ContainerScreen) return
        val screen = client.screen as ContainerScreen

        if (Config.Global.exchangeImprovements && "ISLAND EXCHANGE" in screen.title.string) {
            val item = slot.item
            if ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && "Refresh Listings" in item.hoverName.string) {
                if (item.getLore().lastOrNull()?.string?.contains("Click to Refresh") == true) {
                    ExchangeLookup.clearCache()
                    ExchangeHandler.handleScreen(screen)
                }
            }
        }
    }
}