package cc.pe3epwithyou.trident.feature.discord

import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Logger
import dev.cbyrne.kdiscordipc.data.activity.*
import net.fabricmc.loader.api.FabricLoader
import java.time.Instant

object ActivityManager {
    private var currentActivity: Activity? = null

    private fun createInitialActivity(): Activity {
        val container = FabricLoader.getInstance().getModContainer("trident")
        var version = "Unknown"
        container.ifPresent { modContainer ->
            version = modContainer.metadata.version.friendlyString
        }

        val start = Instant.now().epochSecond

        currentActivity = activity {
            details = "Playing MCC Island"
            largeImage("game_hub")
            smallImage("trident", "Trident $version")
            timestamps(start)
            button("Download Trident", "https://trident.pe3epwithyou.cc")
        }

        return currentActivity!!
    }

    private fun ensureActivity(): Activity = currentActivity ?: createInitialActivity()

    fun hideActivity() {
        currentActivity = null
        IPCManager.submitActivity(null)
    }

    fun updateCurrentActivity() {
        val activity = ensureActivity()
        activity.state = null
        val game = MCCIState.game
        activity.details = "Playing ${game.title}"
        MCCIState.gameState?.let {
            if (it.stage == "podiumphase") {
                activity.state = "Game over"
                return@let
            }
            if (it.totalRounds > 1) {
                activity.state = "${it.mapName} - Round ${it.round + 1} of ${it.totalRounds}"
            } else {
                activity.state = "${it.mapName}"
            }
        }
        when (game) {
            Game.HUB -> {
                activity.details = "In the Hub"
                activity.state = null
                activity.largeImage("game_hub")
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
                activity.largeImage("game_fishing_$island")
            }

            Game.BATTLE_BOX_ARENA -> {
                MCCIState.gameState?.let {
                    if (it.stage == "podiumphase") return@let
                    activity.state = "${it.mapName} - Round ${it.round + 1}"
                }

                activity.largeImage("game_battle_box_arena")
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
                activity.largeImage("game_${game.name.lowercase()}")
            }

            else -> {
                activity.largeImage("game_${game.name.lowercase()}")
            }
        }

        IPCManager.submitActivity(activity)
    }
}
