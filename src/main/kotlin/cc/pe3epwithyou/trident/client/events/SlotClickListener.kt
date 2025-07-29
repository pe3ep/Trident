package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.SupplyWidgetTimer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot

object SlotClickListener {
    fun handleClick(slot: Slot, clickType: ClickType, isLeftClick: Boolean) {
        val client = Minecraft.getInstance()
        if (client.screen !is ContainerScreen) return
        val screen = client.screen as ContainerScreen

        if (Config.Fishing.suppliesModule) {
            if ("FISHING SUPPLIES" !in screen.title.string) return
            val item = slot.item
            if (clickType == ClickType.QUICK_MOVE && isLeftClick && "Unstable Overclock" in item.displayName.string) {
                startUnstableOverclock()
            }
            if (clickType == ClickType.PICKUP && isLeftClick && "Supreme Overclock" in item.displayName.string) {
                startSupremeOverclock()
            }
        }
    }

    private fun startUnstableOverclock() {
        val overclock = TridentClient.playerState.supplies.overclocks.unstable
        if (overclock.isActive || overclock.isCooldown) return
        SupplyWidgetTimer.INSTANCE.startUnstableOverclock()
    }

    private fun startSupremeOverclock() {
        val overclock = TridentClient.playerState.supplies.overclocks.supreme
        if (overclock.isActive || overclock.isCooldown) return
        SupplyWidgetTimer.INSTANCE.startSupremeOverclock()
    }
}