package cc.pe3epwithyou.trident.feature.disguise

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.events.container.ContainerEvents
import cc.pe3epwithyou.trident.mixin.accessors.GuiAccessor
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.minecraft

object Disguise {
    var disguiseIconCache: String? = null
    var isDisguised: Boolean = false

    fun handleChatMessage(message: String) {
        if (!MCCIState.isOnIsland()) return
        if ("You are disguised" in message) {
            isDisguised = true
            sendWhoami()
            return
        }
        if ("You deactivated disguise mode" in message ||  "Party disguise mode is now disabled" in message) {
            isDisguised = false
            return
        }
        if ("Disguise mode is active" in message || "Party disguise mode is active" in message) {
            isDisguised = true
        }
    }

    fun register() {
        ContainerEvents.onOpen { checkActionbar() }
        ContainerEvents.onClose { checkActionbar() }
    }

    fun checkActionbar(): Boolean {
        if (!MCCIState.isOnIsland()) return false
        val gui = minecraft().gui as GuiAccessor
        val actionbar = gui.overlayMessageString ?: run {
            isDisguised = false
            return false
        }
        disguiseIconCache?.let {
            isDisguised = actionbar.string.contains(it)
            return isDisguised
        }
        return false
    }

    fun sendWhoami() = DelayedAction.delayTicks(60L) delayed@{
        if (!MCCIState.isOnIsland()) return@delayed
        if (!Config.Global.autoWhoami) return@delayed
        val game = MCCIState.game
        Logger.debugLog("disguise icon cache: $disguiseIconCache")
        Logger.debugLog("isDisguised: $isDisguised")
        Logger.debugLog("game: $game")
        if (game == Game.HUB || game == Game.FISHING || game == Game.PARKOUR_WARRIOR_DOJO) return@delayed
        val connection = minecraft().connection ?: return@delayed
        connection.sendCommand("whoami")
    }
}