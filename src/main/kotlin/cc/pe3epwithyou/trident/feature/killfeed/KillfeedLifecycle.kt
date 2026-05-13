package cc.pe3epwithyou.trident.feature.killfeed

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.killfeed.widgets.KillWidget
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft

class KillfeedLifecycle : ClientTickEvents.EndTick {

    companion object {
        private val instance = KillfeedLifecycle()
        val killWidgets = LinkedHashMap<Long, KillWidget>()

        fun register() {
            ClientTickEvents.END_CLIENT_TICK.register(instance)
        }

        fun addKill(killWidget: KillWidget) {
            val timestamp = System.currentTimeMillis()

            killWidgets[timestamp] = killWidget

            val maxKills = Config.KillFeed.maxKills

            while (killWidgets.size > maxKills) {
                val oldestKey = killWidgets.keys.firstOrNull() ?: break
                removeWidget(oldestKey)
            }

            DialogCollection.refreshDialog("killfeed")
        }

        fun applyKillAssist() {
            val lastEntry = killWidgets.entries.lastOrNull() ?: return

            val timestamp = lastEntry.key
            val last = lastEntry.value

            killWidgets[timestamp] = KillWidget(
                victim = last.victim,
                killMethod = last.killMethod,
                attacker = last.attacker,
                killColors = last.killColors,
                streak = last.streak,
                hasAssist = true
            )

            DialogCollection.refreshDialog("killfeed")
        }

        fun removeWidget(timestamp: Long) {
            killWidgets.remove(timestamp)
            DialogCollection.refreshDialog("killfeed")
        }

        fun clearKills() {
            killWidgets.clear()
            DialogCollection.refreshDialog("killfeed")
        }
    }

    override fun onEndTick(client: Minecraft) {
        if (!Config.KillFeed.enabled) return
        processTick()
    }

    private fun processTick() {
        val delay = Config.KillFeed.removeKillTime * 1000L
        if (delay == 0L) return

        val now = System.currentTimeMillis()

        var changed = false

        while (true) {
            val entry = killWidgets.entries.firstOrNull() ?: break

            val timestamp = entry.key

            if (now >= timestamp + delay) {
                removeWidget(timestamp)
                changed = true
            } else {
                break
            }
        }

        if (changed) {
            DialogCollection.refreshDialog("killfeed")
        }
    }
}