package cc.pe3epwithyou.trident.feature.dojo

import cc.pe3epwithyou.trident.feature.dojo.hud.DojoHudStateMachine
import cc.pe3epwithyou.trident.feature.dojo.hud.states.CourseState
import cc.pe3epwithyou.trident.feature.dojo.hud.states.HiddenState
import cc.pe3epwithyou.trident.feature.dojo.hud.states.VanillaState
import cc.pe3epwithyou.trident.feature.dojo.hud.currentTimeMs
import cc.pe3epwithyou.trident.feature.dojo.hud.states.CompletedCourseState
import net.minecraft.client.gui.GuiGraphicsExtractor

object DojoInterface {

    const val UI_OFFSET_Y = 8

    private val stateMachine = DojoHudStateMachine()

    @JvmStatic
    fun extractRenderState(
        graphics: GuiGraphicsExtractor,
        y0: Int
    ): Pair<Int, Int> {
        return stateMachine.render(
            graphics,
            y0 - UI_OFFSET_Y
        )
    }

    @JvmStatic
    fun onRunStarted() {
        stateMachine.requestState(VanillaState)
    }

    @JvmStatic
    fun onRunEnded() {
        stateMachine.requestState(HiddenState)
    }

    @JvmStatic
    fun onCourseStarted(courseId: String) {
        stateMachine.requestState(
            CourseState(
                courseId = courseId,
                startedAtMs = currentTimeMs(),
                bestAtStartMs = DojoStorage.bestTimeMs(courseId)
            )
        )
    }

    @JvmStatic
    fun onCourseLeft() {
        stateMachine.requestState(VanillaState)
    }

    @JvmStatic
    fun onCourseCompleted(courseId: String): Boolean {
        val state = stateMachine.getCourseState() ?: return false
        val time = state.elapsedMs()

        DojoStorage.markCourseCompleted(courseId)
        DojoStorage.recordBestTimeMs(courseId, time)

        stateMachine.requestState(CompletedCourseState(state))

        return true
    }

    fun getCourseState() = stateMachine.getCourseState()
}