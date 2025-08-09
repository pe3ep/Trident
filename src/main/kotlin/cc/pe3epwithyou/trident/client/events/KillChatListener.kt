package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.dialogs.killfeed.KillFeedDialog
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.widgets.killfeed.KillMethod
import cc.pe3epwithyou.trident.widgets.killfeed.KillType
import cc.pe3epwithyou.trident.widgets.killfeed.KillWidget
import com.noxcrew.sheeplib.util.opaqueColor
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.scores.DisplaySlot

object KillChatListener {
    private val fallbackColor = 0xFFFFFF.opaqueColor()
    var teamColor: Int = fallbackColor
    private var isEnabled = true
//        Round 1 Over!

    private val slainRegex = Regex("^\\[.] .+ (was slain by) .+")
    private val shotRegex = Regex("^\\[.] .+ (was shot by) .+")
    private val explodedRegex = Regex("^\\[.] .+ (was blown up by) .+")
    private val lavaRegex = Regex("^\\[.] .+ (tried to swim in lava to escape) .+")
    private val potRegex = Regex("^\\[.] .+ was eliminated with magic by .+ using .+")
    private val potRegexAlt = Regex("^\\[.] .+ was hit by .+")
    private val loggedOut = Regex("^\\[.] .+ logged out")

    private val spectatingRegex = Regex("You are now spectating another team\\.")
    private val roundOverRegex = Regex("^\\[.] Round \\d Over!")

    private fun checkScoreboard() {
        val player = Minecraft.getInstance().player!!
        val scoreboard = player.scoreboard
        val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return
        val scores = scoreboard.listPlayerScores(objective)
        scores.forEach {
            val component = it.display ?: return@forEach
            if (player.name.string !in component.string) return@forEach
            val cleaned = cleanupComponent(component).first()
            if (Config.Debug.enableLogging) {
                ChatUtils.sendMessage(Component.literal("Cleaned Name from scoreboard: ").append(cleaned))
            }
            teamColor = cleaned.style.color?.value ?: fallbackColor
            return
        }
    }

    fun register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register allowGame@{ message, _ ->
            if (!MCCIslandState.isOnIsland()) return@allowGame true
            if (MCCIslandState.game !in listOf(MCCGame.BATTLE_BOX, MCCGame.DYNABALL)) return@allowGame true

            if (spectatingRegex.matches(message.string) && MCCIslandState.game == MCCGame.BATTLE_BOX) {
                isEnabled = false
            }

            if (roundOverRegex.matches(message.string) && MCCIslandState.game == MCCGame.BATTLE_BOX) {
                isEnabled = true
            }

            if (!isEnabled) return@allowGame true

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

//            if (loggedOut.matches(message.string)) {
//                return@allowGame handleKill(message, KillMethod.DISCONNECT)
//            }

            return@allowGame true
        }
    }

    private fun handleKill(message: Component, method: KillMethod): Boolean {
        checkScoreboard()
        val players = cleanupComponent(message)
        val victim = players[0]
        val attacker = players[1]
        val type = getType(message, attacker)

        KillFeedDialog.addKill(
            KillWidget(
                victim.string,
                method,
                attacker.string,
                type
            )
        )
        return !Config.KillFeed.hideKills
    }

    private fun getType(message: Component, attacker: Component): KillType {
        val type: KillType
        val self = Minecraft.getInstance().player!!
        if (Config.Debug.enableLogging) {
            ChatUtils.sendMessage("""
                AttackerColorInt: ${attacker.style.color?.value}
                CapturedTeamColor: $teamColor
            """.trimIndent())
        }

        type = if (self.name.string in message.string) {
            if (attacker.string == self.name.string) {
                KillType.SELF_ENEMY
            } else {
                KillType.ENEMY_SELF
            }
        } else {
            if (attacker.style.color?.value == teamColor) {
                KillType.TEAM_ENEMY
            } else {
                KillType.ENEMY_TEAM
            }
        }

        return type
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