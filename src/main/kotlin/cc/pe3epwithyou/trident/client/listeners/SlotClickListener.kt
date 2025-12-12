package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.exchange.ExchangeHandler
import cc.pe3epwithyou.trident.feature.exchange.ExchangeLookup
import cc.pe3epwithyou.trident.feature.fishing.OverclockHandlers
import cc.pe3epwithyou.trident.feature.questing.QuestListener
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot

object SlotClickListener {
    fun handleClick(slot: Slot, clickType: ClickType, isLeftClick: Boolean) {
        val client = Minecraft.getInstance()
        if (client.screen !is ContainerScreen) return
        val screen = client.screen as ContainerScreen
        ChatUtils.debugLog("ct: ${clickType.name} isleftclick: $isLeftClick slotname: ${slot.item.hoverName.string}")
        if (Config.Fishing.suppliesModule && "FISHING SUPPLIES" in screen.title.string) {
            val item = slot.item
            if (clickType == ClickType.QUICK_MOVE && isLeftClick && "Unstable Overclock" in item.hoverName.string) {
                if (TridentClient.playerState.supplies.overclocks.unstable.state.isAvailable) startUnstableOverclock()
            }
            if ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && isLeftClick && "Supreme Overclock" in item.hoverName.string) {
                if (TridentClient.playerState.supplies.overclocks.supreme.state.isAvailable) startSupremeOverclock()
            }
        }

        if (Config.Questing.enabled && "ISLAND REWARDS" in screen.title.string) {
            val item = slot.item
            if (clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) {
                if ("Quest" !in item.hoverName.string) return
                QuestListener.isWaitingRefresh = true
            }
        }

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

    private fun startUnstableOverclock() {
        val overclock = TridentClient.playerState.supplies.overclocks.unstable
        if (overclock.state.isActive || overclock.state.isCooldown) return
        OverclockHandlers.startTimedOverclock("Unstable", overclock.state)
    }

    private fun startSupremeOverclock() {
        val overclock = TridentClient.playerState.supplies.overclocks.supreme
        if (overclock.state.isActive || overclock.state.isCooldown) return
        OverclockHandlers.startTimedOverclock("Supreme", overclock.state)
    }
}