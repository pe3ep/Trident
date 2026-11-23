package cc.pe3epwithyou.trident.feature.meter.daily

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.meter.Meter
import cc.pe3epwithyou.trident.interfaces.hotbar.HotbarDecoration
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Model
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.animation.CubicBezier
import cc.pe3epwithyou.trident.utils.animation.KeyframeAnimator
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component

object DailyMeter : Meter() {
    private val DAILY_ICON = Texture(
        Resources.trident("textures/interface/meter/daily.png"), 9, 7
    )
    private val DAILY_ICON_COMPLETE = Texture(
        Resources.trident("textures/interface/meter/daily_complete.png"), 9, 7
    )
    private val DAILY_ICON_GOLD by lazy {
        Model(Resources.trident("interface/daily_gold_animated"), 9, 7)
    }

    private val expandAnimation = KeyframeAnimator()

    override fun claimXPSolver(claim: Int): Int {
        require(claim in 1..maxSupportedClaims)

        return when (claim) {
            in 1..2 -> 500
            in 3..4 -> 1000
            in 5..6 -> 2000
            in 7..10 -> 3000
            in 11..14 -> 4000
            else -> 5000
        }
    }

    override fun getDecoration() = HotbarDecoration { graphics, _, x0, y0 ->
        if (!MCCIState.isOnIsland()) return@HotbarDecoration 0
        if (!Config.MeterTracker.Daily.enabled) return@HotbarDecoration 0

        val size = expandAnimation.render(graphics, x0, y0) ?: renderDecoration(graphics, x0, y0)
        DailyMeterXPAnimation.render(graphics, x0, y0)

        size.first
    }

    fun animateExpand() {
        expandAnimation.clear()
        val initialWidth = calculateDecorationWidth()

        val font = minecraft().font
        val text = Component.literal("$totalXP/${claimXPSolver(currentClaim + 1)}").mccFont()
            .withoutShadow()
        val textWidth = font.width(text)

        val decoText = createDecorationText()
        val decoTextWidth = font.width(decoText)

        val targetWidth = 23 + textWidth + 2

        val duration = 600L
        // Expand
        expandAnimation.frame(duration) {
            draw { graphics, x0, y0, d ->
                val t = CubicBezier.SMOOTH.transform(d)
                val width = (initialWidth + (targetWidth - initialWidth) * t + .5f).toInt() // adding .5 here because otherwise last frame will be wrong

                renderAnimationBox(graphics, x0, y0, width)

                graphics.scissorStack.push(ScreenRectangle(x0, y0, width, 9))

                // Initital text fade out
                graphics.text(
                    font,
                    decoText,
                    x0 + width - decoTextWidth - 2,
                    y0,
                    0xFFFFFF.opacity((256 - (256 * d * 3).toInt()).coerceIn(0..256))
                )

                // Progress text fade in
                graphics.text(
                    font,
                    text,
                    x0 + width - textWidth - 2,
                    y0,
                    0xFFFFFF.opacity((256 * CubicBezier.EASE_OUT.transform(d)).toInt())
                )

                graphics.scissorStack.pop()

                renderProgressAndIcon(graphics, x0, y0)


                width to 9
            }
        }

        // Hold
        expandAnimation.frame(3_000L) {
            draw { graphics, x0, y0, _ ->
                renderAnimationBox(graphics, x0, y0, targetWidth)
                renderProgressAndIcon(graphics, x0, y0)

                graphics.text(
                    font, text, x0 + targetWidth - textWidth - 2, y0, 0xFFFFFF.opaqueColor()
                )

                targetWidth to 9
            }
        }

        // Shrink
        expandAnimation.frame(duration) {
            draw { graphics, x0, y0, d ->
                val t = CubicBezier.SMOOTH.transform(d)
                val width = (targetWidth + (initialWidth - targetWidth) * t).toInt()

                renderAnimationBox(graphics, x0, y0, width)

                graphics.scissorStack.push(ScreenRectangle(x0, y0, width, 9))

                // Progress text fade out
                graphics.text(
                    font,
                    text,
                    x0 + width - textWidth - 2,
                    y0,
                    0xFFFFFF.opacity((256 - (256 * d * 3).toInt()).coerceIn(0..256))
                )

                // Initital text fade in
                graphics.text(
                    font,
                    decoText,
                    x0 + width - decoTextWidth - 2,
                    y0,
                    0xFFFFFF.opacity((256 * CubicBezier.EASE_OUT.transform(d)).toInt())
                )

                graphics.scissorStack.pop()
                renderProgressAndIcon(graphics, x0, y0)

                width to 9
            }
        }
    }

    private fun renderAnimationBox(
        graphics: GuiGraphicsExtractor, x0: Int, y0: Int, width: Int
    ): Int {
        graphics.fill(
            x0, y0, x0 + width, y0 + 9, 0x000000.opacity(128)
        )

        return width
    }

    private fun createDecorationText() =
        Component.literal("$currentClaim/$totalClaims").mccFont().withoutShadow()

    private fun calculateDecorationWidth(): Int {
        val font = minecraft().font
        val text = createDecorationText()

        return calculateDecorationWidth(font.width(text))
    }

    private fun calculateDecorationWidth(textWidth: Int): Int {
        val progressBarOffset = if (currentClaim < totalClaims) 7 else -1

        // pad + vertical progress + pad + icon + pad + text + pad
        return 2 + progressBarOffset + 9 + 2 + textWidth + 2
    }

    private fun renderDecoration(
        graphics: GuiGraphicsExtractor, x0: Int, y0: Int
    ): Pair<Int, Int> {
        val font = minecraft().font
        val text = createDecorationText()
        val textWidth = font.width(text)
        val width = calculateDecorationWidth(textWidth)

        graphics.fill(
            x0, y0, x0 + width, y0 + 9, 0x000000.opacity(128)
        )

        renderProgressAndIcon(graphics, x0, y0)

        graphics.text(
            font, text, x0 + width - textWidth - 2, y0, 0xFFFFFF.opaqueColor()
        )

        return width to 9
    }

    private fun renderProgressAndIcon(
        graphics: GuiGraphicsExtractor, x0: Int, y0: Int
    ) {
        val isComplete = currentClaim >= totalClaims

        val progress = when {
            totalClaims <= 0 -> 0f
            isComplete -> 1f
            else -> (totalXP.toFloat() / claimXPSolver(currentClaim + 1).toFloat()).coerceIn(0f, 1f)
        }

        if (!isComplete) {
            renderVerticalProgress(graphics, x0 + 2, y0 + 2, progress)
        }

        val iconX = x0 + getIconXOffset()
        val iconY = y0 + 1

        when {
            hasUnclaimedReward -> DAILY_ICON_GOLD.render(graphics, iconX, iconY)
            isComplete -> DAILY_ICON_COMPLETE.blit(graphics, iconX, iconY)
            else -> DAILY_ICON.blit(graphics, iconX, iconY)
        }
    }

    fun getIconXOffset(): Int {
        val isComplete = currentClaim >= totalClaims
        val progressBarOffset = if (!isComplete) 7 else -1

        return progressBarOffset + 2
    }
}