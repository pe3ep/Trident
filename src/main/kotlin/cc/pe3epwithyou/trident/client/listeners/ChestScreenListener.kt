package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.events.container.ContainerEvents
import cc.pe3epwithyou.trident.feature.crafting.CraftingNotifications
import cc.pe3epwithyou.trident.feature.discord.ActivityManager
import cc.pe3epwithyou.trident.feature.doll.Doll
import cc.pe3epwithyou.trident.feature.doll.DollCosmetics
import cc.pe3epwithyou.trident.feature.exchange.ExchangeHandler
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.*
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import cc.pe3epwithyou.trident.utils.extensions.WindowExtensions.focusWindowIfInactive
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.network.chat.Component


// TODO: Refactor this whole object
object ChestScreenListener {
    private fun handleScreen(screen: ContainerScreen) {
        if (!MCCIState.isOnIsland()) return
        val title = screen.title.string
        DollCosmetics.resetCosmetics()
        waitForItems(screen) {
            ContainerEvents.OPEN.invoker().invoke(screen.context())
        }

        useScreen(screen) {
            checkName("ISLAND EXCHANGE") { await { ExchangeHandler.handleScreen(it) } }
            checkName("MATCH FOUND! (0/") { await { minecraft().window.focusWindowIfInactive() } }
            checkName("BATTLE BOX ARENA") { await { ActivityManager.Arena.handleScreen(it) } }
            await { Doll.addWidgets(it) }
            await { CraftingNotifications.handleScreen(it) }
        }

        Logger.debugLog("Screen title: $title")
    }

    fun register() {
        ScreenEvents.AFTER_INIT.register { _, screen: Screen, _, _ ->
            try {
                if (screen is ContainerScreen) handleScreen(screen)
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