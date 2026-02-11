package cc.pe3epwithyou.trident.feature.questing

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.events.container.ContainerContext
import cc.pe3epwithyou.trident.events.container.ContainerEvents
import cc.pe3epwithyou.trident.feature.questing.lock.QuestLock
import cc.pe3epwithyou.trident.utils.context
import cc.pe3epwithyou.trident.utils.minecraft
import cc.pe3epwithyou.trident.utils.screenWidth
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

object QuestListener {
    var isWaitingRefresh: Boolean = false

    fun handleRefreshTasksChat(m: Component) {
        if (!Regex("^\\(.\\) Quest Tasks Rerolled!").matches(m.string)) return
        isWaitingRefresh = true
    }

    fun handleRefreshTasksItem(item: ItemStack) {
        if (!isWaitingRefresh) return
        if ("Quest" !in item.hoverName.string) return
        val screen = (minecraft().screen ?: return) as ContainerScreen
        findQuests(screen.context())
    }

    fun register() {
        ClientReceiveMessageEvents.GAME.register eventHandler@{ message, _ ->
            if (!Config.Questing.enabled) return@eventHandler
            handleRefreshTasksChat(message)
        }

        ContainerEvents.onInit {
            titleHas("ISLAND REWARDS")
            if (!Config.Global.questLock) return@onInit
            val widget = QuestLock.Widget(2, topPos() + imageHeight())
            widget.x = (screenWidth() / 2 - widget.getWidth() / 2)
            addRenderable(widget)
        }
        ContainerEvents.onOpen(::findQuests)
        ContainerEvents.onClose(::findQuests)

    }

    fun findQuests(ctx: ContainerContext) =
        with(ctx) {
            titleHas("ISLAND REWARDS")

            val slotQuests = mutableListOf<Quest>()

            fun handleQuestSlot(index: Int): List<Quest> {
                val slot = slot(index) ?: return emptyList()
                val quests = QuestingParser.parseQuestSlot(slot).orEmpty()

                QuestLock.questSlots[index]?.apply {
                    this.quests = quests
                    isLocked = QuestLock.shouldLock(quests)
                }

                slotQuests += quests
                return quests
            }

            fun handleRemainingSlot(index: Int): Int {
                val slot = slot(index) ?: return 0
                return QuestingParser.parseRemainingSlot(slot)
            }

            // Daily
            handleQuestSlot(37)
            QuestStorage.dailyRemaining = handleRemainingSlot(28)

            // Weekly
            handleQuestSlot(39)
            QuestStorage.weeklyRemaining = handleRemainingSlot(30)

            // Scroll
            handleQuestSlot(41)

            QuestStorage.loadQuests(slotQuests)
        }

}