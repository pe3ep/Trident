package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.client.listeners.KillChatListener
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.questing.QuestListener
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.feature.questing.game.DynaballHandlers
import cc.pe3epwithyou.trident.feature.questing.game.HITWHandlers
import cc.pe3epwithyou.trident.feature.questing.game.RSRHandlers
import cc.pe3epwithyou.trident.feature.questing.game.SkyBattleHandlers
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.fishing.SuppliesDialog
import cc.pe3epwithyou.trident.interfaces.fishing.SpotDialog
import cc.pe3epwithyou.trident.interfaces.fishing.UpgradesDialog
import cc.pe3epwithyou.trident.interfaces.fishing.ChanceDialog
import cc.pe3epwithyou.trident.interfaces.fishing.HookChanceDialog
import cc.pe3epwithyou.trident.interfaces.fishing.MagnetChanceDialog
import cc.pe3epwithyou.trident.interfaces.fishing.RodChanceDialog
import cc.pe3epwithyou.trident.interfaces.fishing.PotChanceDialog
import cc.pe3epwithyou.trident.interfaces.fishing.ChancePerksDialog
import cc.pe3epwithyou.trident.interfaces.killfeed.KillFeedDialog
import cc.pe3epwithyou.trident.interfaces.questing.QuestingDialog
import cc.pe3epwithyou.trident.state.ClimateType
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import com.noxcrew.noxesium.NoxesiumFabricMod
import com.noxcrew.noxesium.feature.skull.SkullContents
import com.noxcrew.noxesium.network.NoxesiumPackets
import com.noxcrew.noxesium.network.clientbound.ClientboundMccGameStatePacket
import net.fabricmc.loader.api.FabricLoader
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

    private fun updateGameDialogs(currentGame: Game, game: String) {
        // If we are staying in the same game and it's Fishing, keep existing dialogs and state (avoid resetting dropdowns)
        if (currentGame == MCCIState.game && currentGame == Game.FISHING) {
            return
        }
        DialogCollection.clear()
        KillFeedDialog.clearKills()

        if (currentGame == Game.FISHING && Config.Fishing.suppliesModule) {
            val k = "supplies"
            DialogCollection.open(k, SuppliesDialog(10, 10, k))
        }
        if (currentGame == Game.FISHING) {
            val k = "spot"
            DialogCollection.open(k, SpotDialog(10, 10, k))
        }
        if (currentGame == Game.FISHING) {
            val k = "upgrades"
            DialogCollection.open(k, UpgradesDialog(10, 20, k))
        }
        if (currentGame == Game.FISHING) {
            val k = "hookchances"
            DialogCollection.open(k, HookChanceDialog(10, 30, k))
        }
        if (currentGame == Game.FISHING) {
            val k = "magnetchances"
            DialogCollection.open(k, MagnetChanceDialog(10, 30, k))
        }
        if (currentGame == Game.FISHING) {
            val k = "rodchances"
            DialogCollection.open(k, RodChanceDialog(10, 30, k))
        }
        if (currentGame == Game.FISHING) {
            val k = "potchances"
            DialogCollection.open(k, PotChanceDialog(10, 30, k))
        }
        if (currentGame == Game.FISHING) {
            val k = "chanceperks"
            DialogCollection.open(k, ChancePerksDialog(10, 30, k))
        }
        if ((currentGame == Game.BATTLE_BOX || currentGame == Game.DYNABALL || currentGame == Game.SKY_BATTLE) && Config.KillFeed.enabled) {
            val k = "killfeed"
            DialogCollection.open(k, KillFeedDialog(10, 10, k))
        }
        if (currentGame != Game.HUB && currentGame != Game.FISHING) {
            DelayedAction.delayTicks(20L) {
                val k = "questing"
                if (QuestListener.checkIfPlobby()) return@delayTicks
                if (!Config.Questing.enabled) return@delayTicks
                QuestingDialog.currentGame = currentGame
                if (QuestStorage.getActiveQuests(currentGame)
                        .isEmpty() && Config.Questing.hideIfNoQuests
                ) return@delayTicks
                DialogCollection.open(k, QuestingDialog(10, 10, k))
            }
        }
        if (currentGame == Game.HUB && game != "" && Config.Questing.showInLobby) {
            val k = "questing"
            if (!Config.Questing.enabled) return
            QuestingDialog.currentGame = Game.entries.filter { g -> g.server == game }.getOrNull(0) ?: return
            DialogCollection.open(k, QuestingDialog(10, 10, k))
        }
    }

    private fun removeKillsIfNeeded(packet: ClientboundMccGameStatePacket) {
        if (MCCIState.game !in listOf(Game.BATTLE_BOX, Game.DYNABALL, Game.SKY_BATTLE)) return
        KillChatListener.resetStreaks()
        if (Config.KillFeed.enabled && Config.KillFeed.clearAfterRound) {
            if (packet.phaseType == "INTERMISSION" && packet.stage == "countdownphase") {
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


            if (Config.Debug.logForScrapers) (ChatUtils.info(
                "Got Nox packet CLIENT_MCC_SERVER: serverType:$server subType:$type associatedGame:$game"
            ))

            updateFishingState(type)

            val currentGame = getCurrentGame(server, type, game)
            updateGameDialogs(currentGame, game)
            if (currentGame in listOf(Game.DYNABALL, Game.BATTLE_BOX)) {
                KillFeedDialog.clearKills()
            }
            if (currentGame != MCCIState.game) {
                MCCIState.game = currentGame
                ChatUtils.debugLog("Current game: ${MCCIState.game.title}")
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
            if (Config.Debug.logForScrapers) (ChatUtils.info(
                """
                        Got Nox packet CLIENT_MCC_GAME_STATE:
                        mapID:${packet.mapId}
                        mapName:${packet.mapName}
                        round:${packet.round}
                        stage:${packet.stage}
                        phaseType:${packet.phaseType}
                        totalRounds:${packet.totalRounds}
                """.trimIndent()
            ))
        }
    }

    private fun updateFishingState(island: String) {
        MCCIState.fishingState.isGrotto = island.contains("grotto", ignoreCase = true)

        ClimateType.entries.forEach { climate ->
            if (island.contains(climate.prefix, ignoreCase = true)) {
                MCCIState.fishingState.climate.climateType = climate
                return
            }
        }
    }

    private fun getCurrentGame(server: String, type: String, game: String): Game {
        if (server == Game.HUB.server) {
            return when {
                type.contains("temperate", ignoreCase = true) -> Game.FISHING
                type.contains("tropical", ignoreCase = true) -> Game.FISHING
                type.contains("barren", ignoreCase = true) -> Game.FISHING
                else -> Game.HUB
            }
        }

        if (game == "parkour_warrior") {
            return if (type == Game.PARKOUR_WARRIOR_SURVIVOR.subtype) {
                Game.PARKOUR_WARRIOR_SURVIVOR
            } else {
                Game.PARKOUR_WARRIOR_DOJO
            }
        }

        Game.entries.forEach { mccGame ->
            if (mccGame in listOf(
                    Game.HUB, Game.FISHING, Game.PARKOUR_WARRIOR_DOJO, Game.PARKOUR_WARRIOR_SURVIVOR
                )
            ) return@forEach

            if (mccGame.server == game) {
                return mccGame
            }
        }

        return Game.HUB
    }
}