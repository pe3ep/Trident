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

object DebugScreen {
    private var customMessage: String? = null

    private val JSON = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Serializable
    data class DebugResponse(val success: Boolean, val hasMessage: Boolean, val message: String? = null)

    private val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10))
        .version(HttpClient.Version.HTTP_1_1).build()


    fun fetchMessages() {
        val ctx = Util.backgroundExecutor().asCoroutineDispatcher()
        CoroutineScope(ctx).launch {
            val player = Minecraft.getInstance().gameProfile
            val req =
                HttpRequest.newBuilder().uri(URI.create("${TRIDENT.fetchUrl}/debug-screen?for=${player.id}"))
                    .GET().setHeader("Content-Type", "application/json")
                    .setHeader("User-Agent", "trident-mc-mod/${player.name}")

            try {
                val responseText =
                    client.sendAsync(req.build(), HttpResponse.BodyHandlers.ofString()).await()
                        .body()
                val response = JSON.decodeFromString<DebugResponse>(responseText)
                main {
                    customMessage = if (response.success && response.hasMessage) {
                        response.message
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                main {
                    Logger.error("Failed to fetch debug screen: ${e.message}")
                }
            }
        }
    }

    fun getMessage(): String {
        if (Trident.playerState.hatesUpdates) {
            return "i CANNOT BELIEVE you hate the cat..."
        }
        return customMessage ?: "Thank you for using Trident <3"
    }
}