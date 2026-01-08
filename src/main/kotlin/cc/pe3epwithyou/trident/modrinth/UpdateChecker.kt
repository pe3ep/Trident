package cc.pe3epwithyou.trident.modrinth

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.updatechecker.DisappointedCatDialog
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.Util
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant

object UpdateChecker {
    var currentVersion: Version? = null
    private const val PROJECT_ID = "L6RCcsrd"
    private const val MOD_ID = "trident"

    private val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).executor(
        Util.nonCriticalIoPool()
    ).build()

    private val JSON = Json {
        ignoreUnknownKeys = true
    }

    var latestVersion: Version? = null

    fun init() {
        val container = FabricLoader.getInstance().getModContainer(MOD_ID)
        container.ifPresent { modContainer ->
            currentVersion = modContainer.metadata.version
        }
    }

    fun checkForUpdates() {
        val req = HttpRequest.newBuilder()
            .uri(URI.create("https://api.modrinth.com/v2/project/$PROJECT_ID/version")).GET()
            .setHeader("User-Agent", "trident-mc-mod").build()

        client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
            .thenAccept {
                Minecraft.getInstance().execute { handleResponse(it.body()) }
            }
            .exceptionally {
                Trident.LOGGER.error("[Trident] Error occurred when checking for updates ", it)
                return@exceptionally null
            }
    }

    private fun handleResponse(response: String) {
        val versions = JSON.decodeFromString<List<VersionResponseSchema.ModrinthVersion>>(response)
        var fetchedVersionModrinth: VersionResponseSchema.ModrinthVersion? = null
        if (currentVersion == null) {
            Logger.error("Missing current version")
            return
        }

        for (version in versions) {
            if (version.version_type != "release") continue
            latestVersion = Version.parse(version.version_number)
            fetchedVersionModrinth = version
            break
        }
        if (latestVersion == null) return
        Logger.info("Current version: ${currentVersion?.friendlyString}")
        Logger.info("Fetched version: ${latestVersion!!.friendlyString}")
        if (latestVersion!! > currentVersion) {
            // New version available, notify the user
            sendUpdateAvailableMessage(latestVersion!!.friendlyString)

            if (Trident.playerState.hatesUpdates) return
            val published = Instant.parse(fetchedVersionModrinth?.date_published ?: return)
            val now = Instant.now()
            val activateSillyDate = published.plus(Duration.ofDays(7L))
            if (!now.isBefore(activateSillyDate)) {
                DelayedAction.delayTicks(100) {
                    val key = "grumpycat"
                    DialogCollection.open(key, DisappointedCatDialog(10, 10, key))
                }
            }
        }
    }

    private fun sendUpdateAvailableMessage(new: String) {
        val component = Component.literal("New Trident version available: ")
            .withSwatch(TridentFont.TRIDENT_COLOR).append(
                Component.literal(currentVersion?.friendlyString ?: "Unknown")
                    .withSwatch(TridentFont.TRIDENT_COLOR)
            ).append(Component.literal(" -> ").withSwatch(TridentFont.TRIDENT_COLOR))
            .append(Component.literal(new).withSwatch(TridentFont.TRIDENT_ACCENT)).append(
                Component.literal("\nClick here to download the latest version")
                    .withSwatch(TridentFont.TRIDENT_ACCENT).withStyle(
                        Style.EMPTY.withUnderlined(true)
                            .withClickEvent(ClickEvent.OpenUrl(URI.create("https://modrinth.com/mod/$PROJECT_ID/version/$new")))
                    )
            )
        Logger.sendMessage(component, true)
    }

}