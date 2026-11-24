package cc.pe3epwithyou.trident.feature.dojo

import cc.pe3epwithyou.trident.feature.dojo.hud.DojoHudStateMachine
import cc.pe3epwithyou.trident.feature.dojo.hud.states.CompletedCourseState
import cc.pe3epwithyou.trident.feature.dojo.hud.states.CourseState
import cc.pe3epwithyou.trident.feature.dojo.hud.states.HiddenState
import cc.pe3epwithyou.trident.feature.dojo.hud.states.VanillaState
import net.minecraft.client.gui.GuiGraphicsExtractor

// TODO: Add support for Practice mode
object DojoInterface {
    const val UI_OFFSET_Y = 8
    private val stateMachine = DojoHudStateMachine()

    @JvmStatic
    fun extractRenderState(
        graphics: GuiGraphicsExtractor,
        y0: Int
    ): Pair<Int, Int> = stateMachine.render(graphics, y0 - UI_OFFSET_Y)

    @JvmStatic
    fun onRunStarted() {
        stateMachine.requestState(VanillaState)
    }

    @JvmStatic
    fun onRunEnded() {
        DojoStorage.clear()
        stateMachine.requestState(HiddenState)
    }

    @JvmStatic
    fun onCourseStarted(courseId: String) {
        stateMachine.requestState(
            CourseState(
                courseId = courseId,

                // Start 30 ticks earlier because that's how much the course title is delayed by
                startedAtTick = DojoTickCounter.ticks - 30
            )
        )
    }

    @JvmStatic
    fun onCourseLeft() {
        stateMachine.requestState(VanillaState)
    }

    @JvmStatic
    fun onCourseCompleted(): Long? {
        val state = stateMachine.getCourseState() ?: return null

        val elapsedTicks = state.elapsedTicks()
        state.completedElapsedTicks = elapsedTicks

        DojoStorage.recordCoursePersonalBest(state.courseId, elapsedTicks)
        stateMachine.requestState(CompletedCourseState(state))

        return elapsedTicks
    }

    fun getCurrentCourseState() = stateMachine.getCourseState()
}