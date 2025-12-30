package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withFont
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object ProgressBar {
    private val COMP_BLANK
        get() = FontCollection.get("_fonts/icon/progress_counter/empty.png", 7, 7)
    private val COMP_HALF
        get() = FontCollection.get("_fonts/icon/progress_counter/half.png", 7, 7)
    private val COMP_FULL
        get() = FontCollection.get("_fonts/icon/progress_counter/full.png", 7, 7)

    private val COMP_SPACE: Component =
        Component.literal("\uE001").withFont(Resources.minecraft("padding"))


    /**
     * Build a progress bar Component using preallocated Component pieces.
     *
     * @param progress - 0.0..1.0 progress fraction
     * @param width - number of characters in the bar (excludes grouping spaces)
     * @param groups - number of groups to split the width into; when > 0 a
     *   space is inserted after each group (e.g. width=20, divisions=4 ->
     *   space every 5 chars). If <= 0 no spaces.
     */
    fun progressComponent(
        progress: Float, width: Int, groups: Int = 0
    ): MutableComponent {
        if (width <= 0) return Component.empty()

        val subPerChar = 2 // so we have empty/half/full
        val clamped = progress.coerceIn(0f, 1f)
        val totalSubUnits = width * subPerChar
        val filledSubUnits = (clamped * totalSubUnits).roundToInt()

        val groupSize = if (groups > 0) max(1, width / groups) else Int.MAX_VALUE

        var component = Component.empty()
        for (i in 0 until width) {
            val startUnit = i * subPerChar
            val remain = max(0, min(subPerChar, filledSubUnits - startUnit))

            val piece = when {
                remain >= subPerChar -> COMP_FULL
                remain * 2 >= subPerChar -> COMP_HALF
                else -> COMP_BLANK
            }

            component = component.append(piece)

            if (groups > 0 && (i + 1) % groupSize == 0 && i != width - 1) {
                component = component.append(COMP_SPACE)
            }
        }

        return component
    }
}