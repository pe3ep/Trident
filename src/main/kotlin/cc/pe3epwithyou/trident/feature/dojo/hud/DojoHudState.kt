package cc.pe3epwithyou.trident.feature.dojo.hud

import cc.pe3epwithyou.trident.utils.animation.KeyframeAnimator
import net.minecraft.client.gui.GuiGraphicsExtractor

interface DojoHudState {

    /**
     * Renders this state when there's nothing to animate
     *
     * @return the width and height of the state as [Pair]
     */
    fun renderIdle(
        graphics: GuiGraphicsExtractor,
        x0: Int,
        y0: Int
    ): Pair<Int, Int>

    /**
     * Defines transitions FROM this state.
     *
     * @return true if an animation was added to the [KeyframeAnimator].
     */
    fun transitionTo(
        nextState: DojoHudState,
        animator: KeyframeAnimator
    ): Boolean
}