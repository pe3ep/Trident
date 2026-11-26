package cc.pe3epwithyou.trident.feature.dojo

import cc.pe3epwithyou.trident.feature.dojo.hud.DojoHudStateMachine
import cc.pe3epwithyou.trident.feature.dojo.hud.states.CompletedCourseState
import cc.pe3epwithyou.trident.feature.dojo.hud.states.CourseState
import cc.pe3epwithyou.trident.feature.dojo.hud.states.HiddenState
import cc.pe3epwithyou.trident.feature.dojo.hud.states.VanillaState
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.ScoreboardUtils
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor

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
        val courseName =
            ScoreboardUtils.findInScoreboard(Regex("""COURSE: (.+)"""))?.groupValues?.getOrNull(1)
                ?: return
        val courseType = if (courseName.lowercase()
                .startsWith("daily")
        ) DojoCourseType.DAILY else DojoCourseType.MONTHLY
        DojoStorage.startRun(courseType, courseName)
        stateMachine.requestState(VanillaState)
    }

    @JvmStatic
    fun onRunEnded() {
        DojoStorage.saveRun()
        stateMachine.requestState(HiddenState) {
            DojoStorage.clear()
        }
    }

    @JvmStatic
    fun onCourseStarted(courseId: String, courseName: String, courseDifficulty: CourseDifficulty) {
        if (DojoStorage.getCurrentRun() == null) return
        val courseState = CourseState(
            courseId = courseId, courseName = courseName, courseDifficulty = courseDifficulty,

            // Start 30 ticks earlier because that's how much the course title is delayed by
            startedAtTick = DojoTickCounter.ticks - 30
        )
        DojoStorage.setCourseState(courseState)
        stateMachine.requestState(courseState)
    }

    @JvmStatic
    fun onCourseLeft() {
        DojoStorage.setCourseState(null)
        stateMachine.requestState(VanillaState)
    }

    @JvmStatic
    fun onCourseCompleted(): CourseState? {
        DojoStorage.incrementCollectedMedals()
        val state = DojoStorage.getCurrentRun()?.courseState?.let {
            // Hard ending
            if (it.courseId == "B4-1") {
                return@let it.copy(courseId = "B4-${it.courseDifficulty.ordinal + 1}")
            }

            it
        } ?: return null

        val elapsedTicks = state.elapsedTicks()
        state.completedElapsedTicks = elapsedTicks


        val brandColor = 0x65FFFF
        Logger.sendMessage(
            Component.literal("[").withColor(brandColor).append(
                FontCollection.get(Game.PARKOUR_WARRIOR_DOJO.icon).withColor(TextColor.WHITE)
            ).append(Component.literal("] Finished ").withColor(brandColor))
                .append(Component.literal("[${state.courseId}] ").withColor(TextColor.GRAY)).append(
                    Component.literal(state.courseName)
                        .withColor(state.courseDifficulty.textColor())
                ).append(Component.literal(" in ").withColor(brandColor)).append(
                    Component.literal(
                        DojoStorage.formattedTime(elapsedTicks, true)
                    )
                ).apply {
                    if (state.bestTime != null) {
                        append(
                            Component.literal(" (").withColor(brandColor)
                                .append(state.getFullStageDiff())
                                .append(Component.literal(")").withColor(brandColor))
                        )
                    }
                }, false
        )

        DojoStorage.recordCoursePersonalBest(state.courseId, elapsedTicks)
        DojoStorage.saveRun()
        DojoStorage.setCourseState(null)
        stateMachine.requestState(CompletedCourseState(state))

        return state
    }
}