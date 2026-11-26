package cc.pe3epwithyou.trident.feature.dojo.hud.states

import cc.pe3epwithyou.trident.feature.dojo.hud.DojoHudState
import cc.pe3epwithyou.trident.utils.animation.CubicBezier
import cc.pe3epwithyou.trident.utils.animation.KeyframeAnimator
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedBottom
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.renderWithInverseScissor
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import kotlin.math.roundToInt

data class CompletedCourseState(val prevState: CourseState) : DojoHudState {
    override fun renderIdle(
        graphics: GuiGraphicsExtractor, x0: Int, y0: Int
    ): Pair<Int, Int> {
        VanillaState.renderStatic(graphics, x0, y0)
        val x0 = (graphics.guiWidth() - VanillaState.WIDTH) / 2
        val y = y0 + VanillaState.HEIGHT

        val bgColor = getColor(prevState).opaqueColor()
        graphics.fill(x0, y - 1, x0 + 1, y, bgColor)
        graphics.fill(x0 + VanillaState.WIDTH - 1, y - 1, x0 + VanillaState.WIDTH, y, bgColor)
        graphics.fillRoundedBottom(x0, y, VanillaState.WIDTH, 9, bgColor)

        val font = minecraft().font
        val text = Component.literal("COMPLETED").mccFont().withoutShadow()
        graphics.centeredText(font, text, x0 + VanillaState.WIDTH / 2, y, 0xFFFFFF.opaqueColor())

        return VanillaState.WIDTH to (VanillaState.HEIGHT + 9)
    }

    override fun transitionTo(
        nextState: DojoHudState,
        animator: KeyframeAnimator,
        runAfterTransition: () -> Unit
    ): Boolean {
        when (nextState) {
            is CourseState -> {
                // Completed -> Course
                animator.frame(500) {
                    onFrameEnd(runAfterTransition)
                    draw { graphics, x0, y0, frameProgress ->
                        VanillaState.renderStatic(graphics, x0, y0)
                        val y = y0 + VanillaState.HEIGHT
                        CourseState.courseStateFrame(graphics, x0, y, 1f, nextState)
                        completedCourseStateFrame(graphics, x0, y0, frameProgress, prevState)
                    }
                }
            }

            VanillaState -> {
                // Completed -> Vanilla
                animator.frame(600) {
                    onFrameEnd(runAfterTransition)
                    draw { graphics, x0, y0, frameProgress ->
                        VanillaState.renderStatic(graphics, x0, y0)

                        val y = y0 + VanillaState.HEIGHT
                        val smooth = CubicBezier.SMOOTH.transform(frameProgress)
                        val d = (1f - smooth).coerceIn(0f, 1f)


                        val height = (10 * d).roundToInt()

                        graphics.renderWithInverseScissor(
                            x0 + 1,
                            y - 1,
                            x0 + VanillaState.WIDTH - 1,
                            y
                        ) {
                            graphics.fillRoundedBottom(
                                x0,
                                y - 1,
                                VanillaState.WIDTH,
                                height,
                                getColor(prevState).opaqueColor()
                            )
                        }

                        graphics.enableScissor(x0, y, x0 + VanillaState.WIDTH, y + height - 1)

                        val text = Component.literal("COMPLETED").mccFont().withoutShadow()
                        val font = minecraft().font
                        graphics.centeredText(
                            font,
                            text,
                            x0 + VanillaState.WIDTH / 2,
                            y + height - font.lineHeight - 1,
                            0xFFFFFF.opaqueColor()
                        )

                        graphics.disableScissor()


                        VanillaState.WIDTH to VanillaState.HEIGHT + height
                    }
                }
            }

            HiddenState -> {
                // Completed -> Hidden
                animator.frame(300) {
                    onFrameEnd(runAfterTransition)
                    draw { graphics, x0, y0, frameProgress ->
                        VanillaState.renderStatic(graphics, x0, y0, alpha = 1f - frameProgress)
                        completedCourseStateFrame(graphics, x0, y0, 0f, prevState, alpha = 1f - frameProgress)
                    }
                }
                animator.frame(300) {
                    onFrameEnd(runAfterTransition)
                    draw { _, _, _, frameProgress -> VanillaState.WIDTH to ((VanillaState.HEIGHT + 9) * (1f - (CubicBezier.SMOOTH.transform(frameProgress)))).roundToInt() }
                }
            }
        }

        return animator.isAnimating()
    }

    companion object {
        private const val BONUS_COLOR: Int = 0xc700ff
        private const val MAIN_COLOR: Int = 0x29974c

        fun getColor(courseState: CourseState): Int = if (courseState.courseId.startsWith("M")) MAIN_COLOR else BONUS_COLOR

        // this is a mess XD
        fun completedCourseStateFrame(
            graphics: GuiGraphicsExtractor,
            x0: Int,
            y0: Int,
            frameProgress: Float,
            courseState: CourseState,
            isReversed: Boolean = false,
            alpha: Float = 1f
        ): Pair<Int, Int> {
            val y = y0 + VanillaState.HEIGHT

            // Overlay
            val bgColor = getColor(courseState).opacity((255 * alpha).toInt())

            val transformed = CubicBezier.SMOOTH.transform(frameProgress)
            val d = 1f - transformed

            val width = (VanillaState.WIDTH * d).roundToInt()

            graphics.renderWithInverseScissor(x0 + 1, y - 1, x0 + VanillaState.WIDTH - 1, y) {
                if (isReversed) {
                    graphics.fillRoundedBottom(
                        x0 + width,
                        y - 1,
                        VanillaState.WIDTH - width,
                        10,
                        bgColor
                    )

                } else {
                    graphics.fillRoundedBottom(x0, y - 1, width, 10, bgColor)
                }
            }

            // Text
            if (isReversed) {
                graphics.enableScissor(x0 + width, y, x0 + VanillaState.WIDTH, y + 9)
            } else {
                graphics.enableScissor(x0, y, x0 + width, y + 9)
            }

            val font = minecraft().font
            val text = Component.literal("COMPLETED").mccFont().withoutShadow()
            graphics.centeredText(
                font,
                text,
                x0 + VanillaState.WIDTH / 2,
                y,
                0xFFFFFF.opacity((255 * alpha).toInt())
            )

            graphics.disableScissor()


            return VanillaState.WIDTH to (VanillaState.HEIGHT + 9)
        }


    }

}
