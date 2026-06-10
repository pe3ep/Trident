package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withTridentFont
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import kotlin.math.max

object ProgressBar {
    private val COMP_HALF_PATH = Component.literal("\uE017").withTridentFont()

    private val COMP_SPACE: Component =
        Component.literal("\uE001").withFont(Resources.minecraft("padding"))

    private val COMP_NEGATIVE_SPACE: Component =
        Component.literal("\uE019").withFont(Resources.minecraft("padding"))


    typealias ProgressColorProvider = (
        progress: Float,
        leftHalfPercent: Float,
        rightHalfPercent: Float
    ) -> Pair<Int, Int>

    fun standardProgressColors(
        progress: Float,
        leftHalfPercent: Float,
        rightHalfPercent: Float
    ): Pair<Int, Int> {
        fun colorAt(percent: Float): Int {
            return if (percent <= progress) 0x6cfe6e else 0x686969
        }

        return colorAt(leftHalfPercent) to colorAt(rightHalfPercent)
    }

    private fun halfGlyph(color: Int): MutableComponent {
        return COMP_HALF_PATH.copy().withColor(color)
    }

    /**
     * Build a progress bar Component using preallocated Component pieces.
     *
     * @param progress - 0.0..1.0 progress fraction
     * @param width - Number of characters in the bar (excludes grouping spaces)
     * @param groups - Specifies the quantity of segments to divide the width into;
     * if this value is greater than zero, a space will be inserted after each segment
     * (e.g., width=20, divisions=4 results in a space every 5 characters).
     * A value of zero or less will result in no spaces being added.
     */
    fun progressComponent(
        progress: Float,
        width: Int,
        groups: Int = 0,
        colorsForPiece: ProgressColorProvider = ::standardProgressColors
    ): MutableComponent {
        if (width <= 0) return Component.empty()

        val clamped = progress.coerceIn(0f, 1f)
        val totalHalves = width * 2
        val groupSize = if (groups > 0) max(1, width / groups) else Int.MAX_VALUE

        var component = Component.empty()

        for (i in 0 until width) {
            val leftHalfPercent = (i * 2 + 0.5f) / totalHalves
            val rightHalfPercent = (i * 2 + 1.5f) / totalHalves

            val (leftColor, rightColor) = colorsForPiece(
                clamped,
                leftHalfPercent,
                rightHalfPercent
            )

            component = component
                .append(halfGlyph(leftColor).append(COMP_NEGATIVE_SPACE))
                .append(halfGlyph(rightColor))

            if (groups > 0 && (i + 1) % groupSize == 0 && i != width - 1) {
                component = component.append(COMP_SPACE)
            }
        }

        return component
    }
}