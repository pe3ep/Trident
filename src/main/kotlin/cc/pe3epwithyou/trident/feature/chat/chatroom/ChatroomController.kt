package cc.pe3epwithyou.trident.feature.chat.chatroom

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.chat.ChatController

class ChatroomController(val chatroom: Chatrooms.Chatroom) : ChatController {
    override fun shouldModifyChat(original: String): Boolean {
        return Config.Global.chatroomChannelButtons
    }

    override fun processChat(original: String): String {
        if (!Config.Global.chatroomChannelButtons) return original
        return "cr ${chatroom.id} $original"
    }
}