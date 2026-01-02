package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.feature.api.ApiProvider.TRIDENT
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.extensions.CoroutineScopeExt.main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraft.util.Util
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.*

object DebugScreen {
    private var messages: Map<UUID, String> = emptyMap()

    private val JSON = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    @Serializable
    data class DebugResponse(val id: String, val message: String)

    private val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10))
        .version(HttpClient.Version.HTTP_1_1).build()


    fun fetchMessages() {
        val ctx = Util.backgroundExecutor().asCoroutineDispatcher()
        CoroutineScope(ctx).launch {
            val player = Minecraft.getInstance().gameProfile.name
            val req =
                HttpRequest.newBuilder().uri(URI.create("${TRIDENT.fetchUrl}/debug-screen"))
                    .GET().setHeader("Content-Type", "application/json")
                    .setHeader("User-Agent", "trident-mc-mod/${player}")

            try {
                val responseText =
                    client.sendAsync(req.build(), HttpResponse.BodyHandlers.ofString()).await()
                        .body()
                val response = JSON.decodeFromString<List<DebugResponse>>(responseText)
                main {
                    response.forEach { messages += UUID.fromString(it.id) to it.message }
                }
            } catch (e: Exception) {
                main {
                    Logger.error("Failed to fetch debug screen: ${e.message}")
                }
            }
        }
    }

    fun getMessage(): String {
        val playerId = Minecraft.getInstance().gameProfile.id
        if (Trident.playerState.hatesUpdates) {
            return "i CANNOT BELIEVE you hate the cat..."
        }
        return messages[playerId] ?: "Thank you for using Trident"
    }
}