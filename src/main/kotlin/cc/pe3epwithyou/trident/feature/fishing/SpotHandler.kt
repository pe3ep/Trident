package cc.pe3epwithyou.trident.feature.fishing

import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.phys.AABB

object SpotHandler {
    data class FishingSpot(val x: Double, val y: Double, val perks: List<Pair<Augment, Int>>)

    fun handle() {
        val client = Minecraft.getInstance()
        val hook = client.player?.fishing ?: return
        if (!hook.isInLiquid) return
        val spot = findNearestSpot(hook)
    }

    private fun findNearestSpot(hook: FishingHook): FishingSpot? {
        val client = Minecraft.getInstance()
        val blockPos = hook.onPos
        val box = AABB.ofSize(blockPos.center, 3.5, 6.0, 3.5)
        val level = client.level ?: return null
        val entities = level.getEntities(null, box).filter { entity -> entity is Display.TextDisplay }
        if (entities.isEmpty()) return null
        val display: Display.TextDisplay = entities.first() as Display.TextDisplay
        ChatUtils.debugLog(display.text.string)
        return null
    }
}