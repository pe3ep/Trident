package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.dialogs.DialogCollection
import cc.pe3epwithyou.trident.state.MCCIslandState
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Component

object FishingEventListener {
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

    fun register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register allowMessage@{ message, _ ->
            if (!MCCIslandState.isOnIsland()) return@allowMessage true

            // Check if player received bait and mark supplies as desynced
            if (message.isReceivedItem() && "Bait" in message.string) {
                if (!TridentClient.playerState.supplies.updateRequired) {
                    TridentClient.playerState.supplies.updateRequired = true
                    DialogCollection.refreshDialog("supplies")
                }
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

                catchFinished = true
                DialogCollection.refreshDialog("supplies")
            }

            true
        }
    }
}