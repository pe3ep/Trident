package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.state.fishing.UpgradeLine
import cc.pe3epwithyou.trident.mixin.BossHealthOverlayAccessor
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.BossHealthOverlay
import net.minecraft.client.gui.components.LerpingBossEvent
import net.minecraft.network.chat.Component
import java.util.*

class TideWindBossbarListener : ClientTickEvents.EndTick {
    private var lastSeen: String = ""

    companion object {
        fun register() {
            ClientTickEvents.END_CLIENT_TICK.register(TideWindBossbarListener())
        }
    }

    override fun onEndTick(client: Minecraft) {
        val gui = client.gui ?: return
        val overlay: BossHealthOverlay = gui.bossOverlay
        val accessor = overlay as BossHealthOverlayAccessor
        val events: Map<UUID, LerpingBossEvent> = accessor.events
        val titles = events.values.map { it.name.string }.joinToString(" | ")
        if (titles == lastSeen) return
        lastSeen = titles

        val ps = TridentClient.playerState
        ps.inGrotto = titles.contains("Stability:")

        ps.tideLines.clear()
        ps.windLines.clear()
        ps.magnetPylonBonus = 0
        if (!ps.inGrotto) {
            fun detect(line: UpgradeLine, key: String) {
                if (titles.contains(key, ignoreCase = true)) ps.tideLines.add(line)
                if (titles.contains(key.replace(" Tide", " Winds"), ignoreCase = true)) ps.windLines.add(line)
            }
            detect(UpgradeLine.STRONG, "Strong Tide")
            detect(UpgradeLine.WISE, "Wise Tide")
            detect(UpgradeLine.GLIMMERING, "Glimmering Tide")
            detect(UpgradeLine.GREEDY, "Greedy Tide")
            detect(UpgradeLine.LUCKY, "Lucky Tide")

            // Pylon: X
            val pylonRegex = Regex("(?i)Pylon:\\s*(\\d+)")
            val m = pylonRegex.find(titles)
            ps.magnetPylonBonus = m?.groups?.get(1)?.value?.toIntOrNull() ?: 0
        }

        DialogCollection.refreshDialog("hookchances")
        DialogCollection.refreshDialog("magnetchances")
        DialogCollection.refreshDialog("chanceperks")
        DialogCollection.refreshDialog("spot")
    }
}


