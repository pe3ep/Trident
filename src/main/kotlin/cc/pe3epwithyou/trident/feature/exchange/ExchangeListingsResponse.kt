package cc.pe3epwithyou.trident.feature.exchange

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeListingsResponse(
    val data: ExchangeListings
)

@Serializable
data class ExchangeListings(
    val activeIslandExchangeListings: List<ActiveIslandExchangeListingsItem>
)

@Serializable
data class ActiveIslandExchangeListingsItem(
    val cost: Long, val asset: ExchangeAsset, val amount: Int
)

@Serializable
data class ExchangeAsset(
    val name: String
)
