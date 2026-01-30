package cc.pe3epwithyou.trident.feature.exchange

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.api.ApiProvider
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.NetworkUtil
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import java.net.URI
import java.time.Duration
import java.time.Instant

object ExchangeLookup {
    var exchangeLookupCache: ExchangeListingsResponse? = null
    var exchangeLookupCacheExpiresIn: Long? = null

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

        val headers = mutableMapOf<String, String>()

        when (provider) {
            ApiProvider.TRIDENT -> headers["x-mc-uuid"] = player.id.toString()
            ApiProvider.SELF_TOKEN -> headers["X-API-Key"] = key
        }

        NetworkUtil.sendGraphQL<ExchangeListingsResponse>(provider.fetchUrl, graphQLString, headers) {
            onSuccess { listingsResponse ->
                ExchangeHandler.fetchingProgress = ExchangeHandler.FetchProgress.COMPLETED
                exchangeLookupCache = listingsResponse
                val expiresIn = Instant.now().toEpochMilli() + Duration.ofSeconds(60).toMillis()
                exchangeLookupCacheExpiresIn = expiresIn
                ExchangeHandler.updatePrices()
                ExchangeHandler.updateCosmetics()
            }

            onError { _, throwable ->
                Logger.error("Something went wrong when fetching Exchange API", throwable)
                ExchangeHandler.fetchingProgress = ExchangeHandler.FetchProgress.FAILED
                Logger.sendMessage(
                    Component.literal("Something went wrong when fetching Exchange API. Please contact developers to fix this issue")
                        .withSwatch(TridentFont.ERROR)
                )
            }
        }
    }
}