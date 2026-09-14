package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.client.packet.PacketHandler
import cc.pe3epwithyou.trident.state.MCCIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.milliseconds

object SuggestionPacket {
    val tasks: ConcurrentHashMap<Int, SuggestionTask> = ConcurrentHashMap()

    data class SuggestionTask(val id: Int, val callback: (List<String>) -> Unit) {
        fun processSuggestions(strings: List<String>) = minecraft().execute { callback(strings) }
    }

    fun requestSuggestions(command: String, callback: (List<String>) -> Unit) {
        if (!MCCIState.isOnIsland()) return
        val id: Int = command.hashCode()

        val task = SuggestionTask(id, callback)
        tasks[id] = task
        sendPacket(id, command)
        CoroutineScope(Dispatchers.IO).launch {
            delay(1_500.milliseconds)
            val task = tasks.remove(id) ?: return@launch
            Logger.debugLog("Failed to get suggestions for command $command")
            task.processSuggestions(emptyList())
        }
    }

    class CommandSuggestionsPacketHandler : PacketHandler {
        override fun check(packet: Packet<*>): Boolean =
            packet is ClientboundCommandSuggestionsPacket

        override fun handle(
            packet: Packet<*>, ci: CallbackInfo
        ) {
            require(packet is ClientboundCommandSuggestionsPacket)
            val task = tasks.remove(packet.id) ?: return
            ci.cancel()
            task.processSuggestions(packet.suggestions.map { it.text })
        }
    }

    private fun sendPacket(id: Int, command: String) {
        val connection = minecraft().connection ?: return
        connection.send(ServerboundCommandSuggestionPacket(id, command))
    }
}