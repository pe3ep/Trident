package cc.pe3epwithyou.trident.feature.questing

import cc.pe3epwithyou.trident.client.listeners.ChestScreenListener
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.questing.game.*
import cc.pe3epwithyou.trident.interfaces.questing.QuestingDialog
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
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
        if (MCCIState.game == Game.PARKOUR_WARRIOR_DOJO) PKWDojoHandlers.handle(m)
        if (MCCIState.game == Game.HITW) HITWHandlers.handlePlacement(m)
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
            if (MCCIState.game == Game.PARKOUR_WARRIOR_SURVIVOR) PKWSurvivorHandlers.handle(message)
            // BB Arena and BB quests are shared
            if (MCCIState.game == Game.BATTLE_BOX || MCCIState.game == Game.BATTLE_BOX_ARENA) BattleBoxHandlers.handle(message)

            if (MCCIState.game == Game.TGTTOS) TGTTOSHandlers.handle(message)
            if (MCCIState.game == Game.SKY_BATTLE) SkyBattleHandlers.handle(message)
            if (MCCIState.game == Game.ROCKET_SPLEEF_RUSH) RSRHandlers.handle(
                message
            )
            if (MCCIState.game == Game.DYNABALL) DynaballHandlers.handle(message)
        }
    }

    fun checkDesynced(m: Component) {
        val match =
            Regex("\\(.\\) (Quest Scroll|Quest) Completed! Check your Quest Log for rewards\\.").matches(m.string)
        if (!match) return
        if (isAQuestCompleted()) return
        QuestingDialog.dialogState = QuestingDialog.QuestingDialogState.DESYNCED
    }

    fun isAQuestCompleted(): Boolean {
        QuestStorage.getActiveQuests(MCCIState.game).forEach { q ->
            if (q.isCompleted) return true
        }
        return false
    }

    fun checkIfPlobby(): Boolean {
        val scoreboard = Minecraft.getInstance().level?.scoreboard ?: return false
        val obj = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return false
        return obj.displayName.string.contains("Plobby", true)
    }
}