package cc.pe3epwithyou.trident.utils

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraft.util.Util
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

enum class RequestMethod {
    GET, POST
}

object NetworkUtil {
    val client: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .version(HttpClient.Version.HTTP_1_1)
        .executor(Util.nonCriticalIoPool())
        .build()

    val JSON = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    class Request<T> {
        var successHandler: ((T) -> Unit) = {}
        var errorHandler: ((String, Throwable) -> Unit) = { url, throwable ->
            Logger.error(
                "Error occurred when sending a request to $url:",
                throwable
            )
        }

        fun onSuccess(block: (T) -> Unit) {
            successHandler = block
        }

        fun onError(block: (String, Throwable) -> Unit) {
            errorHandler = block
        }
    }

    @Serializable
    data class GraphQLRequest(
        val query: String
    )

    inline fun <reified T> sendGraphQL(
        url: String,
        body: String,
        headers: Map<String, String> = emptyMap(),
        noinline block: Request<T>.() -> Unit
    ) = sendRequest<T>(
        RequestMethod.POST,
        url,
        JSON.encodeToString(GraphQLRequest(body)),
        headers,
        block
    )

    inline fun <reified T> sendRequest(
        method: RequestMethod = RequestMethod.POST,
        url: String,
        body: String? = null,
        headers: Map<String, String> = emptyMap(),
        noinline block: Request<T>.() -> Unit
    ) {
        val request = Request<T>().apply(block)

        val player = Minecraft.getInstance().player
        val req = HttpRequest.newBuilder().uri(URI.create(url))

        when (method) {
            RequestMethod.GET -> req.GET()
            RequestMethod.POST -> {
                if (body == null) throw IllegalArgumentException("POST request body cannot be null")
                req.POST(HttpRequest.BodyPublishers.ofString(body))
            }
        }

        headers.forEach { (header, value) ->
            req.setHeader(header, value)
        }
        req.setHeader("Content-Type", "application/json")
        req.setHeader(
            "User-Agent",
            if (player != null) "trident-mc-mod/${player.name}" else "trident-mc-mod"
        )

        client.sendAsync(req.build(), HttpResponse.BodyHandlers.ofString())
            .thenAccept {
                Logger.debugLog("${it.body()}")
                val res = JSON.decodeFromString<T>(it.body())
                Minecraft.getInstance().execute {
                    request.successHandler.invoke(res)
                }
            }
            .exceptionally {
                Minecraft.getInstance().execute { request.errorHandler.invoke(url, it) }
                null
            }
    }
}