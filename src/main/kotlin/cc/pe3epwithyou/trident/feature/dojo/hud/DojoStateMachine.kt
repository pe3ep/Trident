package cc.pe3epwithyou.trident.feature.dojo.hud

import cc.pe3epwithyou.trident.feature.dojo.hud.states.CourseState
import cc.pe3epwithyou.trident.feature.dojo.hud.states.HiddenState
import cc.pe3epwithyou.trident.feature.dojo.hud.states.VanillaState
import cc.pe3epwithyou.trident.utils.animation.KeyframeAnimator
import net.minecraft.client.gui.GuiGraphicsExtractor

class DojoHudStateMachine {

    private var currentState: DojoHudState = HiddenState
    private var pendingState: DojoHudState? = null
    private var isTransitioning = false

    private val animator = KeyframeAnimator()

    fun requestState(nextState: DojoHudState) {
        if (isTransitioning) {
            pendingState = nextState
            return
        }

        beginTransition(nextState)
    }

    fun getCourseState() = currentState as? CourseState

    private fun beginTransition(nextState: DojoHudState) {
        if (currentState == nextState) return

        animator.clear()
        val animated = currentState.transitionTo(nextState, animator)

        if (animated) {
            transitionTarget = nextState
            isTransitioning = true
        } else {
            currentState = nextState
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
                beginTransition(pending)
            }
        }

        return currentState.renderIdle(graphics, x0, y0)
    }

    private var transitionTarget: DojoHudState = HiddenState

    fun currentCourseElapsedMs(): Long? = getCourseState()?.elapsedMs()
}