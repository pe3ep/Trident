package cc.pe3epwithyou.trident.feature.friends

import cc.pe3epwithyou.trident.feature.discord.ActivityManager
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.SuggestionPacket
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.popped
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundTabListPacket

object FriendsInServer {
    private val regex = Regex("""(?:INSTANCE|FISHTANCE) (\d+)""")

    var friends: List<String> = emptyList()
    var prevInstance: Int? = null
    var currentInstance: Int? = null

    fun request() = SuggestionPacket.requestSuggestions("/friend remove ", ::updateFriendsList)

    fun processTabPacket(packet: Packet<*>) {
        if (packet !is ClientboundTabListPacket) return
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

    fun updateFriendsList(suggestions: List<String>) {
        friends = suggestions
        sendMessage()
    }

    fun sendMessage() = DelayedAction.delayTicks(45) {
        if (!check()) return@delayTicks
        val connection = Minecraft.getInstance().connection ?: return@delayTicks
        val onlinePlayers = connection.onlinePlayers.mapNotNull { it.profile.name }
            .filter { !it.startsWith("MCCTabPlayer") && !it.startsWith("MCC_NPC") }
        val onlineFriends = onlinePlayers.filter { it in friends }
        Logger.debugLog("Online players [${onlinePlayers.size}]: $onlinePlayers")
        Logger.debugLog("Online friends: ${onlineFriends.size}/${friends.size}")
        if (onlineFriends.isEmpty()) return@delayTicks


        val component = Component.literal(String.format("Friends in this %s: ", getServerName()))
            .withSwatch(TridentFont.TRIDENT_COLOR).append(buildFriendsComponent(onlineFriends))

        Logger.sendMessage(component, true)
    }

    private fun buildFriendsComponent(onlineFriends: List<String>): Component {
        val party = ActivityManager.Party.members.map { it.lowercase() }

        val components = onlineFriends.map {
            if (it.lowercase() in party) {
                return@map Component.literal("$it (Party)")
                    .setStyle(Style.EMPTY.withColor(0xa8a9fb).withShadowColor(0x2a2a3e.opaqueColor()))
                    .popped()
            } else {
                return@map Component.literal(it).withSwatch(TridentFont.TRIDENT_ACCENT).popped()
            }
        }.sortedBy { !it.string.contains("(Party)")}

        val c = Component.empty()
        components.forEachIndexed { index, component ->
            if (index != 0) c.append(", ").withSwatch(TridentFont.TRIDENT_ACCENT).popped()
            c.append(component)
        }

        return c
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
