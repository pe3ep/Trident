package cc.pe3epwithyou.trident.utils

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft

class TimerUtil : ClientTickEvents.EndTick {
    private var ticksUntilSomething: Long = 0
    private var schedueledTask: () -> Unit = {}
    fun setTimer(waitFor: Long, runnable: () -> Unit) {
        this.ticksUntilSomething = waitFor
        this.schedueledTask = runnable
    }


    override fun onEndTick(client: Minecraft) {
        if (--this.ticksUntilSomething == 0L) {
            ChatUtils.info("Something was scheduled and it sorta ran?")
            try {
                this.schedueledTask()
            } catch (e: Exception) {
                ChatUtils.error("Error occurred when scheduling task: ${e.message}")
            }
        }
    }

    companion object {
        val INSTANCE: TimerUtil = TimerUtil()
        fun register() {
            ClientTickEvents.END_CLIENT_TICK.register(INSTANCE)
        }
    }
}