package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.dialogs.killfeed.KillFeedDialog
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.widgets.killfeed.KillMethod
import cc.pe3epwithyou.trident.widgets.killfeed.KillType
import cc.pe3epwithyou.trident.widgets.killfeed.KillWidget
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.scores.DisplaySlot

object KillChatListener {
    private val fallbackColor = 0xFFFFFF opacity 128

    private val slainRegex = Regex("^\\[.] .+ (was slain by) .+")
    private val shotRegex = Regex("^\\[.] .+ (was shot by) .+")
    private val explodedRegex = Regex("^\\[.] .+ (was blown up by) .+")
    private val lavaRegex = Regex("^\\[.] .+ (tried to swim in lava to escape) .+")
    private val potRegex = Regex("^\\[.] .+ was eliminated with magic by .+ using .+")
    private val potRegexAlt = Regex("^\\[.] .+ was hit by .+")
    private val loggedOut = Regex("^\\[.] .+ logged out\\.")

    fun register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register allowGame@{ message, _ ->
            if (!MCCIslandState.isOnIsland()) return@allowGame true
            if (MCCIslandState.game !in listOf(MCCGame.BATTLE_BOX, MCCGame.DYNABALL)) return@allowGame true

            if (slainRegex.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.MELEE)
            }

            if (shotRegex.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.RANGE)
            }

            if (explodedRegex.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.EXPLOSION)
            }

            if (lavaRegex.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.LAVA)
            }

            if (potRegex.matches(message.string) || potRegexAlt.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.POTION)
            }

            if (loggedOut.matches(message.string)) {
                return@allowGame handleKill(message, KillMethod.DISCONNECT)
            }

            return@allowGame true
        }
    }

    private fun handleKill(message: Component, method: KillMethod): Boolean {
        val players = cleanupComponent(message)
        if (players.isEmpty()) return true
        val victim = players[0]
        val attacker = players.getOrNull(1)
        if (attacker != null) {
            KillFeedDialog.addKill(
                KillWidget(
                    victim.string,
                    method,
                    attacker.string,
                    getColors(victim, attacker)
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