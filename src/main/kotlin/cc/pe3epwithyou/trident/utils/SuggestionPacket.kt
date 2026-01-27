package cc.pe3epwithyou.trident.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.concurrent.ConcurrentHashMap

object SuggestionPacket {
    val tasks: ConcurrentHashMap<Int, SuggestionTask> = ConcurrentHashMap()

    data class SuggestionTask(val id: Int, val callback: (List<String>) -> Unit) {
        fun handle(strings: List<String>) = Minecraft.getInstance().execute { callback(strings) }
    }

    fun requestSuggestions(command: String, callback: (List<String>) -> Unit) {
        val id: Int = command.hashCode()

        val task = SuggestionTask(id, callback)
        tasks[id] = task
        sendPacket(id, command)
        CoroutineScope(Dispatchers.IO).launch {
            delay(1_500)
            val task = tasks.remove(id) ?: return@launch
            Logger.debugLog("Failed to get suggestions for command $command")
            task.handle(emptyList())
        }
    }

    fun handlePacket(packet: Packet<*>, ci: CallbackInfo) {
        if (packet !is ClientboundCommandSuggestionsPacket) return
        val task = tasks.remove(packet.id) ?: return
        ci.cancel()
        task.handle(packet.suggestions.map { it.text })
    }

    private fun sendPacket(id: Int, command: String) {
        val connection = Minecraft.getInstance().connection ?: return
        connection.send(ServerboundCommandSuggestionPacket(id, command))
    }
}