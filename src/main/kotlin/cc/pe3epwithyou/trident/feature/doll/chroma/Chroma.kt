package cc.pe3epwithyou.trident.feature.doll.chroma

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.api.ApiProvider
import cc.pe3epwithyou.trident.utils.NetworkUtil
import cc.pe3epwithyou.trident.utils.RequestMethod
import cc.pe3epwithyou.trident.utils.Resources
import kotlinx.serialization.Serializable
import net.minecraft.resources.Identifier

object ChromaManger {
    var fetchedChromas: List<Chroma>? = null
    var maxCols: Int? = null

    fun fetchChromas() {
        if (!Config.Global.callToHome) return
        NetworkUtil.sendRequest<ChromaResponse>(RequestMethod.GET, ApiProvider.TRIDENT.fetchUrl + "/query/chromas") {
            onSuccess {
                fetchedChromas = it.chromas
                maxCols = it.maxCols
            }
            onError { _, throwable ->
                Trident.LOGGER.error("Failed to fetch chromas", throwable)
            }
        }
    }

    @Serializable
    data class ChromaResponse(val chromas: List<Chroma>, val maxCols: Int)
}

@Serializable
data class Chroma(
    val id: String,
    val displayName: String,
    val colors: List<Int>,
) {
    val itemTexture: Identifier
        get() = Resources.mcc("textures/island_items/infinibag/chroma_set/${this.id.lowercase()}.png")
}