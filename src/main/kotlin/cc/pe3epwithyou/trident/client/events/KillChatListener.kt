package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.dialogs.killfeed.KillFeedDialog
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.widgets.killfeed.KillMethod
import cc.pe3epwithyou.trident.widgets.killfeed.KillWidget
import cc.pe3epwithyou.trident.widgets.questing.CompletionCriteria
import cc.pe3epwithyou.trident.widgets.questing.GenericCompletionCriteria
import cc.pe3epwithyou.trident.widgets.questing.QuestStorage
import com.noxcrew.sheeplib.util.opacity
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object KillChatListener {
    private val fallbackColor = 0xFFFFFF opacity 128

    private val slainRegex = Regex("^\\[.] .+ (was slain by) .+")
    private val shotRegex = Regex("^\\[.] .+ (was shot by) .+")
    private val explodedRegex = Regex("^\\[.] .+ (was blown up by) .+")
    private val explodedSelfRegex = Regex("^\\[.] .+ blew up\\. .+")
    private val lavaRegex = Regex("^\\[.] .+ (tried to swim in lava to escape) .+")
    private val lavaSelfRegex = Regex("^\\[.] .+ tried to swim in lava\\. .+")
    private val potRegex = Regex("^\\[.] .+ was eliminated with magic by .+ using .+")
    private val potRegexAlt = Regex("^\\[.] .+ was hit by .+")
    private val loggedOut = Regex("^\\[.] .+ logged out\\. .+")
    private val loggedOutToGetAway = Regex("^\\[.] .+ logged out to get away from .+")
    private val genericDiedRegex = Regex("^\\[.] .+ died\\. .+")
    private val spleefedRegex = Regex("^\\[.] .+ was spleefed by .+")
    private val prickedRegex = Regex("^\\[.] .+ was pricked to death whilst trying to escape .+")
    private val prickedSelfRegex = Regex("^\\[.] .+ was pricked to death\\. .+")
    private val walkedFire = Regex("^\\[.] .+ walked into fire whilist fighting .+")
    private val fireSelf = Regex("^\\[.] .+ went up in flames\\. .+")
    private val burnedRegex = Regex("^\\[.] .+ was burned to a crisp while fighting .+")
    private val burnedSelfRegex = Regex("^\\[.] .+ burned to death\\. .+")
    private val hasNotRejoined = Regex("^\\[.] .+ hasn't rejoined the game and is automatically eliminated\\. .+")
    private val disconnected = Regex("^\\[.] .+ disconnected\\. .+")
    private val void = Regex("^\\[.] .+ didn't want to live in the same world as\\. .+")
    private val selfVoid = Regex("^\\[.] .+ fell out of the world\\. .+")

    val streaks = hashMapOf<String, Int>()

    fun resetStreaks() {
        streaks.clear()
    }

    fun register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register allowGame@{ message, _ ->
            if (!MCCIslandState.isOnIsland()) return@allowGame true

            if (slainRegex.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.MELEE)
            }

            if (shotRegex.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.RANGE)
            }

            if (explodedRegex.matches(message.string) || explodedSelfRegex.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.EXPLOSION)
            }

            if (lavaRegex.matches(message.string) || lavaSelfRegex.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.LAVA)
            }

            if (potRegex.matches(message.string) || potRegexAlt.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.POTION)
            }

            if (loggedOut.matches(message.string) ||
                loggedOutToGetAway.matches(message.string) ||
                disconnected.matches(message.string) ||
                hasNotRejoined.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.DISCONNECT)
            }

            if (genericDiedRegex.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.GENERIC)
            }

            if (spleefedRegex.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.MELEE)
            }

            if (prickedRegex.matches(message.string) || prickedSelfRegex.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.GENERIC)
            }

            if (void.matches(message.string) || selfVoid.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.VOID)
            }

            if (walkedFire.matches(message.string) ||
                fireSelf.matches(message.string) ||
                burnedRegex.matches(message.string) ||
                burnedSelfRegex.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.GENERIC)
            }

            return@allowGame true
        }
    }

    private fun handleKill(message: Component, method: KillMethod): Boolean {
        val players = cleanupComponent(message)
        if (players.isEmpty()) return true
        val victim = players[0]
        val attacker = players.getOrNull(1)

        /* Call the event for external use */
        KillEvents.KILL.invoker().onKill(KillEvents.KillEventPlayer(
            victim.string,
            victim.style.color?.value ?: fallbackColor
        ), if (attacker == null) null else KillEvents.KillEventPlayer(
            attacker.string,
            attacker.style.color?.value ?: fallbackColor
        ),
            method)

        if (MCCIslandState.game !in listOf(MCCGame.BATTLE_BOX, MCCGame.DYNABALL)) return true

        if (attacker != null) {
    //        Questing
            val self = Minecraft.getInstance().player ?: return true
            if (attacker.string == self.name.string) {
                val game = MCCIslandState.game
                val ctx = GenericCompletionCriteria.playerEliminated(game, sourceTag = "kill") ?: return true
                QuestStorage.applyIncrement(ctx, true)

                if (method == KillMethod.RANGE && game == MCCGame.BATTLE_BOX) {
                    QuestStorage.applyIncrement(QuestIncrementContext(
                        MCCGame.BATTLE_BOX,
                        CompletionCriteria.BATTLE_BOX_QUADS_RANGED_KILLS,
                        1,
                        "bb_ranged_kill"
                    ), true)
                }
            }

            // Streaks
            streaks[attacker.string] = (streaks[attacker.string] ?: 0) + 1

            KillFeedDialog.addKill(
                KillWidget(
                    victim.string,
                    method,
                    attacker.string,
                    getColors(victim, attacker),
                    streak = streaks[attacker.string]!! // This should never fail
                )
            )
        } else {
            val victimColor = victim.style.color?.value?.opacity(128) ?: fallbackColor
            KillFeedDialog.addKill(
                KillWidget(
                    victim.string,
                    method,
                    killColors = Pair(0x606060 opacity 128, victimColor)
                )
            )
        }

        return !Config.KillFeed.hideKills
    }

    private fun getColors(victim: Component, attacker: Component): Pair<Int, Int> {
        val attackerColor = attacker.style.color?.value?.opacity(128) ?: fallbackColor
        val victimColor = victim.style.color?.value?.opacity(128) ?: fallbackColor
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