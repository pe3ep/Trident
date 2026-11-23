package cc.pe3epwithyou.trident.feature.dojo.hud.states

import cc.pe3epwithyou.trident.feature.dojo.hud.DojoHudState
import cc.pe3epwithyou.trident.feature.dojo.hud.currentTimeMs
import cc.pe3epwithyou.trident.feature.dojo.hud.states.CompletedCourseState.Companion.completedCourseStateFrame
import cc.pe3epwithyou.trident.utils.animation.CubicBezier
import cc.pe3epwithyou.trident.utils.animation.KeyframeAnimator
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedBottom
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.renderWithInverseScissor
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.util.opacity
import kotlinx.serialization.Serializable
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component

@Serializable
data class CourseState(
    val courseId: String,
    val startedAtMs: Long,
    val bestAtStartMs: Long?,
    val completedElapsedMs: Long? = null
) : DojoHudState {

    fun elapsedMs(now: Long = currentTimeMs()): Long {
        return completedElapsedMs ?: (now - startedAtMs).coerceAtLeast(0L)
    }

    override fun renderIdle(
        graphics: GuiGraphicsExtractor, x0: Int, y0: Int
    ): Pair<Int, Int> {
        VanillaState.renderStatic(graphics, x0, y0)

        val x0 = (graphics.guiWidth() - VanillaState.WIDTH) / 2
        val y = y0 + VanillaState.HEIGHT

        // Background
        val bgColor = 0x000000 opacity 77
        graphics.fill(x0, y - 1, x0 + 1, y, bgColor)
        graphics.fill(x0 + VanillaState.WIDTH - 1, y - 1, x0 + VanillaState.WIDTH, y, bgColor)
        graphics.fillRoundedBottom(x0, y, VanillaState.WIDTH, 9, bgColor)

        // Text
        renderText(graphics, x0, y, 1f, this)

        return VanillaState.WIDTH to (VanillaState.HEIGHT + 9)
    }

    override fun transitionTo(
        nextState: DojoHudState, animator: KeyframeAnimator
    ): Boolean {
        when (nextState) {
            HiddenState -> {
                transitionTo(VanillaState, animator)
                VanillaState.transitionTo(nextState, animator)
            }

            VanillaState -> {
                animator.frame(600) {
                    draw { graphics, x0, y0, frameProgress ->
                        VanillaState.renderStatic(graphics, x0, y0)
                        val (_, courseStateHeight) = courseStateFrame(
                            graphics,
                            x0,
                            y0 + VanillaState.HEIGHT,
                            frameProgress,
                            this@CourseState,
                            true
                        )

                        VanillaState.WIDTH to (VanillaState.HEIGHT + courseStateHeight)
                    }
                }
            }

            is CompletedCourseState -> {
                animator.frame(500) {
                    draw { graphics, x0, y0, frameProgress ->
                        VanillaState.renderStatic(graphics, x0, y0)
                        val y = y0 + VanillaState.HEIGHT
                        courseStateFrame(graphics, x0, y, 1f, this@CourseState)

                        completedCourseStateFrame(graphics, x0, y0, frameProgress, true)
                    }
                }
            }

            is CourseState -> {
                // Course -> Course
            }
        }

        return animator.isAnimating()
    }

    companion object {
        fun courseStateFrame(
            graphics: GuiGraphicsExtractor,
            x0: Int,
            y: Int,
            frameProgress: Float,
            courseState: CourseState,
            isInversed: Boolean = false
        ): Pair<Int, Int> {
            val bgColor = 0x000000 opacity 77
            val eased = CubicBezier.SMOOTH.transform(frameProgress)
            val d = if (isInversed) 1f - eased else eased

            val height = (10 * d + 0.5).toInt()

            graphics.renderWithInverseScissor(x0 + 1, y - 1, x0 + VanillaState.WIDTH - 1, y) {
                graphics.fillRoundedBottom(x0, y - 1, VanillaState.WIDTH, height, bgColor)

                graphics.enableScissor(x0, y, x0 + VanillaState.WIDTH, y + height)

                val font = minecraft().font
                val textOpacity = (frameProgress * 1.33f).coerceIn(0f, 1f)
                renderText(
                    graphics,
                    x0,
                    y + height - font.lineHeight - 1,
                    if (isInversed) 1f - textOpacity else textOpacity,
                    courseState
                )

                graphics.disableScissor()
            }


            return VanillaState.WIDTH to height
        }

        private fun renderText(
            graphics: GuiGraphicsExtractor, x0: Int, y: Int, opacity: Float, state: CourseState
        ) {
            val stage = Component.literal(state.courseId.uppercase()).mccFont().withoutShadow()
            val time = Component.literal("56.125".uppercase()).mccFont().withoutShadow()
            val stageDiff =
                //            Component.literal("+12.345".uppercase()).withColor(0xFF1212).mccFont().withoutShadow()
                Component.literal("".uppercase()).withColor(0xFFFFFF).mccFont().withoutShadow()
            //            Component.literal("-0.670".uppercase()).withColor(0x1EFF00).mccFont().withoutShadow()

            val font = minecraft().font
            val white = 0xFFFFFF.opacity((255 * opacity).toInt())

            graphics.text(font, stage, x0 + 2, y, white)
            graphics.text(font, time, x0 + VanillaState.WIDTH - font.width(time) - 1, y, white)
            graphics.centeredText(font, stageDiff, x0 + VanillaState.WIDTH / 2, y, white)
        }
    }
}