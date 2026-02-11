package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.events.container.ContainerEvents
import cc.pe3epwithyou.trident.feature.crafting.CraftingNotifications
import cc.pe3epwithyou.trident.feature.discord.ActivityManager
import cc.pe3epwithyou.trident.feature.doll.Doll
import cc.pe3epwithyou.trident.feature.doll.DollCosmetics
import cc.pe3epwithyou.trident.feature.exchange.ExchangeHandler
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.state.Research
import cc.pe3epwithyou.trident.utils.*
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.safeGetLine
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
            checkName("FISHING PROGRESS") { await { findFishingResearch(it) } }
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

    fun findFishingResearch(screen: ContainerScreen) {
        if ("FISHING PROGRESS" !in screen.title.string) return

        // empty the list
        playerState().research.researchTypes = mutableListOf()

        val researchSlots = listOf(12, 13, 14, 15, 16)
        val researchTypes =
            mapOf(12 to "Strong", 13 to "Wise", 14 to "Glimmering", 15 to "Greedy", 16 to "Lucky")
        for (slot in researchSlots) {
            val tierLine =
                screen.menu.slots[slot].item.safeGetLine(0)?.string?.split("(")?.getOrNull(1)
                    ?: continue
            val tierLineNoBrackets = tierLine.dropLast(2)
            val tier = tierLineNoBrackets.split("/").getOrNull(0)?.parseFormattedInt() ?: continue

            val progressLine = screen.menu.slots[slot].item.safeGetLine(4)?.string ?: continue
            if ("Progress: " in progressLine) {
                val progressValues = progressLine.split(": ").getOrNull(1) ?: continue

                val amount = progressValues.split("/").getOrNull(0)?.parseFormattedInt() ?: continue
                val total = progressValues.split("/").getOrNull(1)?.parseFormattedInt() ?: continue

                playerState().research.researchTypes.add(
                    researchSlots.indexOf(slot), Research(
                        researchTypes[slot] ?: "Strong",
                        tier = tier,
                        progressThroughTier = amount,
                        totalForTier = total
                    )
                )
            }
        }

        playerState().research.needsUpdating = false
        DialogCollection.refreshDialog("research")
    }
}