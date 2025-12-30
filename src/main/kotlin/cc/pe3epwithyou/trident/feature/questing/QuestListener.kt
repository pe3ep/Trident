package cc.pe3epwithyou.trident.feature.questing

import cc.pe3epwithyou.trident.client.listeners.ChestScreenListener
import cc.pe3epwithyou.trident.config.Config
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
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
        val screen = (Minecraft.getInstance().screen ?: return) as ContainerScreen
        ChestScreenListener.findQuests(screen)
    }

    fun register() {
        ClientReceiveMessageEvents.GAME.register eventHandler@{ message, _ ->
            if (!Config.Questing.enabled) return@eventHandler
            handleRefreshTasksChat(message)
        }
    }
}