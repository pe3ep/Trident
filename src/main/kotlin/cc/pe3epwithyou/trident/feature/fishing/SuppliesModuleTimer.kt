package cc.pe3epwithyou.trident.feature.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.TridentFont
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import java.util.*

class SuppliesModuleTimer : ClientTickEvents.EndTick {
    private var unstableOverclock: Long = 0
    private var unstableOverclockCooldown: Long = 0

    private var supremeOverclock: Long = 0
    private var supremeOverclockCooldown: Long = 0

    companion object {
        val INSTANCE: SuppliesModuleTimer = SuppliesModuleTimer()
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

    fun startSupremeOverclock() {
        val supremeOverclockState = TridentClient.playerState.supplies.overclocks.supreme
        this.supremeOverclock = supremeOverclockState.duration
        this.supremeOverclockCooldown = supremeOverclockState.cooldownDuration

        supremeOverclockState.isActive = true
        supremeOverclockState.isCooldown = false
    }

    override fun onEndTick(client: Minecraft) {
        tickUnstableOverclock()
        tickUnstableOverclockCooldown()
        tickSupremeOverclock()
        tickSupremeOverclockCooldown()
    }

    private fun tickUnstableOverclock() {
        val unstableOverclockState = TridentClient.playerState.supplies.overclocks.unstable
        if (!unstableOverclockState.isActive) return

        if (this.unstableOverclock % 20 == 0L) {
            unstableOverclockState.timeLeft = this.unstableOverclock
            DialogCollection.refreshDialog("supplies")
            DialogCollection.refreshDialog("upgrades")
            DialogCollection.refreshDialog("chanceperks")
        }

        if (this.unstableOverclock == 0L) {
            unstableOverclockState.isActive = false
            unstableOverclockState.isCooldown = true

            DialogCollection.refreshDialog("supplies")
            DialogCollection.refreshDialog("upgrades")
            DialogCollection.refreshDialog("chanceperks")
        }
        this.unstableOverclock--

    }

    private fun tickUnstableOverclockCooldown() {
        val unstableOverclockState = TridentClient.playerState.supplies.overclocks.unstable
        if (!unstableOverclockState.isCooldown) return

        if (this.unstableOverclockCooldown % 20 == 0L) {
            unstableOverclockState.cooldownLeft = this.unstableOverclockCooldown
            DialogCollection.refreshDialog("supplies")
            DialogCollection.refreshDialog("upgrades")
            DialogCollection.refreshDialog("chanceperks")
        }

        if (this.unstableOverclockCooldown == 0L) {
            unstableOverclockState.isActive = false
            unstableOverclockState.isCooldown = false
            sendReadyMessage("Unstable Overclock")
            DialogCollection.refreshDialog("supplies")
            DialogCollection.refreshDialog("upgrades")
            DialogCollection.refreshDialog("chanceperks")
        }
        this.unstableOverclockCooldown--

    }

    private fun tickSupremeOverclock() {
        val supremeOverclockState = TridentClient.playerState.supplies.overclocks.supreme
        if (!supremeOverclockState.isActive) return

        if (this.supremeOverclock % 20 == 0L) {
            supremeOverclockState.timeLeft = this.supremeOverclock
            DialogCollection.refreshDialog("supplies")
            DialogCollection.refreshDialog("upgrades")
            DialogCollection.refreshDialog("chanceperks")
        }

        if (this.supremeOverclock == 0L) {
            supremeOverclockState.isActive = false
            supremeOverclockState.isCooldown = true

            DialogCollection.refreshDialog("supplies")
            DialogCollection.refreshDialog("upgrades")
            DialogCollection.refreshDialog("chanceperks")
        }
        this.supremeOverclock--

    }

    private fun tickSupremeOverclockCooldown() {
        val supremeOverclockState = TridentClient.playerState.supplies.overclocks.supreme
        if (!supremeOverclockState.isCooldown) return

        if (this.supremeOverclockCooldown % 20 == 0L) {
            supremeOverclockState.cooldownLeft = this.supremeOverclockCooldown
            DialogCollection.refreshDialog("supplies")
            DialogCollection.refreshDialog("upgrades")
            DialogCollection.refreshDialog("chanceperks")
        }

        if (this.supremeOverclockCooldown == 0L) {
            supremeOverclockState.isActive = false
            supremeOverclockState.isCooldown = false
            sendReadyMessage("Supreme Overclock")
            DialogCollection.refreshDialog("supplies")
            DialogCollection.refreshDialog("upgrades")
            DialogCollection.refreshDialog("chanceperks")
        }
        this.supremeOverclockCooldown--

    }

    private fun sendReadyMessage(oc: String) {
        val component = Component.literal("Your ").withColor(TridentFont.TRIDENT_COLOR)
            .append(Component.literal(oc).withColor(TridentFont.TRIDENT_ACCENT))
            .append(Component.literal(" is no longer on cooldown and is ready to be used").withColor(TridentFont.TRIDENT_COLOR))
        ChatUtils.sendMessage(component, true)
        Minecraft.getInstance().player?.playSound(
            SoundEvent(ResourceLocation.fromNamespaceAndPath("mcc", "games.fishing.overclock_ready"), Optional.empty())
        )
    }
}