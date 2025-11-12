package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.fishing.DepletedDisplay
import cc.pe3epwithyou.trident.feature.fishing.WayfinderTracker
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.mixin.BossHealthOverlayAccessor
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.extensions.WindowExtensions.focusWindowIfInactive
import cc.pe3epwithyou.trident.utils.extensions.WindowExtensions.requestAttentionIfInactive
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import java.util.*

object ChatEventListener {
    private var isSupplyPreserve = false
    private var triggerBait = true
    private var catchFinished = true

    private fun checkJunk(component: Component): Boolean {
        val message = component.string
        return listOf(
            "Rusted Can",
            "Tangled Kelp",
            "Lost Shoe",
            "Royal Residue",
            "Forgotten Crown"
        ).any { message.contains(it) }
    }

    // Regex matchers for fishing messages taken from the amazing Jamboree mod <3
    // https://github.com/JamesMCo/jamboree
    private fun Component.isCaughtMessage() = Regex("^\\(.\\) You caught: \\[.+].*").matches(this.string)
    private fun Component.isIconMessage() = Regex("^\\s*. (Triggered|Special): .+").matches(this.string)
    private fun Component.isXPMessage() = Regex("^\\s*. You earned: .+").matches(this.string)
    private fun Component.isReceivedItem() = Regex("^\\(.\\) You receive: .+").matches(this.string)
    private fun Component.isDepletedSpot() =
        Regex("^\\[.] This spot is Depleted, so you can no longer fish here\\.").matches(this.string)

    private fun Component.isGrottoUnlockedMessage() =
        Regex("^\\[.] Your Grotto has become unstable, teleporting you back to safety\\.\\.\\.").matches(this.string)

    private fun Component.isOutOfGrotto() =
        Regex("^\\[.] Your Grotto has become unstable, teleporting you back to safety\\.\\.\\.").matches(this.string)

    private fun Component.isStockReplenished() = Regex("^\\[.] Fishing Spot Stock replenished!").matches(this.string)

    private fun Component.isPKWLeapFinished() = Regex("^\\[.] Leap \\d ended! .+").matches(this.string)

    fun register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register allowMessage@{ message, _ ->
            if (!MCCIState.isOnIsland()) return@allowMessage true
//            PKW messages
            if (message.isPKWLeapFinished() && Config.Games.autoFocus) {
                Minecraft.getInstance().window.focusWindowIfInactive()
            }

//            Fishing messages
            if (message.isDepletedSpot() && Config.Fishing.flashIfDepleted) {
                Minecraft.getInstance().window.requestAttentionIfInactive()
                Minecraft.getInstance().player?.playSound(
                    SoundEvent(
                        ResourceLocation.fromNamespaceAndPath("mcc", "games.fishing.stock_depleted"),
                        Optional.empty()
                    )
                )
                DepletedDisplay.showDepletedTitle()
            }

            if (message.isOutOfGrotto() && Config.Fishing.flashIfDepleted) {
                Minecraft.getInstance().window.requestAttentionIfInactive()

                // Reset grotto state for current climate
                val currentClimateStatus = WayfinderTracker.wayfinderStatuses[MCCIState.fishingState.climate.climateType]
                if (currentClimateStatus != null) {
                    currentClimateStatus.hasGrotto = false
                    currentClimateStatus.data -= 2000
                }
            }


            if (message.isGrottoUnlockedMessage()) {
                // Reset grotto state for current climate
                val currentClimateStatus = WayfinderTracker.wayfinderStatuses[MCCIState.fishingState.climate.climateType]
                if (currentClimateStatus != null) {
                    currentClimateStatus.hasGrotto = true
                }
                DialogCollection.refreshDialog("wayfinder")
            }


            // Check if player received bait and mark supplies as desynced
            if (message.isReceivedItem() && "Bait" in message.string) {
                if (!TridentClient.playerState.supplies.baitDesynced) {
                    TridentClient.playerState.supplies.baitDesynced = true
                    DialogCollection.refreshDialog("supplies")
                }
            }

            if (message.isStockReplenished() && Config.Fishing.flashIfDepleted) {
                DepletedDisplay.DepletedTimer.INSTANCE.stopLoop()
            }

            if (message.isCaughtMessage() && catchFinished) {
                catchFinished = false
                isSupplyPreserve = false
                triggerBait = !checkJunk(message)
            }

            if (message.isIconMessage() && message.string.contains("Supply Preserve", ignoreCase = true)) {
                isSupplyPreserve = true
            }

            if (message.isXPMessage()) {
                if (isSupplyPreserve) {
                    isSupplyPreserve = false
                    catchFinished = true
                    return@allowMessage true
                }

                TridentClient.playerState.supplies.line.uses?.let {
                    if (it != 0) TridentClient.playerState.supplies.line.uses = it - 1
                }

                if (triggerBait) {
                    TridentClient.playerState.supplies.bait.amount?.let {
                        if (it != 0) TridentClient.playerState.supplies.bait.amount = it - 1
                    }
                }

                WayfinderTracker.update(message)

                catchFinished = true
                DialogCollection.refreshDialog("supplies")
                DialogCollection.refreshDialog("wayfinder")
            }

            true
        }
    }
}