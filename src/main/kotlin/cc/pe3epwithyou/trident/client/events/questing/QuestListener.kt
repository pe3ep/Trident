package cc.pe3epwithyou.trident.client.events.questing

import cc.pe3epwithyou.trident.client.events.questing.BattleBoxQuestEvents.handleBattleBox
import cc.pe3epwithyou.trident.client.events.questing.SkyBattleQuestEvents.handleSkyBattle
import cc.pe3epwithyou.trident.client.events.questing.SurvivorQuestEvents.handlePKWS
import cc.pe3epwithyou.trident.client.events.questing.TGTTOSQuestEvents.handleTGTTOS
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.WorldUtils.getGameID
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object QuestListener {
    val interruptibleTasks: ConcurrentHashMap<UUID, DelayedAction.DelayedTask> = ConcurrentHashMap()

    fun handleSubtitle(m: Component) {
        if (!Config.Questing.enabled) return
        if (MCCIslandState.game == MCCGame.PARKOUR_WARRIOR_DOJO) DojoQuestEvents.handlePKWD(m)
        if (MCCIslandState.game == MCCGame.HITW) HITWQuestEvents.handlePlacement(m)
    }

    fun handleTimedQuest(minutes: Long, shouldInterrupt: Boolean = false, action: () -> Unit) {
        if (!Config.Questing.enabled) return
        val initialID = getGameID()
        val task = DelayedAction.delay(TimeUnit.MINUTES.toMillis(minutes)) {
            val currentID = getGameID()
            if (initialID != currentID) return@delay
            action.invoke()
        }
        ChatUtils.debugLog("Scheduled task with: ${task.id}")
        if (!shouldInterrupt) return
        interruptibleTasks[task.id] = task
    }

    fun interruptTasks() {
        interruptibleTasks.forEach { (_, task) ->
            task.cancel()
        }
        interruptibleTasks.clear()
    }

    fun register() {
        ClientReceiveMessageEvents.GAME.register eventHandler@{ message, _ ->
            if (!Config.Questing.enabled) return@eventHandler

            if (MCCIslandState.game == MCCGame.PARKOUR_WARRIOR_SURVIVOR) handlePKWS(message)
            if (MCCIslandState.game == MCCGame.BATTLE_BOX) handleBattleBox(message)
            if (MCCIslandState.game == MCCGame.TGTTOS) handleTGTTOS(message)
            if (MCCIslandState.game == MCCGame.SKY_BATTLE) handleSkyBattle(message)
        }
    }
}