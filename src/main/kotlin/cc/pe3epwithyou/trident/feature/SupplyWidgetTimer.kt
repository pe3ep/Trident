package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.dialogs.DialogCollection
import cc.pe3epwithyou.trident.utils.ChatUtils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft

class SupplyWidgetTimer : ClientTickEvents.EndTick {
    private var unstableOverclock: Long = 0
    private var unstableOverclockCooldown: Long = 0

    companion object {
        val INSTANCE: SupplyWidgetTimer = SupplyWidgetTimer()
        fun register() {
            ClientTickEvents.END_CLIENT_TICK.register(INSTANCE)
        }
    }

    fun startUnstableOverclock() {
        val unstableOverclockState = TridentClient.playerState.supplies.overclocks.unstable
        this.unstableOverclock = unstableOverclockState.duration
        this.unstableOverclockCooldown = unstableOverclockState.cooldownDuration

        unstableOverclockState.isActive = true
        unstableOverclockState.isCooldown = false
    }

    override fun onEndTick(client: Minecraft) {
        tickUnstableOverclock()
        tickUnstableOverclockCooldown()
    }

    private fun tickUnstableOverclock() {
        val unstableOverclockState = TridentClient.playerState.supplies.overclocks.unstable
        if (!unstableOverclockState.isActive) return

        if (this.unstableOverclock % 20 == 0L) {
            unstableOverclockState.timeLeft = this.unstableOverclock
            DialogCollection.refreshDialog("supplies")
        }

        if (this.unstableOverclock == 0L) {
            unstableOverclockState.isActive = false
            unstableOverclockState.isCooldown = true

            DialogCollection.refreshDialog("supplies")
        }
        this.unstableOverclock--

    }

    private fun tickUnstableOverclockCooldown() {
        val unstableOverclockState = TridentClient.playerState.supplies.overclocks.unstable
        if (!unstableOverclockState.isCooldown) return

        if (this.unstableOverclockCooldown % 20 == 0L) {
            unstableOverclockState.cooldownLeft = this.unstableOverclockCooldown
            DialogCollection.refreshDialog("supplies")
        }

        if (this.unstableOverclockCooldown == 0L) {
            unstableOverclockState.isActive = false
            unstableOverclockState.isCooldown = false
            ChatUtils.sendMessage("Your Unstable Overclock is no longer on cooldown and is ready to use.")
            DialogCollection.refreshDialog("supplies")
        }
        this.unstableOverclockCooldown--

    }

}