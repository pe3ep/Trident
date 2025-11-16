package cc.pe3epwithyou.trident.feature.exchange

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant

object ExchangeLookup {
    var exchangeLookupCache: ExchangeListingsResponse? = null
    var exchangeLookupCacheExpiresIn: Long? = null

    private val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()
    private val json = Json { ignoreUnknownKeys = true }

    fun clearCache() {
        exchangeLookupCache = null
        exchangeLookupCacheExpiresIn = null
    }

    fun lookup() {
        if (!Config.Global.exchangeImprovements) return
        val context = Util.backgroundExecutor().asCoroutineDispatcher()
        val player = Minecraft.getInstance().gameProfile
        val key = Config.Api.key
        if (key.isBlank()) {
            ChatUtils.sendMessage(
                Component.literal("Your API key is not set. ").withSwatch(TridentFont.ERROR).append(
                        Component.literal("Set it using /trident api setToken <TOKEN>")
                            .withSwatch(TridentFont.ERROR, TridentFont.SwatchType.MUTED)
                    )
            )
            ExchangeHandler.fetchingProgress = ExchangeHandler.FetchProgress.FAILED
            return
        }

        val graphQLString = """
            query activeExchangeListings {
              player(uuid: \"${player.id}\") {
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

        CoroutineScope(context).launch {
            val req = HttpRequest.newBuilder().uri(URI.create("https://api.mccisland.net/graphql")).POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                        {"query":"$graphQLString"}
                    """.trimIndent()
                )
            ).setHeader("Content-Type", "application/json").setHeader("User-Agent", "trident-mc-mod/${player.name}")
                .setHeader("X-API-Key", key).build()

            try {
                val responseText = client.sendAsync(req, HttpResponse.BodyHandlers.ofString()).await().body()
                val listingsResponse = json.decodeFromString<ExchangeListingsResponse>(responseText)
                ExchangeHandler.fetchingProgress = ExchangeHandler.FetchProgress.COMPLETED
                Minecraft.getInstance().execute {
                    exchangeLookupCache = listingsResponse
                    val expiresIn = Instant.now().toEpochMilli() + Duration.ofSeconds(60).toMillis()
                    exchangeLookupCacheExpiresIn = expiresIn
                    ExchangeHandler.updatePrices()
                    ExchangeHandler.updateCosmetics()
                }
            } catch (e: Exception) {
                ExchangeHandler.fetchingProgress = ExchangeHandler.FetchProgress.FAILED
                Minecraft.getInstance().execute {
                    ChatUtils.error("Failed to fetch exchange API: ${e.message}")
                    ChatUtils.sendMessage(
                        Component.literal("Something went wrong when fetching Exchange API. Please contact developers to fix this issue")
                            .withStyle(TridentFont.ERROR.baseStyle)
                    )
                }
            }
        }
    }
}