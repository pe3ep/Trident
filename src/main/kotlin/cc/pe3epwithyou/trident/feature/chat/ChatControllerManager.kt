package cc.pe3epwithyou.trident.feature.chat

import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.minecraft
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
import net.minecraft.client.gui.screens.ChatScreen

object ChatControllerManager {
    private var currentController: ChatController? = null

    fun register() {
        ClientSendMessageEvents.ALLOW_CHAT.register allowChat@{ original ->
            if (!MCCIState.isOnIsland()) return@allowChat true
            val connection = minecraft().connection ?: return@allowChat true
            val chatController = currentController ?: return@allowChat true
            if (chatController.shouldModifyChat(original)) {
                connection.sendCommand(chatController.processChat(original) ?: return@allowChat true)
                return@allowChat false
            }
            return@allowChat true
        }

        ClientSendMessageEvents.MODIFY_COMMAND.register modifyCommand@{ original ->
            if (!MCCIState.isOnIsland()) return@modifyCommand original
            val chatController = currentController ?: return@modifyCommand original
            if (chatController.shouldModifyCommand(original)) {
                return@modifyCommand chatController.processCommand(original) ?: original
            }
            return@modifyCommand original
        }
    }

    fun setController(chatController: ChatController) {
        Logger.debugLog("Setting chat controller: ${chatController::class.simpleName}")
        currentController = chatController
        refreshChatScreen()
    }

    fun clearController() {
        Logger.debugLog("Removing chat controller")
        currentController = null
        refreshChatScreen()
    }

    fun getController(): ChatController? {
        return currentController
    }

    /**
     * re-inits chat screen (hacky, but works)
     */
    fun refreshChatScreen() {
        val client = minecraft()
        val screen = client.screen
        if (screen is ChatScreen) {
            client.setScreen(screen)
        }
    }
}