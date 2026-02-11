package cc.pe3epwithyou.trident.feature.fishing.listeners

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.events.container.ContainerContext
import cc.pe3epwithyou.trident.events.container.ContainerEvents
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.state.WayfinderStatus
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.findInLore
import cc.pe3epwithyou.trident.utils.parseFormattedInt
import net.minecraft.world.item.ItemStack

object WayfinderListeners {
    fun register() {
        ContainerEvents.onOpen(::findData)
        ContainerEvents.onClose(::findData)
    }

    fun findData(ctx: ContainerContext) = with(ctx) {
        titleHas("FISHING ISLANDS")
        val wayfinder = Trident.playerState.wayfinderData

        item(24)?.let { processWayfinder(wayfinder.temperate, it) }
        item(33)?.let { processWayfinder(wayfinder.tropical, it) }
        item(42)?.let { processWayfinder(wayfinder.barren, it) }

        wayfinder.needsUpdating = false
        DialogCollection.refreshDialog("wayfinder")
    }

    private fun processWayfinder(status: WayfinderStatus, item: ItemStack) {
        item.findInLore(Regex("""Locked!"""))?.let {
            status.unlocked = false
            return
        }

        item.findInLore(Regex("""Wayfinder Data: (\d{1,3}(?:,\d{3})*)/(\d{1,3}(?:,\d{3})*)."""))?.let {
            val parsedData = it.groups[1]?.value?.parseFormattedInt() ?: return@let
            status.apply {
                unlocked = true
                data = parsedData
                hasGrotto = parsedData >= 2000
                return
            }
        }

        item.findInLore(Regex("""Remaining Stability: (\d+)%"""))?.let {
            status.apply {
                unlocked = true
                hasGrotto = true
                grottoStability = it.groups[1]?.value?.parseFormattedInt() ?: return@let
            }
        }

        Logger.debugLog("Island: ${item.displayName.string}, status: $status")
    }
}