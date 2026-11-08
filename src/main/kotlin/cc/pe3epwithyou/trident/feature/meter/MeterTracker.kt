package cc.pe3epwithyou.trident.feature.meter

import cc.pe3epwithyou.trident.events.container.ContainerContext
import cc.pe3epwithyou.trident.events.container.ContainerEvents
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.findInLore
import cc.pe3epwithyou.trident.utils.parseFormattedInt

object MeterTracker {
    fun register() {
        ContainerEvents.onOpen(::handleContainer)
        ContainerEvents.onClose(::handleContainer)
    }

    fun handleContainer(ctx: ContainerContext) = with(ctx) {
        requireTitle("ISLAND REWARDS")

        // Daily meter
        item(13)?.let { item ->
            item.findInLore(Regex("""Daily Claims: (.+)/(.+)"""))?.let { result ->
                val currentClaim = result.groups[1]?.value?.parseFormattedInt() ?: return@let
                DailyMeter.setCurrentClaim(currentClaim)

                val maxClaims = result.groups[2]?.value?.parseFormattedInt() ?: return@let
                DailyMeter.setMaxClaims(maxClaims)
            }

            item.findInLore(Regex("""Progress: (.+)/.+ XP"""))?.let { result ->
                val currentXP = result.groups[1]?.value?.parseFormattedInt() ?: return@let
                DailyMeter.setCurrentXP(currentXP)
            }
        }
    }

    fun handleXP(amount: Int) {
        DailyMeter.handleIncomingXP(amount)
    }
}