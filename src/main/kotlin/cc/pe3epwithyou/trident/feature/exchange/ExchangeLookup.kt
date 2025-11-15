package cc.pe3epwithyou.trident.feature.exchange

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.TridentFont
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

object ExchangeLookup {
    var responseCache: ExchangeListingsResponse? = null // TODO: Implement invalidation and use the cache LOL
    private val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()
    private val json = Json { ignoreUnknownKeys = true }

    fun lookup() {
        val context = Util.backgroundExecutor().asCoroutineDispatcher()
        val player = Minecraft.getInstance().player?.name?.string
        val key = Config.Api.key
        if (key.isBlank()) {
            ChatUtils.sendMessage(
                Component.literal("Your API key is not set. Set it using /trident api setToken <TOKEN>")
                    .withStyle(TridentFont.ERROR.baseStyle)
            )
            return
        }

        val graphQLString = """
            query activeExchangeListings {
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
            ).setHeader("Content-Type", "application/json").setHeader("User-Agent", "trident-mc-mod/$player")
                .setHeader("X-API-Key", key).build()

            val responseText = client.sendAsync(req, HttpResponse.BodyHandlers.ofString()).await().body()
            val listingsResponse = json.decodeFromString<ExchangeListingsResponse>(responseText)
            val firstListing = listingsResponse.data.activeIslandExchangeListings.first()
            responseCache = listingsResponse
            Minecraft.getInstance().execute {
                ChatUtils.sendMessage(
                    """
                        Listing: ${firstListing.asset.name}
                        Amount: ${firstListing.amount}
                        Price: ${firstListing.cost}
                    """.trimIndent()
                )
            }
        }
    }
}