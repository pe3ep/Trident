package cc.pe3epwithyou.trident.feature.debug

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.api.ApiProvider.TRIDENT
import cc.pe3epwithyou.trident.utils.NetworkUtil
import cc.pe3epwithyou.trident.utils.RequestMethod
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft

object DebugScreen {
    private var customMessage: String? = null

    @Serializable
    data class DebugResponse(
        val success: Boolean, val hasMessage: Boolean, val message: String? = null
    )

    fun fetchMessages() {
        if (!Config.Global.callToHome) return
        val player = Minecraft.getInstance().gameProfile

        NetworkUtil.sendRequest<DebugResponse>(
            RequestMethod.GET,
            "${TRIDENT.fetchUrl}/debug-screen?for=${player.id}"
        ) {
            onSuccess { response ->
                customMessage = response.message.takeIf { response.success && response.hasMessage }
            }

            onError { _, throwable ->
                Trident.LOGGER.error(
                    "Failed to fetch debug screen: ",
                    throwable
                )
            }
        }
    }

    fun getMessage(): String {
        if (Trident.playerState.hatesUpdates) {
            return "i CANNOT BELIEVE you hate the cat..."
        }
        return customMessage ?: "Thank you for using Trident <3"
    }
}
