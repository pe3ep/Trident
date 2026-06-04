package cc.pe3epwithyou.trident.feature.chat.chatroom

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.events.click.ClickEvents
import cc.pe3epwithyou.trident.feature.chat.ChatControllerManager
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.playerState
import kotlinx.serialization.Serializable
import net.minecraft.core.component.DataComponents

object Chatrooms {
    fun getActiveChatroom(): Chatroom? =
        (ChatControllerManager.getController() as? ChatroomController)?.chatroom

    fun register() {
        ClickEvents.onClick {
            if (!Config.Global.chatroomChannelButtons) return@onClick
            requireTitle("CHAT ROOMS")
            if (middle) {
                val id = clickedItem()?.hoverName?.string ?: return@onClick
                val model = clickedItem()?.components?.get(DataComponents.ITEM_MODEL) ?: return@onClick
                val color = ChatroomColor.entries.find {it.getItemModel() == model} ?: ChatroomColor.WHITE
                val chatroom = Chatroom(id, color)
                if (playerState().activeChatrooms.removeIf { chatroom.id == it.id }) {
                    Logger.sendMessage("Removed chatroom $chatroom")
                    return@onClick
                }

                playerState().activeChatrooms.add(chatroom)
                Logger.sendMessage("Added chatroom $chatroom")
            }
        }
    }

    @Serializable
    data class Chatroom(val id: String, val color: ChatroomColor)

    @Suppress("unused")
    enum class ChatroomColor(
        val color: Int
    ) {
        WHITE(0xFFFFFF),
        YELLOW(0xFFFF55),
        BLUE(0x5555FF),
        GREEN(0x55FF55),
        ORANGE(0xFF5F1F),
        PINK(0xFF55FF),
        TEAL(0x55FFFF);

        fun getItemModel() = Resources.mcc("island_interface/settings/chat_bubble_${this.name.lowercase()}")
    }
}