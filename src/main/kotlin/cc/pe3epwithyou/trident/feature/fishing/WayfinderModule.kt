package cc.pe3epwithyou.trident.feature.fishing

import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.mixin.BossHealthOverlayAccessor
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.extensions.StringExt.parseFormattedInt
import net.minecraft.client.Minecraft

object WayfinderModule {
    fun handleBossbarEvent() = DelayedAction.delayTicks(1L) {
        if (MCCIState.game != Game.FISHING) return@delayTicks
        if (!MCCIState.fishingState.isGrotto) return@delayTicks
        val wayfinderStatus = MCCIState.fishingState.climate.getCurrentWayfinderStatus()

        val events = (Minecraft.getInstance().gui.bossOverlay as BossHealthOverlayAccessor).events
        events.forEach { (_, value) ->
            Regex("""STABILITY: (\d+)%""").find(value.name.string)?.let {
                val newStability = it.groups[1]?.value?.parseFormattedInt() ?: return@forEach
                if (wayfinderStatus.grottoStability == newStability) return@forEach
                wayfinderStatus.grottoStability = newStability
                Logger.debugLog("${MCCIState.fishingState.climate.climateType}: Grotto is at $newStability%")
                DialogCollection.refreshDialog("wayfinder")
            }
        }
    }
}