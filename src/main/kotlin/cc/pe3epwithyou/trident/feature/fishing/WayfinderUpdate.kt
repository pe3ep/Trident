package cc.pe3epwithyou.trident.feature.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.client.listeners.FishingSpotListener
import cc.pe3epwithyou.trident.mixin.BossHealthOverlayAccessor
import cc.pe3epwithyou.trident.state.ClimateType
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.state.WayfinderStatus
import cc.pe3epwithyou.trident.state.fishing.OverclockTexture
import cc.pe3epwithyou.trident.state.fishing.Perk
import cc.pe3epwithyou.trident.utils.ChatUtils
import net.minecraft.network.chat.Component
import net.minecraft.client.Minecraft

object WayfinderTracker {
    val wayfinderStatuses = mapOf<ClimateType, WayfinderStatus>(
        ClimateType.TEMPERATE to TridentClient.playerState.wayfinderData.temperate,
        ClimateType.TROPICAL to TridentClient.playerState.wayfinderData.tropical,
        ClimateType.BARREN to TridentClient.playerState.wayfinderData.barren
    )

    fun update(message: Component?) {
        if (FishingSpotListener.currentSpot == null) return

        val currentClimateStatus = wayfinderStatuses[MCCIState.fishingState.climate.climateType]
        if (currentClimateStatus == null) return
        if (currentClimateStatus.hasGrotto) return

        val currentSpot = FishingSpotListener.currentSpot!!
        var wayfinderForCatch = 0;

        // Check for wayfinder lures in augments
        for (augment in TridentClient.playerState.supplies.augments) {
            if (augment.augmentName == "Wayfinder Lure") {
                wayfinderForCatch += 10
            }
        }

        // Check for wayfinder in the unstable overclock
        if (TridentClient.playerState.supplies.overclocks.unstable.texture == OverclockTexture.WISE_UNSTABLE && TridentClient.playerState.supplies.overclocks.unstable.state.isActive) {
            wayfinderForCatch += TridentClient.playerState.wayfinderData.overclockData ?: 0
        }

        // Check for wise winds in the climate
        val overlay = Minecraft.getInstance().gui.bossOverlay
        val accessor = overlay as BossHealthOverlayAccessor
        accessor.events.forEach { it -> ChatUtils.debugLog(it.toString()) }

        // Check for wayfinder data on the spot
        if (currentSpot.perks.stream().anyMatch { p -> p.first == Perk.WAYFINDER_DATA }) {
            wayfinderForCatch += 10
        }

        // Add the amount for the wayfinder data perk
        if (TridentClient.playerState.wayfinderData.wayfinderPerkData != null) {
            wayfinderForCatch += TridentClient.playerState.wayfinderData.wayfinderPerkData ?: 0
        } else {
            ChatUtils.sendMessage("Your Wayfinder Data perk is unknown so Wayfinder Data calculations are inaccurate! Please open the perks menu!")
        }

        // Check for boosted rod on the catch
        if (Regex("^\\s+. Triggered: . Boosted Rod").matches(message.toString())) {
            wayfinderForCatch *= 2
        }

        currentClimateStatus.data += wayfinderForCatch
        if (currentClimateStatus.data >= 2000 && !currentClimateStatus.hasGrotto) {
            currentClimateStatus.hasGrotto = true
            currentClimateStatus.grottoStability = 100
        }
    }
}