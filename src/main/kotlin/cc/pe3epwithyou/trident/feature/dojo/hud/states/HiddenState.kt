package cc.pe3epwithyou.trident.feature.dojo.hud.states

import cc.pe3epwithyou.trident.feature.dojo.hud.DojoHudState
import cc.pe3epwithyou.trident.utils.animation.CubicBezier
import cc.pe3epwithyou.trident.utils.animation.KeyframeAnimator
import net.minecraft.client.gui.GuiGraphicsExtractor
import kotlin.math.max
import kotlin.math.roundToInt

object HiddenState : DojoHudState {
    override fun renderIdle(
        graphics: GuiGraphicsExtractor,
        x0: Int,
        y0: Int
    ): Pair<Int, Int> = 0 to 0

    override fun transitionTo(
        nextState: DojoHudState,
        animator: KeyframeAnimator,
        runAfterTransition: () -> Unit
    ): Boolean {
        when (nextState) {
            VanillaState -> {
                // Hidden -> Vanilla
                animator.frame(900) {
                    draw { graphics, x0, y0, frameProgress ->
                        val d = CubicBezier(.3f,1.5f,0f,1f).transform(frameProgress)
                        val offsetY = (y0 + VanillaState.HEIGHT + 4) * d
                        val originY = -VanillaState.HEIGHT - 4

                        val visualHeight = originY + offsetY.roundToInt()

                        val pose = graphics.pose()
                        pose.pushMatrix()

                        val scaleX = CubicBezier.SMOOTH.transform(frameProgress)
                        pose.translate(0f, offsetY)
                        pose.scaleAround(scaleX, 1f, (x0 + VanillaState.WIDTH / 2f), y0 + VanillaState.HEIGHT / 2f)

                        VanillaState.renderStatic(graphics, x0, originY, alpha = (frameProgress * 2).coerceAtMost(1f))

                        pose.popMatrix()

                        VanillaState.WIDTH to max(0, visualHeight + VanillaState.HEIGHT - y0)
                    }
                }
            }
        }

        return animator.isAnimating()
    }
}