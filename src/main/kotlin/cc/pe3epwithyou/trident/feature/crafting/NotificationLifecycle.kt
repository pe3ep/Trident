package cc.pe3epwithyou.trident.feature.crafting

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.crafting.CraftingNotifications.Notification
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft

class NotificationLifecycle : ClientTickEvents.EndTick {
    companion object {
        private val instance = NotificationLifecycle()

        fun register() {
            ClientTickEvents.END_CLIENT_TICK.register(instance)
        }
    }

    override fun onEndTick(client: Minecraft) {
        if (!Config.Global.craftingNotifications) return
        Trident.playerState.craftingNotifications.assembler.forEach(Notification::check)
        Trident.playerState.craftingNotifications.fusion.forEach(Notification::check)
    }
}