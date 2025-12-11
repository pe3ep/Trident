package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.client.events.KillEvents
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.killfeed.DeathMessages
import cc.pe3epwithyou.trident.feature.killfeed.KillMethod
import cc.pe3epwithyou.trident.feature.questing.EliminatedCriteria
import cc.pe3epwithyou.trident.feature.questing.IncrementContext
import cc.pe3epwithyou.trident.feature.questing.QuestCriteria
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.interfaces.killfeed.KillFeedDialog
import cc.pe3epwithyou.trident.interfaces.killfeed.widgets.KillWidget
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import com.noxcrew.sheeplib.util.opacity
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object KillChatListener {
    val killfeedGames = listOf(
        Game.BATTLE_BOX, Game.BATTLE_BOX_ARENA, Game.DYNABALL, Game.SKY_BATTLE, Game.ROCKET_SPLEEF_RUSH
    )

    private val fallbackColor = 0xFFFFFF opacity 128

    val streaks = hashMapOf<String, Int>()

    fun resetStreaks() {
        streaks.clear()
    }

    fun register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register allowGame@{ message, _ ->
            if (!MCCIState.isOnIsland()) return@allowGame true

            val killAssistMatch = Regex("""^\[.] You assisted in eliminating (.+)!""").matches(message.string)
            if (killAssistMatch) {
                KillFeedDialog.applyKillAssist()
            }

            DeathMessages.entries.forEach { deathMessage ->
                if (deathMessage.regex.matches(message.string)) {
                    return@allowGame handleKill(message, deathMessage.method)
                }
            }

            return@allowGame true
        }
    }

    private fun handleKill(message: Component, method: KillMethod): Boolean {
        val players = cleanupComponent(message)
        if (players.isEmpty()) return true
        val victim = players[0]
        val attacker = players.getOrNull(1)
        var killMethod = method
        if (method == KillMethod.MAGIC) {
            if ("Splash Potion" in message.string) killMethod = KillMethod.POTION
            if ("Orb" in message.string) killMethod = KillMethod.ORB
        }

        /* Call the event for external use */
        KillEvents.KILL.invoker().onKill(
            KillEvents.KillEventPlayer(
                victim.string, victim.style.color?.value ?: fallbackColor
            ), if (attacker == null) null else KillEvents.KillEventPlayer(
                attacker.string, attacker.style.color?.value ?: fallbackColor
            ), killMethod
        )
        if (MCCIState.game !in killfeedGames) return true

        if (attacker != null) {
            //        Questing
            val self = Minecraft.getInstance().player ?: return true
            if (attacker.string == self.name.string) {
                val game = MCCIState.game
                val ctx = EliminatedCriteria.get(game, sourceTag = "kill") ?: return true
                QuestStorage.applyIncrement(ctx, true)

                if (game == Game.BATTLE_BOX || game == Game.BATTLE_BOX_ARENA) {
                    val ctx = IncrementContext(
                        Game.BATTLE_BOX,
                        QuestCriteria.BATTLE_BOX_QUADS_PLAYERS_KILLED_OR_ASSISTED,
                        1,
                        "bb_kills_or_assists"
                    )
                    QuestStorage.applyIncrement(ctx, true)
                    if (killMethod == KillMethod.RANGE) {
                        QuestStorage.applyIncrement(
                            IncrementContext(
                                Game.BATTLE_BOX, QuestCriteria.BATTLE_BOX_QUADS_RANGED_KILLS, 1, "bb_ranged_kill"
                            ), true
                        )

                    }
                }
            }

            // Streaks
            streaks[attacker.string] = (streaks[attacker.string] ?: 0) + 1

            KillFeedDialog.addKill(
                KillWidget(
                    victim.string,
                    killMethod,
                    attacker.string,
                    getColors(victim, attacker),
                    streak = streaks[attacker.string]!! // This should never fail
                )
            )
        } else {
            val victimColor = victim.style.color?.value?.opacity(128) ?: fallbackColor
            KillFeedDialog.addKill(
                KillWidget(
                    victim.string, killMethod, killColors = Pair(0x606060 opacity 128, victimColor)
                )
            )
        }

        return !Config.KillFeed.hideKills
    }

    private fun getColors(victim: Component, attacker: Component): Pair<Int, Int> {
        val attackerColor = attacker.style.color?.value?.opacity(128) ?: fallbackColor
        var victimColor = victim.style.color?.value?.opacity(128) ?: fallbackColor

        if (attackerColor == victimColor) {
            victimColor = victim.style.color?.value?.opacity(96) ?: 0xFFFFFF.opacity(96)
        }

        return Pair(attackerColor, victimColor)
    }

    private fun cleanupComponent(c: Component): List<Component> {
        val rawList = c.toFlatList()
        val socialManager = Minecraft.getInstance().playerSocialManager
        val components: MutableList<Component> = mutableListOf()
        rawList.forEach {
            if (it.string.length in 1..2) return@forEach
            if ("[" in it.string) return@forEach
            val uuid = socialManager.getDiscoveredUUID(it.string)
            if (uuid == Util.NIL_UUID) return@forEach
            components.add(it)
            if (components.size == 2) return components
        }
        return components
    }
}