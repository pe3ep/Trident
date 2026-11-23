package cc.pe3epwithyou.trident.feature.dojo.hud

import cc.pe3epwithyou.trident.utils.animation.KeyframeAnimator
import net.minecraft.client.gui.GuiGraphicsExtractor

interface DojoHudState {

    fun renderIdle(
        graphics: GuiGraphicsExtractor,
        x0: Int,
        y0: Int
    ): Pair<Int, Int>

    /**
     * Define transitions FROM this state.
     *
     * @return true if an animation was added to the animator.
     */
    fun transitionTo(
        nextState: DojoHudState,
        animator: KeyframeAnimator
    ): Boolean
}

internal fun currentTimeMs(): Long =
    System.nanoTime() / 1_000_000L