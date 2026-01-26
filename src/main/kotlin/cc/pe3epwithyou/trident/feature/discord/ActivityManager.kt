package cc.pe3epwithyou.trident.feature.discord

import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.SuggestionPacket
import io.github.vyfor.kpresence.rpc.ActivityAssetsBuilder
import io.github.vyfor.kpresence.rpc.ActivityBuilder
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Component
import java.time.Instant

object ActivityManager {
    private var currentActivityBuilder: ActivityBuilder? = null

    private fun activityBuilder(block: ActivityBuilder.() -> Unit) = ActivityBuilder().apply(block)

    private fun defaultActivity(): ActivityBuilder {
        val container = FabricLoader.getInstance().getModContainer("trident")
        var version = "Unknown"
        container.ifPresent { modContainer ->
            version = modContainer.metadata.version.friendlyString
        }

        val now = Instant.now().epochSecond

        val builder = activityBuilder {
            details = "Playing MCC Island"
            assets {
                largeImage = "game_hub"
                smallImage = "trident"
                smallText = "Trident $version"
            }

            timestamps {
                start = now
            }
            button("Download Trident", "https://trident.pe3epwithyou.cc")
        }

        currentActivityBuilder = builder
        return builder
    }

    private fun getActivity(): ActivityBuilder = currentActivityBuilder ?: defaultActivity()

    fun hideActivity() {
        currentActivityBuilder = null
        IPCManager.submitBuilder(null)
    }

    fun updateCurrentActivity() {
        val activity = getActivity()
        activity.state = null
        val game = MCCIState.game
        activity.details = "Playing ${game.title}"
        val assetsBuilder = ActivityAssetsBuilder()
        assetsBuilder.largeImage = "game_${game.name.lowercase()}"
        assetsBuilder.smallImage = activity.assets?.smallImage
        assetsBuilder.smallText = activity.assets?.smallText


        MCCIState.gameState?.let {
            if (it.stage == "podiumphase") {
                activity.state = "Game over"
                return@let
            }

            if (it.totalRounds > 1) {
                activity.state = "${it.mapName} - Round ${it.round + 1} of ${it.totalRounds}"
            } else if (it.mapName.length > 2) {
                activity.state = "${it.mapName}"
            }
        }
        when (game) {
            Game.HUB -> {
                activity.details = "In the Hub"
                activity.state = null
                assetsBuilder.largeImage = "game_hub"
                if (MCCIState.lobbyGame != Game.HUB) {
                    activity.state = "${MCCIState.lobbyGame.title} lobby"
                }
            }

            Game.FISHING -> {
                val island = MCCIState.fishingState.island ?: "temperate_1"
                activity.state = null

                val islandName = when (island) {
                    "temperate_1" -> "Verdant Woods"
                    "temperate_2" -> "Floral Forest"
                    "temperate_3" -> "Dark Grove"
                    "temperate_grotto" -> "Sunken Swamp"

                    "tropical_1" -> "Tropical Overgrowth"
                    "tropical_2" -> "Coral Shores"
                    "tropical_3" -> "Twisted Swamp"
                    "tropical_grotto" -> "Mirrored Oasis"

                    "barren_1" -> "Ancient Sands"
                    "barren_2" -> "Blazing Canyon"
                    "barren_3" -> "Ashen Wastes"
                    "barren_grotto" -> "Volcanic Springs"

                    else -> "Unknown Island"
                }

                activity.details = "Fishing at $islandName"
                assetsBuilder.largeImage = "game_fishing_$island"
            }

            Game.BATTLE_BOX_ARENA -> {
                MCCIState.gameState?.let {
                    if (it.stage == "podiumphase") return@let
                    activity.state = "${it.mapName} - Round ${it.round + 1}"
                }

                assetsBuilder.largeImage = "game_battle_box_arena"
            }

            Game.PARKOUR_WARRIOR_DOJO -> {
                Logger.debugLog("gametypes: ${MCCIState.gameTypes}")
                MCCIState.gameTypes.find { Regex("""(main-\d|daily)""").matches(it) }?.let {
                    Logger.debugLog("Found course type: $it")
                    if (it == "daily") {
                        activity.state = "Daily Challenge"
                    } else {
                        val challenge = it.split("-").getOrNull(1)?.toIntOrNull() ?: return@let
                        activity.state = "Course #$challenge"
                    }
                }
                assetsBuilder.largeImage = "game_parkour_warrior_dojo"
            }

            else -> {}
        }

        activity.assets = assetsBuilder.build()

        currentActivityBuilder = activity
        Party.request()
    }

    fun sendWithParty() {
        val activity = getActivity()
        var size = Party.size
        if (size == 1) size = null
        if (size == null) {
            activity.party = null
        } else {
            activity.party {
                currentSize = size
                maxSize = 4
            }
        }

        currentActivityBuilder = activity
        IPCManager.submitBuilder(currentActivityBuilder)
    }

    object Party {
        var previousSize: Int? = null
        var size: Int? = null

        fun request() = SuggestionPacket.requestSuggestions("/party kick ", ::updateSize)

        fun updateSize(suggestions: List<String>) {
            val suggestionCount = suggestions.size
            Logger.debugLog("Party size: ${suggestionCount + 1}")

            updatePartySize(suggestionCount + 1)
        }

        fun updatePartySize(receivedSize: Int?) {
            val newSize = receivedSize?.coerceAtLeast(1)
            previousSize = size
            size = if (newSize == 1) null else newSize
            sendWithParty()
        }

        fun handleChatMessage(message: Component) {
            val str = message.string

            Regex("""(has joined|has been removed|have been removed from|has left|leaves|left|have joined) the party""").find(str)?.let {
                if ("You left" in str || "You have been removed" in str) {
                    updatePartySize(null)
                    return
                }
                request()
            }
        }
    }
}
