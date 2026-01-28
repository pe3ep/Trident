package cc.pe3epwithyou.trident.feature.discord

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.state.Rank
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.SuggestionPacket
import cc.pe3epwithyou.trident.utils.useScreen
import io.github.vyfor.kpresence.rpc.ActivityAssetsBuilder
import io.github.vyfor.kpresence.rpc.ActivityBuilder
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screens.inventory.ContainerScreen
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
        val container = FabricLoader.getInstance().getModContainer("trident")
        var version = "Unknown"
        container.ifPresent { modContainer ->
            version = modContainer.metadata.version.friendlyString
        }

        activity.details = null
        val game = MCCIState.game
        activity.state = game.title
        val assetsBuilder = ActivityAssetsBuilder()
        assetsBuilder.largeImage = "game_${game.name.lowercase()}"
        assetsBuilder.smallImage = "trident"
        assetsBuilder.smallText = "Trident $version"

        MCCIState.gameState?.let {
            if (it.stage == "podiumphase" || it.stage == "postgame") {
                activity.details = "Game Finished"
                return@let
            }

            if (it.totalRounds > 1) {
                activity.details = "${it.mapName} - Round ${it.round + 1} of ${it.totalRounds}"
            } else if (it.mapName.length > 2) {
                activity.details = "${it.mapName}"
            }
        }
        when (game) {
            Game.HUB -> {
                activity.state = "Hub"
                activity.details = null
                assetsBuilder.largeImage = "game_hub"
                if (MCCIState.lobbyGame != Game.HUB) {
                    activity.details = "${MCCIState.lobbyGame.title} lobby"
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
                    if (it.stage == "podiumphase" || it.stage == "postgame") return@let
                    activity.details = "${it.mapName} - Round ${it.round + 1}"
                }

                assetsBuilder.largeImage = "game_battle_box_arena"
                Trident.playerState.arenaData.currentRank?.let {
                    assetsBuilder.smallText = it.name
                    assetsBuilder.smallImage = it.image
                }

            }

            Game.PARKOUR_WARRIOR_DOJO -> {
                Logger.debugLog("gametypes: ${MCCIState.gameTypes}")
                MCCIState.gameTypes.find { Regex("""(main-\d|daily)""").matches(it) }?.let {
                    Logger.debugLog("Found course type: $it")
                    if (it == "daily") {
                        activity.details = "Daily Challenge"
                    } else {
                        val challenge = it.split("-").getOrNull(1)?.toIntOrNull() ?: return@let
                        activity.details = "Course #$challenge"
                    }
                }
                assetsBuilder.largeImage = "game_parkour_warrior_dojo"
            }

            else -> {}
        }

        activity.assets = assetsBuilder.build()

        if (!Config.Discord.displayExtraInfo) {
            activity.details = null
            if (game == Game.FISHING) {
                activity.details = "On a Fishing Island"
            }
        }

        currentActivityBuilder = activity
        Party.request()
    }

    fun sendWithParty() {
        val activity = getActivity()
        var size = Party.size
        if (size == 1) size = null
        if (size != null && Config.Discord.displayParty) {
            activity.party {
                currentSize = size
                maxSize = 4
            }
        } else {
            activity.party = null
        }

        currentActivityBuilder = activity
        IPCManager.submitBuilder(currentActivityBuilder)
    }

    object Party {
        var previousSize: Int? = null
        var size: Int? = null

        var members: List<String> = emptyList()

        fun request() = SuggestionPacket.requestSuggestions("/party kick ", ::updateSize)

        fun updateSize(suggestions: List<String>) {
            val suggestionCount = suggestions.size
            Logger.debugLog("Party size: ${suggestionCount + 1}")
            members = suggestions
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
                request()
            }
        }
    }

    object Arena {
        val knownRanks = listOf(
            "bronze_iii",
            "bronze_ii",
            "bronze_i",
            "silver_iii",
            "silver_ii",
            "silver_i",
            "gold_iii",
            "gold_ii",
            "gold_i",
            "platinum_iii",
            "platinum_ii",
            "platinum_i",
            "master",
            "grand_master",
        )

        fun handleScreen(screen: ContainerScreen) = useScreen(screen) {
            updateRank(getItem(10).hoverName.string)
            val currentRank = Trident.playerState.arenaData.currentRank
            Logger.debugLog("Set rank to $currentRank")
        }

        fun updateRank(name: String?) {
            if (name == null) {
                Trident.playerState.arenaData.currentRank = null
                return
            }

            val rank = name.replace(" ", "_").lowercase()
            if (rank !in knownRanks) {
                Trident.playerState.arenaData.currentRank = null
                return
            }

            Trident.playerState.arenaData.currentRank = Rank(name, "rank_$rank")
        }

    }
}
