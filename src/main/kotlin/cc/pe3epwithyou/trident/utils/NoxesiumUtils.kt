package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.client.listeners.KillChatListener
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.feature.questing.game.DynaballHandlers
import cc.pe3epwithyou.trident.feature.questing.game.HITWHandlers
import cc.pe3epwithyou.trident.feature.questing.game.RSRHandlers
import cc.pe3epwithyou.trident.feature.questing.game.SkyBattleHandlers
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.fishing.SuppliesDialog
import cc.pe3epwithyou.trident.interfaces.killfeed.KillFeedDialog
import cc.pe3epwithyou.trident.interfaces.questing.QuestingDialog
import cc.pe3epwithyou.trident.interfaces.questing.QuestingDialog.QuestingDialogState
import cc.pe3epwithyou.trident.state.ClimateType
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import com.noxcrew.noxesium.core.fabric.feature.skull.SkullContents
import com.noxcrew.noxesium.core.mcc.ClientboundMccGameStatePacket
import com.noxcrew.noxesium.core.mcc.ClientboundMccServerPacket
import com.noxcrew.noxesium.core.mcc.ClientboundMccStatisticPacket
import com.noxcrew.noxesium.core.mcc.MccPackets
import net.minecraft.network.chat.MutableComponent
import java.util.*

object NoxesiumUtils {
    fun skullComponent(
        uuid: UUID, grayscale: Boolean = false, advance: Int = 0, ascent: Int = 0, scale: Float = 1.0F
    ): MutableComponent {
        return MutableComponent.create(
            SkullContents(
                Optional.of(uuid), Optional.empty(), grayscale, advance, ascent, scale
            )
        )
    }

    fun updateGameDialogs(currentGame: Game, isPlobby: Boolean) {
        DialogCollection.clear()
        KillFeedDialog.clearKills()

        if (currentGame == Game.FISHING && Config.Fishing.suppliesModule) {
            val k = "supplies"
            DialogCollection.open(k, SuppliesDialog(10, 10, k))
        }
        if (KillChatListener.killfeedGames.contains(currentGame) && Config.KillFeed.enabled) {
            val k = "killfeed"
            DialogCollection.open(k, KillFeedDialog(10, 10, k))
        }
        if (currentGame != Game.HUB && currentGame != Game.FISHING) {
            if (!Config.Questing.enabled) return
            val k = "questing"
            if (isPlobby) {
                DialogCollection.close(k)
                return
            }
            if (QuestStorage.getActiveQuests(currentGame)
                    .isEmpty() && Config.Questing.hideIfNoQuests
            ) {
                DialogCollection.close(k)
                return
            }
            QuestingDialog.currentGame = currentGame
            QuestingDialog.dialogState = QuestingDialogState.NORMAL
            DialogCollection.open(k, QuestingDialog(10, 10, k))
            DialogCollection.refreshDialog(k)
        }
//        if (currentGame == Game.HUB && Config.Questing.showInLobby) {
//            val k = "questing"
//            if (!Config.Questing.enabled) return
//            QuestingDialog.currentGame = Game.entries.filter { g -> g == currentGame }.getOrNull(0) ?: return
//            DialogCollection.open(k, QuestingDialog(10, 10, k))
//        }
    }

    private fun removeKillsIfNeeded(packet: ClientboundMccGameStatePacket) {
        if (MCCIState.game !in KillChatListener.killfeedGames) return
        KillChatListener.resetStreaks()
        if (Config.KillFeed.enabled && Config.KillFeed.clearAfterRound) {
            if (packet.phaseType == "INTERMISSION" && (packet.stage == "countdownphase" || packet.stage == "preparationphase")) {
                KillFeedDialog.clearKills()
            }
        }
    }

    private fun handleTimedQuests() {
        if (MCCIState.game == Game.HITW) {
            HITWHandlers.scheduleSurvivedMinute()
            HITWHandlers.scheduleSurvivedTwoMinutes()
        }

        if (MCCIState.game == Game.SKY_BATTLE) {
            SkyBattleHandlers.scheduleSurvivedMinute()
            SkyBattleHandlers.scheduleSurvivedTwoMinutes()
        }

        if (MCCIState.game == Game.ROCKET_SPLEEF_RUSH) {
            RSRHandlers.scheduleSurvivedMinute()
        }

        if (MCCIState.game == Game.DYNABALL) {
            DynaballHandlers.scheduleDynaball()
        }
    }

    fun registerListeners() {
        MccPackets.CLIENTBOUND_MCC_SERVER.addListener(this, ClientboundMccServerPacket::class.java) { _, packet, _ ->
            val server = packet.server
            val types = packet.types

            ChatUtils.debugLog(
                "NOX Packet received:\nserver: $server\ntypes: $types"
            )

            val currentGame = getCurrentGame(server, types)
            var isPlobby = false

            if ("session" in types) {
                isPlobby = true
            }

            updateGameDialogs(currentGame, isPlobby)

            if (currentGame in KillChatListener.killfeedGames) {
                KillFeedDialog.clearKills()
            }
            if (currentGame != MCCIState.game) {
                MCCIState.game = currentGame
                ChatUtils.debugLog("Current game: ${MCCIState.game.title}")
            }
        }

        MccPackets.CLIENTBOUND_MCC_GAME_STATE.addListener(
            this,
            ClientboundMccGameStatePacket::class.java
        ) { _, packet, _ ->
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
        }

        MccPackets.CLIENTBOUND_MCC_STATISTIC.addListener(
            this,
            ClientboundMccStatisticPacket::class.java
        ) { _, packet, _ ->
            if (Config.Debug.enableLogging) {
                ChatUtils.sendMessage(
                    """
                    CLIENTBOUND_MCC_STATISTIC:
                    stat: ${packet.statistic}
                    value: ${packet.value}
                    record: ${packet.record}
                """.trimIndent()
                )
            }
        }
    }

    private fun updateFishingState(island: String) {
        MCCIState.fishingState.isGrotto = island.contains("grotto", ignoreCase = true)

        ClimateType.entries.forEach {
            if (island.contains(it.prefix, ignoreCase = true)) {
                MCCIState.fishingState.climate.climateType = it
                return
            }
        }
    }

    private fun getCurrentGame(server: String, types: List<String>): Game {
        if (types.size < 2) {
            ChatUtils.error("Returned server types were too short")
            return Game.HUB
        }

        // Fishing
        if (server == "fishing") {
            val island = types.getOrNull(2) ?: "temperate_1"
            updateFishingState(island)
            return Game.FISHING
        }

        // Lobby servers
        if (server == "lobby") {
            val game = types.getOrNull(2) ?: "lobby"
            return parseGameString(game)
        }

        // Dojo
        if (server == "dojo") {
            return Game.PARKOUR_WARRIOR_DOJO
        }

        // Games
        if (server == "game") {
            Game.entries.forEach { game ->
                game.types?.all { it in types }?.let { bool ->
                    if (!bool) return@forEach

                    // BB Arena edge case
                    if (game == Game.BATTLE_BOX_ARENA ||
                        game == Game.BATTLE_BOX
                    ) {
                        if ("arena" in types) {
                            return Game.BATTLE_BOX_ARENA
                        }
                        return Game.BATTLE_BOX
                    }

                    return game
                }
            }
        }

        return Game.HUB
    }


    private fun parseGameString(game: String): Game {
        Game.entries.forEach {
            if (it.gameID == game) {
                return it
            }
        }
        return Game.HUB
    }
//
//    private fun getCurrentGameOld(server: String, types: List<String>): Game {
//        if (server == Game.HUB.server) {
//            return when {
//                type.contains("temperate", ignoreCase = true) -> Game.FISHING
//                type.contains("tropical", ignoreCase = true) -> Game.FISHING
//                type.contains("barren", ignoreCase = true) -> Game.FISHING
//                else -> Game.HUB
//            }
//        }
//
//        if (game == "parkour_warrior") {
//            return if (type == Game.PARKOUR_WARRIOR_SURVIVOR.subtype) {
//                Game.PARKOUR_WARRIOR_SURVIVOR
//            } else {
//                Game.PARKOUR_WARRIOR_DOJO
//            }
//        }
//
//        if (game == "battle_box") {
//            return if (type == Game.BATTLE_BOX_ARENA.subtype) {
//                Game.BATTLE_BOX_ARENA
//            } else {
//                Game.BATTLE_BOX
//            }
//        }
//
//        Game.entries.forEach {
//            if (it in listOf(
//                    Game.HUB,
//                    Game.FISHING,
//                    Game.PARKOUR_WARRIOR_DOJO,
//                    Game.PARKOUR_WARRIOR_SURVIVOR,
//                    Game.BATTLE_BOX,
//                    Game.BATTLE_BOX_ARENA
//                )
//            ) return@forEach
//
//            if (it.server == game) {
//                return it
//            }
//        }
//
//        return Game.HUB
//    }
}