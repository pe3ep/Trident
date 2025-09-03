package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withFont
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object ProgressBar {
    data object VALUES {
        private const val PROGRESS_BLANK = ''
        private const val PROGRESS_HALF = ''
        private const val PROGRESS_FULL = ''

        val COMP_BLANK: Component = Component.literal(PROGRESS_BLANK.toString()).mccFont("icon")
        val COMP_HALF: Component = Component.literal(PROGRESS_HALF.toString()).mccFont("icon")
        val COMP_FULL: Component = Component.literal(PROGRESS_FULL.toString()).mccFont("icon")
        val COMP_SPACE: Component =
            Component.literal("\uE001").withFont(ResourceLocation.withDefaultNamespace("padding"))

        private val COMPLETED_QUEST_SPRITE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath("mcc", "textures/island_interface/quest_log/quest_complete.png")
        private const val COMPLETED_QUEST_COLOR: Int = 0x1EFC00
    }

    /**
     * Build a progress bar Component using preallocated Component pieces.
     *
     * @param progress - 0.0..1.0 progress fraction
     * @param width - number of characters in the bar (excludes grouping spaces)
     * @param groups - number of groups to split the width into; when > 0 a
     *   space is inserted after each group (e.g. width=20, divisions=4 ->
     *   space every 5 chars). If <= 0 no spaces.
     */
    fun createProgressBarComponent(
        progress: Float,
        width: Int,
        groups: Int = 0
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
                remain >= subPerChar -> VALUES.COMP_FULL
                remain * 2 >= subPerChar -> VALUES.COMP_HALF
                else -> VALUES.COMP_BLANK
            }

            component = component.append(piece)

            if (groups > 0 && (i + 1) % groupSize == 0 && i != width - 1) {
                component = component.append(VALUES.COMP_SPACE)
            }
        }
        return component
    }
}