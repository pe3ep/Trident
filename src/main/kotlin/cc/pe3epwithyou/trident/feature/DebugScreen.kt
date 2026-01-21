package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.api.ApiProvider.TRIDENT
import cc.pe3epwithyou.trident.utils.Resources.trident
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer
import net.minecraft.client.gui.components.debug.DebugScreenEntry
import net.minecraft.resources.Identifier
import net.minecraft.util.Util
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.LevelChunk
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object DebugScreen {
    private var customMessage: String? = null

    private val JSON = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Serializable
    data class DebugResponse(
        val success: Boolean,
        val hasMessage: Boolean,
        val message: String? = null
    )

    private val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .version(HttpClient.Version.HTTP_1_1)
        .executor(Util.nonCriticalIoPool())
        .build()


    fun fetchMessages() {
        if (!Config.Global.callToHome) return
        val player = Minecraft.getInstance().gameProfile

        val req =
            HttpRequest.newBuilder()
                .uri(URI.create("${TRIDENT.fetchUrl}/debug-screen?for=${player.id}"))
                .GET().setHeader("Content-Type", "application/json")
                .setHeader("User-Agent", "trident-mc-mod/${player.name}")

        client.sendAsync(
            req.build(),
            HttpResponse.BodyHandlers.ofString()
        )
            .thenAccept {
                val response = JSON.decodeFromString<DebugResponse>(it.body())
                customMessage = if (response.success && response.hasMessage) {
                    response.message
                } else {
                    null
                }
            }
            .exceptionally {
                Trident.LOGGER.error("[Trident] Failed to fetch debug screen: ", it)
                return@exceptionally null
            }
    }

    fun getMessage(): String {
        if (Trident.playerState.hatesUpdates) {
            return "i CANNOT BELIEVE you hate the cat..."
        }
        return customMessage ?: "Thank you for using Trident <3"
    }
}

class TridentDebug : DebugScreenEntry {
    val GROUP: Identifier = trident("debug_group")

    override fun display(
        displayer: DebugScreenDisplayer,
        serverOrClientLevel: Level?,
        clientChunk: LevelChunk?,
        serverChunk: LevelChunk?
    ) {

        val container = FabricLoader.getInstance().getModContainer("trident")
        val currentVersion = container.get().metadata.version

        displayer.addToGroup(
            GROUP,
            listOf(
                String.format(
                    "%s[Trident]%s %s",
                    ChatFormatting.AQUA,
                    ChatFormatting.RESET,
                    currentVersion
                ), String.format(
                    "%s[Trident]%s %s",
                    ChatFormatting.AQUA,
                    ChatFormatting.RESET,
                    DebugScreen.getMessage()
                )
            )
        )
    }

    override fun isAllowed(reducedDebugInfo: Boolean): Boolean {
        return true
    }
}