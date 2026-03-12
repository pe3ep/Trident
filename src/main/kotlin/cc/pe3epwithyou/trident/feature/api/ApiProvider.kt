package cc.pe3epwithyou.trident.feature.api

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.exchange.ExchangeListingsResponse
import cc.pe3epwithyou.trident.state.PlayerDataResponse
import cc.pe3epwithyou.trident.utils.NetworkUtil
import cc.pe3epwithyou.trident.utils.RequestMethod
import cc.pe3epwithyou.trident.utils.minecraft
import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.network.chat.Component

enum class ApiProvider : NameableEnum {
    TRIDENT, SELF_TOKEN;

    override fun getDisplayName(): Component =
        Component.translatable("config.trident.global.api_provider.provider.${name.lowercase()}")

    val fetchUrl: String
        get() = when (this) {
            TRIDENT -> "https://api.pe3epwithyou.cc/trident/v2"
            SELF_TOKEN -> "https://api.mccisland.net/graphql"
        }

    fun queryExchangeListings(block: NetworkUtil.Request<ExchangeListingsResponse>.() -> Unit) {
        val player = minecraft().gameProfile
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

        when (this) {
            TRIDENT -> NetworkUtil.sendRequest<ExchangeListingsResponse>(
                RequestMethod.GET,
                "$fetchUrl/query/exchangeListings",
                headers = mapOf("x-mc-uuid" to player.id.toString()),
                block = block
            )


            SELF_TOKEN -> NetworkUtil.sendGraphQL<ExchangeListingsResponse>(
                fetchUrl,
                graphQLString,
                headers = mapOf("X-API-Key" to Config.Api.key),
                block = block
            )
        }
    }

    fun queryIslandData(block: NetworkUtil.Request<PlayerDataResponse>.() -> Unit) {
        val player = minecraft().gameProfile
        val graphQLString = $$"""
            query CrownLevel($uuid: UUID = "$${player.id}") {
              player(uuid: $uuid) {
                collections {
                  cosmetics {
                    owned
                    cosmetic {
                      name
                    }
                  }
                }
                crownLevel {
                  fishingLevelData {
                    evolution
                    level
                  }
                  levelData {
                    evolution
                    level
                  }
                  styleLevelData {
                    evolution
                    level
                  }
                }
              }
            }
        """

        when (this) {
            TRIDENT -> NetworkUtil.sendRequest<PlayerDataResponse>(
                RequestMethod.GET,
                "$fetchUrl/query/playerData",
                headers = mapOf("x-mc-uuid" to player.id.toString()),
                block = block
            )


            SELF_TOKEN -> NetworkUtil.sendGraphQL<PlayerDataResponse>(
                fetchUrl,
                graphQLString,
                headers = mapOf("X-API-Key" to Config.Api.key),
                block = block
            )
        }
    }
}