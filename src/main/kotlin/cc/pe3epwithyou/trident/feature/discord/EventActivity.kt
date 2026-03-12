package cc.pe3epwithyou.trident.feature.discord

import cc.pe3epwithyou.trident.feature.api.ApiProvider
import cc.pe3epwithyou.trident.utils.NetworkUtil
import cc.pe3epwithyou.trident.utils.RequestMethod
import kotlinx.serialization.Serializable

object EventActivity {
    var fetchedActivities: List<EventActivitiesResponseItem>? = null

    fun fetchEventActivities() {
        NetworkUtil.sendRequest<List<EventActivitiesResponseItem>>(
            RequestMethod.GET, ApiProvider.TRIDENT.fetchUrl + "/getEventActivities",
        ) {
            onSuccess {
                fetchedActivities = it
            }
        }
    }
}

@Serializable
data class EventActivitiesResponseItem(
    val hideInAutoPrivateMode: Boolean,
    val showEffectBar: Boolean,
    val noxesiumServer: NoxesiumServer,
    val rpc: Rpc,
)

@Serializable
data class NoxesiumServer(
    val server: String,
    val types: List<String>,
)

@Serializable
data class Rpc(
    val state: String,
    val details: String,
    val largeImage: String,
)
