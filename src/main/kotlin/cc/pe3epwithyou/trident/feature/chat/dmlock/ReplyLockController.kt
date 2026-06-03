package cc.pe3epwithyou.trident.feature.chat.dmlock

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.chat.ChatController

class ReplyLockController(val user: String) : ChatController {
    override fun shouldModifyChat(original: String): Boolean {
        return Config.Global.replyLock
    }

    override fun processChat(original: String): String {
        if (!Config.Global.replyLock) return original
        return "msg $user $original"
    }

    override fun shouldModifyCommand(original: String): Boolean {
        return Config.Global.replyLock
    }

    override fun processCommand(original: String): String {
        if (!Config.Global.replyLock) return original
        var modified = original

        if (modified.startsWith("r ") || modified.startsWith("reply ")) {
            modified = modified.removePrefix("reply ")
            modified = modified.removePrefix("r ")
            modified = "msg $user $modified"
        }

        return modified
    }
}