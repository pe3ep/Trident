package cc.pe3epwithyou.trident.modrinth

import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.TridentFont
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object UpdateChecker {
    private var currentVersion: Version? = null
    private const val PROJECT_ID = "L6RCcsrd"
    private const val MOD_ID = "trident"

    private val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()
    private val JSON = Json.Default

    fun init() {
        val container = FabricLoader.getInstance().getModContainer(MOD_ID)
        container.ifPresent { modContainer ->
            currentVersion = modContainer.metadata.version
        }
    }

    fun checkForUpdates() {
        val background = Util.backgroundExecutor().asCoroutineDispatcher()
        CoroutineScope(background).launch {
            val req =
                HttpRequest.newBuilder().uri(URI.create("https://api.modrinth.com/v2/project/$PROJECT_ID/version"))
                    .GET().setHeader("User-Agent", "trident-mc-mod").build()
            val body = client.sendAsync(req, HttpResponse.BodyHandlers.ofString()).await().body()
            Minecraft.getInstance().execute {
                handleResponse(body)
            }
        }
    }

    private fun handleResponse(response: String) {
        val versions = JSON.decodeFromString<List<VersionResponseSchema.ModrinthVersion>>(response)
        var fetchedVersion: Version? = null
        if (currentVersion == null) {
            ChatUtils.error("Missing current version")
            return
        }

        for (version in versions) {
            if (version.version_type != "release") continue
            fetchedVersion = Version.parse(version.version_number)
            break
        }
        if (fetchedVersion == null) return
        ChatUtils.info("Current version: ${currentVersion?.friendlyString}")
        ChatUtils.info("Fetched version: ${fetchedVersion.friendlyString}")
        if (fetchedVersion > currentVersion) {
            // New version available, notify the user
            sendUpdateAvailableMessage(fetchedVersion.friendlyString)
        }
    }

    private fun sendUpdateAvailableMessage(new: String) {
        val component = Component.literal("New Trident version available: ").withColor(TridentFont.TRIDENT_COLOR)
            .append(Component.literal(currentVersion?.friendlyString ?: "Unknown").withColor(TridentFont.TRIDENT_COLOR))
            .append(Component.literal(" -> ").withColor(TridentFont.TRIDENT_COLOR))
            .append(Component.literal(new).withColor(TridentFont.TRIDENT_ACCENT)).append(
                Component.literal("\nClick here to download the latest version").withStyle(
                        Style.EMPTY.withColor(TridentFont.TRIDENT_ACCENT).withUnderlined(true)
                            .withClickEvent(ClickEvent.OpenUrl(URI.create("https://modrinth.com/mod/$PROJECT_ID/version/$new")))
                    )
            )
        ChatUtils.sendMessage(component, true)
    }

}