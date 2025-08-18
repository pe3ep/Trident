package cc.pe3epwithyou.trident.client.events.questing

import cc.pe3epwithyou.trident.client.events.questing.BattleBoxQuestEvents.handleBattleBox
import cc.pe3epwithyou.trident.client.events.questing.DojoQuestEvents.handlePKWD
import cc.pe3epwithyou.trident.client.events.questing.SurvivorQuestEvents.handlePKWS
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.mixin.BossHealthOverlayAccessor
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.WorldUtils.getGameID
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import java.util.UUID
import java.util.concurrent.TimeUnit

object QuestListener {
    fun handleSubtitle(m: Component) {
        if (MCCIslandState.game == MCCGame.PARKOUR_WARRIOR_DOJO) handlePKWD(m)
    }

    fun handleTimedQuests(minutes: Long, action: () -> Unit) {
        val initialID = getGameID()
        DelayedAction.delay(TimeUnit.MINUTES.toMillis(minutes)) {
            val currentID = getGameID()
            if (initialID != currentID) return@delay
            action.invoke()
        }
    }

    fun register() {
        ClientReceiveMessageEvents.GAME.register eventHandler@{ message, _ ->
            if (!Config.Questing.enabled) return@eventHandler

            if (MCCIslandState.game == MCCGame.PARKOUR_WARRIOR_SURVIVOR) handlePKWS(message)
            if (MCCIslandState.game == MCCGame.BATTLE_BOX) handleBattleBox(message)
        }
    }
}