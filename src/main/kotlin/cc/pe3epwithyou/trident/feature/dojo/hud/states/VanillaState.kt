package cc.pe3epwithyou.trident.feature.dojo.hud.states

import cc.pe3epwithyou.trident.feature.dojo.DojoStorage
import cc.pe3epwithyou.trident.feature.dojo.hud.DojoHudState
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.animation.CubicBezier
import cc.pe3epwithyou.trident.utils.animation.KeyframeAnimator
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.client.gui.GuiGraphicsExtractor
import kotlin.math.roundToInt

object VanillaState : DojoHudState {

    const val WIDTH = 130
    const val HEIGHT = 14

    // TODO: Make each asset use Trident's textures and add an asset for "currently on this course"
    private object Assets {

        private fun bonusPointAsset(completedPoints: Int): Texture {
            require(completedPoints in 0..3)

            val path = if (completedPoints == 0) {
                "textures/_fonts/icon/parkour_warrior/point_empty_bonus.png"
            } else {
                "textures/_fonts/icon/parkour_warrior/point_bonus_$completedPoints.png"
            }

            return Texture(
                location = Resources.mcc(path),
                width = 8,
                height = 14,
                textureWidth = 16,
                textureHeight = 28
            )
        }

        private val bonusPoints = Array(4) {
            bonusPointAsset(it)
        }

        private val mainEmpty = Texture(
            location = Resources.mcc(
                "textures/_fonts/icon/parkour_warrior/point_empty.png"
            ),
            width = 8,
            height = 8,
            textureWidth = 16,
            textureHeight = 16
        )

        private val mainFull = Texture(
            location = Resources.mcc(
                "textures/_fonts/icon/parkour_warrior/point_full.png"
            ),
            width = 8,
            height = 8,
            textureWidth = 16,
            textureHeight = 16
        )

        fun bonusPoints(completed: Int) = bonusPoints[completed]

        fun mainPoint(completed: Boolean) =
            if (completed) mainFull else mainEmpty
    }

    override fun renderIdle(
        graphics: GuiGraphicsExtractor,
        x0: Int,
        y0: Int
    ): Pair<Int, Int> {
        renderStatic(graphics, x0, y0)
        return WIDTH to HEIGHT
    }

    // TODO: Add the completion type icon at the end
    internal fun renderStatic(
        graphics: GuiGraphicsExtractor,
        x0: Int,
        y0: Int
    ) {
        val bg = 0x000000.opacity(128)
        val bgAlt = 0x000000.opacity(32)

        graphics.fillRoundedAll(x0, y0, WIDTH, HEIGHT, bg)

        graphics.fillRoundedAll(x0, y0, 10, HEIGHT, bgAlt)
        graphics.fillRoundedAll(x0 + 40, y0, 10, HEIGHT, bgAlt)
        graphics.fillRoundedAll(x0 + 80, y0, 10, HEIGHT, bgAlt)
        graphics.fillRoundedAll(x0 + 120, y0, 10, HEIGHT, bgAlt)

        Assets.bonusPoints(DojoStorage.completedCourseCount("B1"))
            .blit(graphics, x0 + 1, y0)

        Assets.bonusPoints(DojoStorage.completedCourseCount("B2"))
            .blit(graphics, x0 + 41, y0)

        Assets.bonusPoints(DojoStorage.completedCourseCount("B3"))
            .blit(graphics, x0 + 81, y0)

        fun drawMainPath(x: Int, completed: Int) {
            require(completed in 0..3)

            Assets.mainPoint(completed >= 1).blit(graphics, x, y0 + 3)
            Assets.mainPoint(completed >= 2).blit(graphics, x + 9, y0 + 3)
            Assets.mainPoint(completed >= 3).blit(graphics, x + 18, y0 + 3)
        }

        drawMainPath(x0 + 12, DojoStorage.completedCourseCount("M1"))
        drawMainPath(x0 + 52, DojoStorage.completedCourseCount("M2"))
        drawMainPath(x0 + 92, DojoStorage.completedCourseCount("M3"))
    }

    override fun transitionTo(
        nextState: DojoHudState,
        animator: KeyframeAnimator
    ): Boolean {
        when (nextState) {
            HiddenState -> {
                // Vanilla -> Hidden
                animator.frame(600) {
                    draw { graphics, _, y0, frameProgress ->
                        val d = (1f - CubicBezier.SMOOTH.transform(frameProgress))
                        val width = (WIDTH * d).roundToInt()
                        val height = (HEIGHT * d).roundToInt()

                        val x = (graphics.guiWidth() - width) / 2
                        graphics.fillRoundedAll(x, y0, width, height, 0x000000.opacity((128 * d).roundToInt()))

                        width to height
                    }
                }
            }

            is CourseState -> {
                // Vanilla -> Course
                animator.frame(600) {
                    draw { graphics, x0, y0, frameProgress ->
                        renderStatic(graphics, x0, y0)
                        val (_, courseHeight) = CourseState.courseStateFrame(graphics, x0, y0 + HEIGHT, frameProgress,
                            nextState)

                        WIDTH to HEIGHT + courseHeight
                    }

                }
            }
        }

        return animator.isAnimating()
    }
}