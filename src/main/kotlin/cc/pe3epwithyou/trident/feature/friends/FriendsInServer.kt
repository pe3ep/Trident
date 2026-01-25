package cc.pe3epwithyou.trident.feature.friends

import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket
import net.minecraft.network.protocol.game.ClientboundTabListPacket
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object FriendsInServer {
    private const val ID = 676913370
    private val regex = Regex("""(?:INSTANCE|FISHTANCE) (\d+)""")

    var friends: List<String> = emptyList()
    var prevInstance: Int? = null
    var currentInstance: Int? = null

    fun sendPacket() {
        val connection = Minecraft.getInstance().connection ?: return
        connection.send(ServerboundCommandSuggestionPacket(ID, "/friend remove "))
    }

    fun handlePacket(packet: Packet<*>, ci: CallbackInfo) {
        if (packet is ClientboundTabListPacket) {
            val match = regex.find(packet.footer.string)
            if (match != null) {
                val instance = match.groupValues[1].toIntOrNull()
                prevInstance = currentInstance
                currentInstance = instance
            } else {
                prevInstance = null
                currentInstance = null
            }
        }

        if (packet !is ClientboundCommandSuggestionsPacket || packet.id != ID) return
        ci.cancel()

        val suggestions = packet.suggestions
        friends = suggestions.mapNotNull { return@mapNotNull it.text }
        Minecraft.getInstance().execute {
            sendMessage()
        }
    }

    fun sendMessage() = DelayedAction.delayTicks(45) {
        if (!check()) return@delayTicks
        val connection = Minecraft.getInstance().connection ?: return@delayTicks
        val onlinePlayers = connection.onlinePlayers.mapNotNull { return@mapNotNull it.profile.name }.filter { !it.startsWith("MCCTabPlayer") && !it.startsWith("MCC_NPC") }
        val onlineFriends = onlinePlayers
            .filter { it in friends }
        Logger.debugLog("Online players [${onlinePlayers.size}]: $onlinePlayers")
        Logger.debugLog("Online friends: ${onlineFriends.size}/${friends.size}")
        if (onlineFriends.isEmpty()) return@delayTicks
        val friendsString = onlineFriends.joinToString(", ")

        val component = Component.literal(String.format("Friends in this %s: ", getServerName()))
            .withSwatch(TridentFont.TRIDENT_COLOR).append(
                Component.literal(friendsString).withSwatch(TridentFont.TRIDENT_ACCENT)
            )

        Logger.sendMessage(component, true)
    }

    private fun check(): Boolean {
        if (MCCIState.game == Game.FISHING || MCCIState.game == Game.HUB) return currentInstance != null && currentInstance != prevInstance
        return true
    }

    private fun getServerName(): String {
        return when (MCCIState.game) {
            Game.HUB -> "lobby"
            Game.FISHING -> "fishtance"
            else -> "game"
        }
    }
}
