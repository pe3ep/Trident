package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.ItemParser
import cc.pe3epwithyou.trident.utils.ChatUtils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.phys.AABB

object SpotEntityListener : ClientTickEvents.EndTick {
    private const val RADIUS: Double = 2.0
    private const val Y_RADIUS: Double = 6.0
    private var lastDebugLogGameTime: Long = 0

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register(this)
    }

    override fun onEndTick(client: Minecraft) {
        if (!MCCIState.isOnIsland()) return
        val level = client.level ?: return
        val player = client.player ?: return

        if (!isFishing(player)) return
        val display = findDisplay(player.fishing) ?: return

        val text = display.text.string
        val lines = text.replace("\r\n", "\n").replace('\r', '\n').split("\n")
        val parsed = ItemParser.parseSpotBonuses(lines)

        // Rate-limited debug log (once per second)
        if (level.gameTime - lastDebugLogGameTime >= 20L) {
            lastDebugLogGameTime = level.gameTime
            val hookStr = parsed.hookPercents.entries.joinToString(", ") { "${it.key.name}:${"""%.2f""".format(it.value)}%" }
            val magStr = parsed.magnetPercents.entries.joinToString(", ") { "${it.key.name}:${"""%.2f""".format(it.value)}%" }
            ChatUtils.debugLog(
                """
                Spot Text:\n${text}
                Parsed Spot Bonuses:
                Hooks: ${hookStr.ifEmpty { "-" }}
                Magnets: ${magStr.ifEmpty { "-" }}
                FishChance:+${"""%.2f""".format(parsed.fishChanceBonusPercent)}%
                Elusive:+${"""%.2f""".format(parsed.elusiveChanceBonusPercent)}%  Pearl:+${"""%.2f""".format(parsed.pearlChanceBonusPercent)}%
                Treasure:+${"""%.2f""".format(parsed.treasureChanceBonusPercent)}%  Spirit:+${"""%.2f""".format(parsed.spiritChanceBonusPercent)}%
                Wayfinder:+${"""%.2f""".format(parsed.wayfinderDataBonus)}
                """.trimIndent()
            )
        }

        val st = TridentClient.playerState.spot
        st.hasSpot = true
        st.hookPercents.clear(); st.hookPercents.putAll(parsed.hookPercents)
        st.magnetPercents.clear(); st.magnetPercents.putAll(parsed.magnetPercents)
        st.elusiveChanceBonusPercent = parsed.elusiveChanceBonusPercent
        st.pearlChanceBonusPercent = parsed.pearlChanceBonusPercent
        st.treasureChanceBonusPercent = parsed.treasureChanceBonusPercent
        st.spiritChanceBonusPercent = parsed.spiritChanceBonusPercent
        st.wayfinderDataBonus = parsed.wayfinderDataBonus
        st.fishChanceBonusPercent = parsed.fishChanceBonusPercent

        DialogCollection.refreshDialog("hookchances")
        DialogCollection.refreshDialog("magnetchances")
        DialogCollection.refreshDialog("chanceperks")
        DialogCollection.refreshDialog("spot")
    }

    fun findDisplay(fishingHook: FishingHook?): Display.TextDisplay? {
        if (fishingHook == null) {
            clearSpot()
            return null
        }
        // Find any TextDisplay entity near the fishing hook entity
        val aabb = AABB(fishingHook.x - RADIUS, fishingHook.y - Y_RADIUS, fishingHook.z - RADIUS, fishingHook.x + RADIUS, fishingHook.y + Y_RADIUS, fishingHook.z + RADIUS)
        val displays = fishingHook.level().getEntities(null, aabb) { e: Entity -> e.type == EntityType.TEXT_DISPLAY }
        val display = displays.minByOrNull { it.distanceToSqr(fishingHook) } as? Display.TextDisplay ?: run {
            clearSpot()
            return null
        }
        return display
    }

    fun isFishing(player: LocalPlayer): Boolean {
        val fishingHook = player.fishing;
        if (fishingHook == null) {
            clearSpot()
            return false
        }
        return true
    }

    private fun clearSpot() {
        val st = TridentClient.playerState.spot
        if (!st.hasSpot && st.hookPercents.isEmpty() && st.magnetPercents.isEmpty() && st.elusiveChanceBonusPercent == 0.0 && st.pearlChanceBonusPercent == 0.0 && st.treasureChanceBonusPercent == 0.0 && st.spiritChanceBonusPercent == 0.0 && st.wayfinderDataBonus == 0.0 && st.fishChanceBonusPercent == 0.0) return
        // Rate-limited debug log for clearing
        Minecraft.getInstance().level?.let { lvl ->
            if (lvl.gameTime - lastDebugLogGameTime >= 20L) {
                lastDebugLogGameTime = lvl.gameTime
                ChatUtils.debugLog("Clearing SpotState (no nearby TextDisplay or not fishing)")
            }
        }
        st.hasSpot = false
        st.hookPercents.clear()
        st.magnetPercents.clear()
        st.elusiveChanceBonusPercent = 0.0
        st.pearlChanceBonusPercent = 0.0
        st.treasureChanceBonusPercent = 0.0
        st.spiritChanceBonusPercent = 0.0
        st.wayfinderDataBonus = 0.0
        st.fishChanceBonusPercent = 0.0
        DialogCollection.refreshDialog("hookchances")
        DialogCollection.refreshDialog("magnetchances")
        DialogCollection.refreshDialog("chanceperks")
        DialogCollection.refreshDialog("spot")
    }
}


