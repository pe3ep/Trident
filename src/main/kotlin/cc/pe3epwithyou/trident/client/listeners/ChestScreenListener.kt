package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.events.container.ContainerEvents
import cc.pe3epwithyou.trident.feature.doll.DollCosmetics
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.context
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import cc.pe3epwithyou.trident.utils.waitForItems
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.network.chat.Component

object ChestScreenListener {
    fun register() {
        ScreenEvents.AFTER_INIT.register { _, screen: Screen, _, _ ->
            try {
                if (screen is ContainerScreen) {
                    if (!MCCIState.isOnIsland()) return@register
                    DollCosmetics.resetCosmetics()
                    waitForItems(screen) {
                        ContainerEvents.OPEN.invoker().invoke(screen.context())
                    }

                    Logger.debugLog("Screen title: ${screen.title.string}")
                }
            } catch (e: Exception) {
                Logger.sendMessage(
                    Component.literal("Something went wrong when opening screen, please contact developers about this issue")
                        .withSwatch(TridentFont.ERROR)
                )

                Logger.error("Something went wrong when opening screen, ${e.message}")
            }
        }
    }
}