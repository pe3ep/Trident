package cc.pe3epwithyou.trident.feature.api

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.NetworkUtil
import cc.pe3epwithyou.trident.utils.RequestMethod
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import kotlinx.serialization.Serializable
import net.minecraft.network.chat.Component

object ApiChecker {
    @Serializable
    data class PingResponse(
        val success: Boolean
    )

    fun joinCheck() {
        if (!Config.Global.callToHome) return
        NetworkUtil.sendRequest<PingResponse>(RequestMethod.GET, "${ApiProvider.TRIDENT.fetchUrl}/ping") {
            onSuccess { _ -> }
            onError { _, throwable ->
                if (Config.Global.apiProvider == ApiProvider.TRIDENT) {
                    Logger.error("Failed to ping Trident API", throwable)
                    Logger.sendMessage(
                        Component.literal("Trident API is down. Switching to self-hosted token.")
                            .withSwatch(
                                TridentFont.ERROR
                            )
                    )
                    Config.handler.instance().globalApiProvider = ApiProvider.SELF_TOKEN
                    Config.handler.save()
                }
            }
        }
    }
}