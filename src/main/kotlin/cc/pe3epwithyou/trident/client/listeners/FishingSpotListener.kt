package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.client.events.FishingSpotEvents
import cc.pe3epwithyou.trident.state.fishing.Augment
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.phys.AABB

object FishingSpotListener {
    data class FishingSpot(val x: Double, val y: Double, val perks: List<Pair<Augment, Double>>)

    /**
     * If this is null then player is not currently fishing.
     * Used to prevent new spots being 'detected' every tick.
     **/
    var currentSpot: FishingSpot? = null

    fun handle() {
        val client = Minecraft.getInstance()
        val hook = client.player?.fishing
        if (hook == null || !hook.isInLiquid) {
            return
        }
        val spot = findNearestSpot(hook)
        if (spot == null && currentSpot != null) {
            currentSpot = null
            return
        }
        if (spot != null && (currentSpot == null || currentSpot != spot)) {
            currentSpot = spot
            FishingSpotEvents.CAST.invoker().onCast(spot)
        }
    }

    private fun findNearestSpot(hook: FishingHook): FishingSpot? {
        val client = Minecraft.getInstance()
        val blockPos = hook.onPos
        val box = AABB.ofSize(blockPos.center, 3.5, 6.0, 3.5)
        val level = client.level ?: return null
        val entities = level.getEntities(null, box).filter { entity -> entity is Display.TextDisplay }
        if (entities.isEmpty()) return null
        val display: Display.TextDisplay = entities.first() as Display.TextDisplay

        // Temporary fake perk list due to display parser not existing yet
        val debugAugments = listOf(Pair(Augment.FISH_MAGNET, 0.3))
        val spot = FishingSpot(display.x, display.y, debugAugments)
        return spot
    }
}