package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.dialogs.SuppliesDialog
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.utils.ChatUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Component

object FishingEventListener {
    private var isSupplyPreserve: Boolean = false
    private var triggerBait: Boolean = true
    private var catchFinished: Boolean = true
    private fun checkJunk(component: Component): Boolean {
        val m = component.string
        when {
            m.contains("Rusted Can") -> return true
            m.contains("Tangled Kelp") -> return true
            m.contains("Lost Shoe") -> return true
            m.contains("Royal Residue") -> return true
            m.contains("Forgotten Crown") -> return true
        }
        return false
    }

//    Regex matchers for fishing messages taken from the amazing Jamboree mod <3
//    https://github.com/JamesMCo/jamboree
    private fun Component.isCaughtMessage(): Boolean = Regex("^\\(.\\) You caught: \\[.+].*").matches(this.string)
    private fun Component.isIconMessage(): Boolean   = Regex("^\\s*. (Triggered|Special): .+").matches(this.string)
    private fun Component.isXPMessage(): Boolean     = Regex("^\\s*. You earned: .+").matches(this.string)
    private fun Component.isReceivedItem(): Boolean  = Regex("^\\(.\\) You receive: .+").matches(this.string)



    fun register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register allowMessage@{ message, _ ->
            if (!MCCIslandState.isOnIsland()) return@allowMessage true

//            Check if player received bait and mark supplies as desynced
            if (message.isReceivedItem() && message.string.contains("Bait")) {
                if (!TridentClient.playerState.supplies.updateRequired) {
                    TridentClient.playerState.supplies.updateRequired = true
                    (TridentClient.openedDialogs["supplies"] as SuppliesDialog?)?.refresh()
                }
            }

            if (message.isCaughtMessage() && catchFinished) {
                catchFinished = false
                isSupplyPreserve = false
                triggerBait = !checkJunk(message)
            }
            if (message.isIconMessage() && message.string.contains("Supply Preserve", true)) {
                isSupplyPreserve = true
            }
            if (message.isXPMessage()) {
                if (isSupplyPreserve) {
                    isSupplyPreserve = false
                    catchFinished = true
                    return@allowMessage true
                }
                if (TridentClient.playerState.supplies.line.uses != null && TridentClient.playerState.supplies.line.uses != 0) {
                    TridentClient.playerState.supplies.line.uses = TridentClient.playerState.supplies.line.uses!! - 1
                }
                if (triggerBait && TridentClient.playerState.supplies.bait.amount != null && TridentClient.playerState.supplies.bait.amount != 0) {
                    TridentClient.playerState.supplies.bait.amount = TridentClient.playerState.supplies.bait.amount!! - 1
                }
                catchFinished = true
                (TridentClient.openedDialogs["supplies"] as SuppliesDialog?)?.refresh()
            }

            return@allowMessage true
        }
    }
}