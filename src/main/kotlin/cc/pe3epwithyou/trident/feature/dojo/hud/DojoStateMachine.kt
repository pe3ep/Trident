package cc.pe3epwithyou.trident.feature.dojo.hud

import cc.pe3epwithyou.trident.feature.dojo.hud.states.CourseState
import cc.pe3epwithyou.trident.feature.dojo.hud.states.HiddenState
import cc.pe3epwithyou.trident.feature.dojo.hud.states.VanillaState
import cc.pe3epwithyou.trident.utils.animation.KeyframeAnimator
import net.minecraft.client.gui.GuiGraphicsExtractor

class DojoHudStateMachine {

    private var currentState: DojoHudState = HiddenState
    private var pendingState: Pair<DojoHudState, () -> Unit>? = null
    private var isTransitioning = false

    private val animator = KeyframeAnimator()

    fun requestState(nextState: DojoHudState, runAfterTransition: () -> Unit = {}) {
        if (isTransitioning) {
            pendingState = nextState to runAfterTransition
            return
        }

        beginTransition(nextState, runAfterTransition)
    }

    fun getCourseState() = currentState as? CourseState

    private fun beginTransition(nextState: DojoHudState, runAfterTransition: () -> Unit = {}) {
        if (currentState == nextState) return

        animator.clear()
        val animated = currentState.transitionTo(nextState, animator, runAfterTransition)

        if (animated) {
            transitionTarget = nextState
            isTransitioning = true
        } else {
            currentState = nextState
            runAfterTransition()
        }
    }

    fun render(
        graphics: GuiGraphicsExtractor,
        y0: Int
    ): Pair<Int, Int> {
        val x0 = (graphics.guiWidth() - VanillaState.WIDTH) / 2

        if (isTransitioning) {
            val size = animator.render(graphics, x0, y0)

            if (size != null) {
                return size
            }

            isTransitioning = false

            currentState = transitionTarget

            val pending = pendingState
            pendingState = null

            if (pending != null) {
                beginTransition(pending.first, pending.second)
            }
        }

        return currentState.renderIdle(graphics, x0, y0)
    }

    private var transitionTarget: DojoHudState = HiddenState
}