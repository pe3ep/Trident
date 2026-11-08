package cc.pe3epwithyou.trident.interfaces.hotbar

import cc.pe3epwithyou.trident.feature.chat.dmlock.ReplyLock
import cc.pe3epwithyou.trident.feature.meter.DailyMeter
import cc.pe3epwithyou.trident.mixin.accessors.HudAccessor
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.minecraft
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.world.entity.HumanoidArm

object HotbarDecorationManager {
    private val XP_BONUS_CHARACTERS = mutableSetOf<String>()
    private const val HOTBAR_OFFSET_X = 91
    private const val HOTBAR_OFFSET_Y = 12
    private val decorations = buildSet {
        add(ReplyLock.getDecoration())
        add(DailyMeter.getDecoration())
    }

    fun register() {
        HudElementRegistry.attachElementAfter(
            VanillaHudElements.HOTBAR, Resources.trident("hotbar_deco")
        ) { graphics, deltaTracker ->
            val x0 = graphics.guiWidth() / 2 + HOTBAR_OFFSET_X + 1 + getDynamicOffset()
            val y0 = graphics.guiHeight() - HOTBAR_OFFSET_Y

            var offsetX = 0
            decorations.forEach {
                val decorationWidth = it.render(graphics, deltaTracker, x0 + offsetX, y0)
                offsetX += if (decorationWidth == 0) 0 else (decorationWidth + 1)
            }
        }
    }

    fun getDynamicOffset(): Int {
        var offset = 0
        val minecraft = minecraft()

        val actionbar = (minecraft.gui.hud as? HudAccessor)?.overlayMessageString ?: return 0
        if (containsChar(actionbar.string)) offset += 40

        val player = minecraft.player ?: return offset
        val oppositeHand = player.mainArm.opposite
        if (oppositeHand == HumanoidArm.RIGHT && !player.offhandItem.isEmpty) offset += 29

        return offset
    }

    fun addXPBonusCharacter(char: String) {
        if (XP_BONUS_CHARACTERS.contains(char)) return
        XP_BONUS_CHARACTERS.add(char)
    }

    fun clearXPBonusCharacters() {
        XP_BONUS_CHARACTERS.clear()
    }

    fun containsChar(actionbar: String): Boolean {
        if (XP_BONUS_CHARACTERS.isEmpty()) return false
        for (c in actionbar) {
            if (c.toString() in XP_BONUS_CHARACTERS) return true
        }
        return false
    }
}