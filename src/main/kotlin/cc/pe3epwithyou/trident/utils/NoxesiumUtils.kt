package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.client.events.KillChatListener
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.dialogs.DialogCollection
import cc.pe3epwithyou.trident.dialogs.fishing.SuppliesDialog
import cc.pe3epwithyou.trident.dialogs.killfeed.KillFeedDialog
import cc.pe3epwithyou.trident.state.ClimateType
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.MCCIslandState
import com.noxcrew.noxesium.NoxesiumFabricMod
import com.noxcrew.noxesium.feature.skull.SkullContents
import com.noxcrew.noxesium.network.NoxesiumPackets
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
        if (currentGame == MCCGame.BATTLE_BOX && Config.KillFeed.enabled) {
            val k = "killfeed"
            DialogCollection.open(k, KillFeedDialog(10, 10, k))
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

            if (Config.Debug.enableLogging) {
                ChatUtils.sendMessage(
                    "NOX Packet received:\nserver: $server\ntype: $type\ngame: $game"
                )
            }

            updateFishingState(type)

            val currentGame = getCurrentGame(server, type, game)
            if (currentGame in listOf(MCCGame.DYNABALL, MCCGame.BATTLE_BOX)) {
                KillFeedDialog.clearKills()
            }
            if (currentGame != MCCIslandState.game) {
                MCCIslandState.game = currentGame
                updateGameDialogs(currentGame)
                if (Config.Debug.enableLogging) {
                    ChatUtils.sendMessage("Current game: ${MCCIslandState.game.title}")
                }
            }
        }

        NoxesiumPackets.CLIENT_MCC_GAME_STATE.addListener(this) { _, packet, _ ->
            if (Config.Debug.enableLogging) {
                ChatUtils.sendMessage(
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
            }
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