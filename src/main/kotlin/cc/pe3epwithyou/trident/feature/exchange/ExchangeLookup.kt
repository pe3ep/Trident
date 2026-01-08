package cc.pe3epwithyou.trident.feature.exchange

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.api.ApiProvider
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.Util
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant

object ExchangeLookup {
    var exchangeLookupCache: ExchangeListingsResponse? = null
    var exchangeLookupCacheExpiresIn: Long? = null

    private val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .version(HttpClient.Version.HTTP_1_1)
        .executor(Util.nonCriticalIoPool())
        .build()

    private val JSON = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    @Serializable
    data class GraphQLRequest(
        val query: String
    )

    fun clearCache() {
        exchangeLookupCache = null
        exchangeLookupCacheExpiresIn = null
    }

    fun lookup() {
        if (!Config.Global.exchangeImprovements) return
        val player = Minecraft.getInstance().gameProfile
        val provider = Config.Global.apiProvider
        val key = Config.Api.key

        if (key.isBlank() && provider == ApiProvider.SELF_TOKEN) {
            Logger.sendMessage(
                Component.literal("Your API key is not set. ").withSwatch(TridentFont.ERROR).append(
                    Component.literal("Set it using /trident api setToken <TOKEN>")
                        .withSwatch(TridentFont.ERROR, TridentFont.SwatchType.MUTED)
                )
            )
            Logger.sendMessage(
                Component.literal("Click here to visit Trident Docs learn how to get your API key")
                    .withSwatch(TridentFont.TRIDENT_ACCENT)
                    .withStyle(
                        Style.EMPTY.withUnderlined(true)
                            .withClickEvent(ClickEvent.OpenUrl(URI.create("https://trident.pe3epwithyou.cc/docs/setting-up-api#bringing-your-own-token")))
                    )
            )
            ExchangeHandler.fetchingProgress = ExchangeHandler.FetchProgress.FAILED
            return
        }

        val graphQLString = """
            query activeExchangeListings {
                player(uuid: "${player.id}") {
                    collections {
                        cosmetics {
                            owned
                            cosmetic {
                                name
                            }
                        }
                    }
                }
                activeIslandExchangeListings {
                    asset {
                        name
                    }
                    cost
                    amount
                }
            }
        """.trimIndent()

        val payload = GraphQLRequest(query = graphQLString)
        val jsonPayload = JSON.encodeToString(GraphQLRequest.serializer(), payload)

        val req = HttpRequest.newBuilder().uri(URI.create(provider.fetchUrl)).POST(
            HttpRequest.BodyPublishers.ofString(jsonPayload)
        ).setHeader("Content-Type", "application/json")
            .setHeader("User-Agent", "trident-mc-mod/${player.name}")


        when (provider) {
            ApiProvider.TRIDENT -> req.setHeader("x-mc-uuid", player.id.toString())
            ApiProvider.SELF_TOKEN -> req.setHeader("X-API-Key", key)
        }


        client.sendAsync(req.build(), HttpResponse.BodyHandlers.ofString())
            .thenAccept {
                val listingsResponse = JSON.decodeFromString<ExchangeListingsResponse>(it.body())
                ExchangeHandler.fetchingProgress = ExchangeHandler.FetchProgress.COMPLETED
                exchangeLookupCache = listingsResponse
                val expiresIn = Instant.now().toEpochMilli() + Duration.ofSeconds(60).toMillis()
                exchangeLookupCacheExpiresIn = expiresIn
                ExchangeHandler.updatePrices()
                ExchangeHandler.updateCosmetics()
            }
            .exceptionally {
                ExchangeHandler.fetchingProgress = ExchangeHandler.FetchProgress.FAILED
                Logger.error("Failed to fetch exchange API on url ${provider.fetchUrl}: ${it.message}")
                Logger.sendMessage(
                    Component.literal("Something went wrong when fetching Exchange API. Please contact developers to fix this issue")
                        .withSwatch(TridentFont.ERROR)
                )

                return@exceptionally null
            }


    }
}