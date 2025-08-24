package cc.pe3epwithyou.trident.feature.questing

import cc.pe3epwithyou.trident.client.events.ChestScreenListener
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.dialogs.questing.QuestingDialog
import cc.pe3epwithyou.trident.feature.questing.game.*
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.WorldUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.scores.DisplaySlot
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object QuestListener {
    val interruptibleTasks: ConcurrentHashMap<UUID, DelayedAction.DelayedTask> = ConcurrentHashMap()
    var isWaitingRefresh: Boolean = false

    fun handleRefreshTasksChat(m: Component) {
        if (!Regex("^\\(.\\) Quest Tasks Rerolled!").matches(m.string)) return
        isWaitingRefresh = true
    }

    fun handleRefreshTasksItem(item: ItemStack) {
        if (!isWaitingRefresh) return
        if ("Quest" !in item.hoverName.string) return
        val screen = (Minecraft.getInstance().screen ?: return) as ContainerScreen
        ChestScreenListener.findQuests(screen)
    }

    fun handleSubtitle(m: Component) {
        if (!Config.Questing.enabled) return
        if (MCCIslandState.game == MCCGame.PARKOUR_WARRIOR_DOJO) PKWDojoHandlers.handlePKWD(m)
        if (MCCIslandState.game == MCCGame.HITW) HITWHandlers.handlePlacement(m)
    }

    fun handleTimedQuest(minutes: Long, shouldInterrupt: Boolean = false, action: () -> Unit) {
        if (!Config.Questing.enabled) return
        val initialID = WorldUtils.getGameID()
        val task = DelayedAction.delay(TimeUnit.MINUTES.toMillis(minutes)) {
            val currentID = WorldUtils.getGameID()
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
            if (checkIfPlobby()) return@eventHandler
            handleRefreshTasksChat(message)
            checkDesynced(message)
            if (MCCIslandState.game == MCCGame.PARKOUR_WARRIOR_SURVIVOR) PKWSurvivorHandlers.handlePKWS(message)
            if (MCCIslandState.game == MCCGame.BATTLE_BOX) BattleBoxHandlers.handleBattleBox(message)
            if (MCCIslandState.game == MCCGame.TGTTOS) TGTTOSHandlers.handleTGTTOS(message)
            if (MCCIslandState.game == MCCGame.SKY_BATTLE) SkyBattleHandlers.handleSkyBattle(message)
            if (MCCIslandState.game == MCCGame.ROCKET_SPLEEF_RUSH) RSRHandlers.handleRocketSpleefRush(
                message
            )
            if (MCCIslandState.game == MCCGame.DYNABALL) DynaballHandlers.handleDynaball(message)
        }
    }

    fun checkDesynced(m: Component) {
        val match = Regex("\\(.\\) (Quest Scroll|Quest) Completed! Check your Quest Log for rewards\\.").matches(m.string)
        if (!match) return
        if (isAQuestCompleted()) return
        QuestingDialog.isDesynced = true
    }

    fun isAQuestCompleted(): Boolean {
        QuestStorage.getActiveQuests(MCCIslandState.game).forEach { q ->
            if (q.isCompleted) return true
        }
        return false
    }

    fun checkIfPlobby(): Boolean {
        val scoreboard = Minecraft.getInstance().player?.scoreboard ?: return false
        val obj = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return false
        return obj.displayName.string.contains("Plobby", true)
    }
}