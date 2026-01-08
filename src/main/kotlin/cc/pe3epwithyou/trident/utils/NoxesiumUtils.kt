package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.client.listeners.KillChatListener
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.questing.GameQuests
import cc.pe3epwithyou.trident.feature.questing.IncrementContext
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.fishing.SuppliesDialog
import cc.pe3epwithyou.trident.interfaces.killfeed.KillFeedDialog
import cc.pe3epwithyou.trident.interfaces.questing.QuestingDialog
import cc.pe3epwithyou.trident.mixin.BossHealthOverlayAccessor
import cc.pe3epwithyou.trident.state.ClimateType
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import com.noxcrew.noxesium.core.fabric.feature.sprite.SkullSprite
import com.noxcrew.noxesium.core.mcc.ClientboundMccGameStatePacket
import com.noxcrew.noxesium.core.mcc.ClientboundMccServerPacket
import com.noxcrew.noxesium.core.mcc.ClientboundMccStatisticPacket
import com.noxcrew.noxesium.core.mcc.MccPackets
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.BossEvent
import java.util.*


object NoxesiumUtils {
    fun skullComponent(
        uuid: UUID, advance: Int = 0, ascent: Int = 0, scale: Float = 1.0F, hat: Boolean = true
    ): MutableComponent {
        return Component.`object`(
            SkullSprite(
                Optional.of(uuid), Optional.empty(), advance, ascent, scale, hat
            )
        )
    }

    fun updateGameDialogs(currentGame: Game, isPlobby: Boolean, types: List<String>) {
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

            if ("lobby" in types && !Config.Questing.showInLobby) return
            QuestingDialog.currentGame = currentGame
            DialogCollection.open(k, QuestingDialog(10, 10, k))
            DialogCollection.refreshDialog(k)
        }
    }

    private fun removeKillsIfNeeded(packet: ClientboundMccGameStatePacket) {
        if (MCCIState.game !in KillChatListener.killfeedGames) return
        KillChatListener.resetStreaks()
        if (Config.KillFeed.enabled && Config.KillFeed.clearAfterRound) {
            if (packet.phaseType == "intermission" && (packet.stage == "countdownphase" || packet.stage == "preparationphase")) {
                KillFeedDialog.clearKills()
            }
        }
    }


    fun registerListeners() {
        MccPackets.CLIENTBOUND_MCC_SERVER.addListener(
            this,
            ClientboundMccServerPacket::class.java
        ) { _, packet, _ ->
            val server = packet.server
            val types = packet.types

            Logger.debugLog(
                "NOX Packet received:\nserver: $server\ntypes: $types"
            )

            val currentGame = getCurrentGame(server, types)
            MCCIState.isPlobby = false

            if ("session" in types) {
                MCCIState.isPlobby = true
            }

            updateGameDialogs(currentGame, MCCIState.isPlobby, types)

            if (currentGame in KillChatListener.killfeedGames) {
                KillFeedDialog.clearKills()
            }
            if (currentGame != MCCIState.game) {
                MCCIState.game = currentGame
                Logger.debugLog("Current game: ${MCCIState.game.title}")
            }
        }

        MccPackets.CLIENTBOUND_MCC_GAME_STATE.addListener(
            this, ClientboundMccGameStatePacket::class.java
        ) { _, packet, _ ->
            removeKillsIfNeeded(packet)
            Logger.debugLog(
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
            this, ClientboundMccStatisticPacket::class.java
        ) { _, packet, _ ->
            Logger.debugLog(
                """
                CLIENTBOUND_MCC_STATISTIC:
                stat: ${packet.statistic}
                value: ${packet.value}
                record: ${packet.record}
            """.trimIndent()
            )

            handleQuests(packet.statistic, packet.value)
            handleFishCaught(packet.statistic, packet.value)
        }
    }

    private fun handleQuests(stat: String, value: Int) {
        val currentGame = MCCIState.game
        if (currentGame == Game.HUB || currentGame == Game.FISHING) return
        try {
            val criteria = GameQuests.valueOf(currentGame.toString()).list
            criteria.filter { stat in it.statisticKeys }.forEach {
                val game =
                    if (currentGame == Game.BATTLE_BOX_ARENA) Game.BATTLE_BOX else currentGame
                val ctx = IncrementContext(
                    game, it, value, stat
                )
                QuestStorage.applyIncrement(ctx)
            }
            DialogCollection.refreshDialog("questing")
        } catch (e: Exception) {
            Logger.error("Something went wrong when handling quest for stat $stat: ${e.message}")
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

    private fun handleFishCaught(statistic: String, value: Int) {
        val wayfinderStatus = when (MCCIState.fishingState.climate.climateType) {
            ClimateType.TEMPERATE -> Trident.playerState.wayfinderData.temperate
            ClimateType.TROPICAL -> Trident.playerState.wayfinderData.tropical
            ClimateType.BARREN -> Trident.playerState.wayfinderData.barren
        }

        // Grotto Stability
        val events = (Minecraft.getInstance().gui.bossOverlay as BossHealthOverlayAccessor).events
        events.forEach { (key, value) ->
            val text = value.name.string
            if (text.contains("STABILITY")) {
                val newStability = text.split(": ")[1].replace("%", "")
                Logger.debugLog("${MCCIState.fishingState.climate.climateType}: Grotto is at $newStability%")
                wayfinderStatus.grottoStability = newStability.replace("\uE024", "").replace("\uE01D", "").toIntOrNull() ?: wayfinderStatus.grottoStability
                DialogCollection.refreshDialog("wayfinder")
            }
        }

        // Wayfinder
        if (statistic.contains("fishing_wayfinder_xp_")) {
            if (!wayfinderStatus.hasGrotto) {
                wayfinderStatus.data += value
                if (wayfinderStatus.data >= 2000) {
                    wayfinderStatus.hasGrotto = true
                    wayfinderStatus.grottoStability = 100
                }
            }

            DialogCollection.refreshDialog("wayfinder")
        }
    }


    private fun getCurrentGame(server: String, types: List<String>): Game {
        if (types.size < 2) {
            Logger.error("Returned server types were too short")
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
                    if (game == Game.BATTLE_BOX_ARENA || game == Game.BATTLE_BOX) {
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
}