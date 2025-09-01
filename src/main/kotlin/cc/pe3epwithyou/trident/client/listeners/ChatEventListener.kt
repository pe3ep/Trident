package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.fishing.DepletedDisplay
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.extensions.WindowExtensions.focusWindowIfInactive
import cc.pe3epwithyou.trident.utils.extensions.WindowExtensions.requestAttentionIfInactive
import cc.pe3epwithyou.trident.utils.ChatUtils  
import cc.pe3epwithyou.trident.state.fishing.UseCondition
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
    private var lastCaughtMessage: Component? = null
    private var lastSpecial: Boolean = false

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

            if (message.isStockReplenished()) {
                // Handle use condition CAST_INTO_SPOT when stock is replenished
                val augments = TridentClient.playerState.supplies.augments
                val isGrotto = MCCIState.fishingState.isGrotto
                var changed = false
                augments.forEach { a ->
                    if (a.useCondition == UseCondition.CAST_INTO_SPOT) {
                        if (!(a.bannedInGrotto && isGrotto)) {
                            a.usesCurrent?.let { c ->
                                if (c != 0) {
                                    a.usesCurrent = c - 1
                                    changed = true
                                }
                            }
                        }
                    }
                }
                if (changed) DialogCollection.refreshDialog("supplies")
            }

            if (message.isCaughtMessage() && catchFinished) {
                catchFinished = false
                isSupplyPreserve = false
                triggerBait = !checkJunk(message)
                lastCaughtMessage = message
                lastSpecial = false
            }

            if (message.isIconMessage()) {
                if (message.string.contains("Supply Preserve", ignoreCase = true)) {
                    isSupplyPreserve = true
                }
                if (message.string.contains("Special:", ignoreCase = true)) {
                    lastSpecial = true
                }
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

                // Decrease uses for augments independently of bait/junk logic
                val augments = TridentClient.playerState.supplies.augments
                val isGrotto = MCCIState.fishingState.isGrotto
                val caughtMsg = lastCaughtMessage
                val caughtText = caughtMsg?.string ?: ""
                val caughtSpirit = caughtText.contains("Spirit", ignoreCase = true)
                val caughtTreasure = caughtText.contains("Treasure", ignoreCase = true)
                val caughtPearl = caughtText.contains("Pearl", ignoreCase = true)
                val isJunk = if (caughtMsg != null) checkJunk(caughtMsg) else false
                val caughtFish = caughtText.isNotEmpty() && !caughtSpirit && !caughtTreasure && !caughtPearl && !isJunk
                val caughtElusive = lastSpecial
                ChatUtils.debugLog(
                    (
                        """
                        Fishing catch parse:
                        - caughtText: '$caughtText'
                        - specialIcon: $lastSpecial
                        - isGrotto: $isGrotto
                        - isJunk: $isJunk
                        - Spirit: $caughtSpirit
                        - Treasure: $caughtTreasure
                        - Pearl: $caughtPearl
                        - Fish: $caughtFish
                        - ElusiveFish: $caughtElusive
                        """
                        ).trimIndent()
                )
                augments.forEach { a ->
                    if (a.paused) return@forEach
                    val cond = a.useCondition
                    val shouldUse = when (cond) {
                        UseCondition.SPIRIT -> caughtSpirit
                        UseCondition.FISH -> (caughtFish || caughtElusive)
                        UseCondition.PEARL -> caughtPearl
                        UseCondition.ELUSIVE_FISH -> caughtElusive
                        UseCondition.TREASURE -> caughtTreasure
                        UseCondition.IN_GROTTO -> isGrotto
                        UseCondition.ANYTHING -> true
                        UseCondition.CAST_INTO_SPOT -> false // handled in separate listener
                        null -> false
                    }
                    if (shouldUse && !(a.bannedInGrotto && isGrotto)) {
                        a.usesCurrent?.let { c -> if (c != 0) a.usesCurrent = c - 1 }
                    }
                }

                catchFinished = true
                lastCaughtMessage = null
                lastSpecial = false
                DialogCollection.refreshDialog("supplies")
            }

            true
        }
    }
}