package cc.pe3epwithyou.trident.feature.api

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import cc.pe3epwithyou.trident.utils.extensions.CoroutineScopeExt.main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.network.chat.Component
import net.minecraft.util.Util
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object ApiChecker {
    private val JSON = Json { ignoreUnknownKeys = true }

    @Serializable
    data class PingResponse(
        val success: Boolean
    )

    fun joinCheck() {
        if (!Config.Global.callToHome) return
        val ctx = Util.backgroundExecutor().asCoroutineDispatcher()
        CoroutineScope(ctx).launch {
            val ping = pingApi()
            if (!ping && Config.Global.apiProvider == ApiProvider.TRIDENT) {
                main {
                    Logger.sendMessage(
                        Component.literal("Trident API is down. Switching to self-hosted token.")
                            .withSwatch(
                                TridentFont.ERROR
                            )
                    )
                    Config.handler.instance().globalApiProvider = ApiProvider.SELF_TOKEN
                    Config.handler.save()
                }
            }
        }
    }

    suspend fun pingApi(): Boolean {
        val client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(3))
            .build()

        val request = HttpRequest.newBuilder()
            .uri(URI.create("${ApiProvider.TRIDENT.fetchUrl}/ping"))
            .timeout(Duration.ofSeconds(3))
            .GET()
            .header("Accept", "application/json")
            .build()

        return try {
            val ctx = Util.backgroundExecutor().asCoroutineDispatcher()
            val response = withContext(ctx) {
                client.send(request, HttpResponse.BodyHandlers.ofString())
            }
            val body = response.body()

            val parsed = JSON.decodeFromString<PingResponse>(body)
            return parsed.success
        } catch (_: Exception) {
            false
        }
    }
}