package cc.pe3epwithyou.trident.feature.dojo.hud.states

import cc.pe3epwithyou.trident.feature.dojo.hud.DojoHudState
import cc.pe3epwithyou.trident.utils.animation.KeyframeAnimator
import net.minecraft.client.gui.GuiGraphicsExtractor

object HiddenState : DojoHudState {

    override fun renderIdle(
        graphics: GuiGraphicsExtractor,
        x0: Int,
        y0: Int
    ): Pair<Int, Int> = 0 to 0

    override fun transitionTo(
        nextState: DojoHudState,
        animator: KeyframeAnimator
    ): Boolean {
        when (nextState) {
            VanillaState -> {
                // Hidden -> Vanilla

                // TODO: Add a smooth opening animation for the UI
            }
        }

        return animator.isAnimating()
    }
}