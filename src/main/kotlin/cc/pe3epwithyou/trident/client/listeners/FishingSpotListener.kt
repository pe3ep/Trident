package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.client.events.FishingSpotEvents
import cc.pe3epwithyou.trident.feature.fishing.FishingSpotParser
import cc.pe3epwithyou.trident.state.fishing.Perk
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.phys.AABB

object FishingSpotListener {
    data class FishingSpot(val x: Double, val y: Double, val perks: List<Pair<Perk, Double>>) {
        override fun hashCode(): Int {
            var result = x.hashCode()
            result = 31 * result + y.hashCode()
            result = 31 * result + perks.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as FishingSpot

            if (x != other.x) return false
            if (y != other.y) return false
            if (perks != other.perks) return false

            return true
        }
    }

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
        if (spot != null && (currentSpot == null || spot != currentSpot)) {
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
        val perks = FishingSpotParser.parse(display.text)
        if (perks.isEmpty()) return null
        val spot = FishingSpot(display.x, display.y, perks)
        return spot
    }


}