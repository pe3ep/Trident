package cc.pe3epwithyou.trident.feature.exchange

import kotlinx.serialization.Serializable

@Serializable
data class CosmeticOwnershipPlayer(
    val collections: Collections? = null
)

@Serializable
data class Collections(
    val cosmetics: List<CosmeticsItem>
)

@Serializable
data class CosmeticsItem(
    val owned: Boolean, val cosmetic: Cosmetic
)

@Serializable
data class Cosmetic(
    val name: String
)
