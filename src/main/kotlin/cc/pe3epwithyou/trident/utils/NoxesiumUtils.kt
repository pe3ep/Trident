package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.client.events.questing.HITWQuestEvents
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.dialogs.DialogCollection
import cc.pe3epwithyou.trident.dialogs.fishing.SuppliesDialog
import cc.pe3epwithyou.trident.dialogs.killfeed.KillFeedDialog
import cc.pe3epwithyou.trident.dialogs.questing.QuestingDialog
import cc.pe3epwithyou.trident.state.ClimateType
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.MCCIslandState
import com.noxcrew.noxesium.NoxesiumFabricMod
import com.noxcrew.noxesium.feature.skull.SkullContents
import com.noxcrew.noxesium.network.NoxesiumPackets
import com.noxcrew.noxesium.network.clientbound.ClientboundMccGameStatePacket
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.MutableComponent
import java.util.*

object NoxesiumUtils {

    fun skullComponent(
        uuid: UUID,
        grayscale: Boolean = false,
        advance: Int = 0,
        ascent: Int = 0,
        scale: Float = 1.0F
    ): MutableComponent {
        return MutableComponent.create(
            SkullContents(
                Optional.of(uuid),
                Optional.empty(),
                grayscale,
                advance,
                ascent,
                scale
            )
        )
    }

    private fun updateGameDialogs(currentGame: MCCGame) {
        DialogCollection.clear()
        if (currentGame == MCCGame.FISHING && Config.Fishing.suppliesModule) {
            val k = "supplies"
            DialogCollection.open(k, SuppliesDialog(10, 10, k))
        }
        if ((currentGame == MCCGame.BATTLE_BOX || currentGame == MCCGame.DYNABALL) && Config.KillFeed.enabled) {
            val k = "killfeed"
            DialogCollection.open(k, KillFeedDialog(10, 10, k))
        }
        if (currentGame != MCCGame.HUB && currentGame != MCCGame.FISHING) {
            val k = "questing"
            DialogCollection.open(k, QuestingDialog(10, 10, k))
        }
    }

    private fun removeKillsIfNeeded(packet: ClientboundMccGameStatePacket) {
        if (MCCIslandState.game !in listOf(MCCGame.BATTLE_BOX, MCCGame.DYNABALL)) return
        if (Config.KillFeed.enabled && Config.KillFeed.clearAfterRound) {
            if (packet.phaseType == "INTERMISSION" && packet.stage == "countdownphase") {
                KillFeedDialog.clearKills()
            }
        }
    }

    private fun handleTimedQuests() {
        if (MCCIslandState.game == MCCGame.HITW) {
            HITWQuestEvents.scheduleSurvivedMinute()
            HITWQuestEvents.scheduleSurvivedTwoMinutes()
        }
    }

    fun registerListeners() {
        if (FabricLoader.getInstance().isModLoaded("noxesium")) {
            NoxesiumFabricMod.initialize()
        }

        NoxesiumPackets.CLIENT_MCC_SERVER.addListener(this) { _, packet, _ ->
            val server = packet.serverType
            val type = packet.subType
            val game = packet.associatedGame

            ChatUtils.debugLog(
                "NOX Packet received:\nserver: $server\ntype: $type\ngame: $game"
            )


            if (Config.Debug.logForScrapers) (
                ChatUtils.info(
                    "Got Nox packet CLIENT_MCC_SERVER: serverType:$server subType:$type associatedGame:$game"
                )
            )

            updateFishingState(type)

            val currentGame = getCurrentGame(server, type, game)
            if (currentGame in listOf(MCCGame.DYNABALL, MCCGame.BATTLE_BOX)) {
                KillFeedDialog.clearKills()
            }
            if (currentGame != MCCIslandState.game) {
                MCCIslandState.game = currentGame
                updateGameDialogs(currentGame)
                ChatUtils.debugLog("Current game: ${MCCIslandState.game.title}")
            }
        }

        NoxesiumPackets.CLIENT_MCC_GAME_STATE.addListener(this) { _, packet, _ ->
            removeKillsIfNeeded(packet)
            if (packet.phaseType == "PLAY" || packet.stage == "inround") {
                handleTimedQuests()
            }
            ChatUtils.debugLog(
                """
                NOX GAME_STATE Packet Received:
                mapID: ${packet.mapId}
                mapName: ${packet.mapName}
                round: ${packet.round}
                stage: ${packet.stage}
                phaseType: ${packet.phaseType}
                totalRounds: ${packet.totalRounds}
                """.trimIndent()
            )
            if (Config.Debug.logForScrapers) (
                ChatUtils.info(
                    """
                        Got Nox packet CLIENT_MCC_GAME_STATE:
                        mapID:${packet.mapId}
                        mapName:${packet.mapName}
                        round:${packet.round}
                        stage:${packet.stage}
                        phaseType:${packet.phaseType}
                        totalRounds:${packet.totalRounds}
                    """.trimIndent()
                )
            )
        }
    }

    private fun updateFishingState(island: String) {
        MCCIslandState.fishingState.isGrotto = island.contains("grotto", ignoreCase = true)

        ClimateType.entries.forEach { climate ->
            if (island.contains(climate.prefix, ignoreCase = true)) {
                MCCIslandState.fishingState.climate.climateType = climate
                return
            }
        }
    }

    private fun getCurrentGame(server: String, type: String, game: String): MCCGame {
        if (server == MCCGame.HUB.server) {
            return when {
                type.contains("temperate", ignoreCase = true) -> MCCGame.FISHING
                type.contains("tropical", ignoreCase = true) -> MCCGame.FISHING
                type.contains("barren", ignoreCase = true) -> MCCGame.FISHING
                else -> MCCGame.HUB
            }
        }

        if (game == "parkour_warrior") {
            return if (type == MCCGame.PARKOUR_WARRIOR_SURVIVOR.subtype) {
                MCCGame.PARKOUR_WARRIOR_SURVIVOR
            } else {
                MCCGame.PARKOUR_WARRIOR_DOJO
            }
        }

        MCCGame.entries.forEach { mccGame ->
            if (mccGame in listOf(
                    MCCGame.HUB,
                    MCCGame.FISHING,
                    MCCGame.PARKOUR_WARRIOR_DOJO,
                    MCCGame.PARKOUR_WARRIOR_SURVIVOR
                )
            ) return@forEach

            if (mccGame.server == game) {
                return mccGame
            }
        }

        return MCCGame.HUB
    }
}