package cc.pe3epwithyou.trident.interfaces.fishing.widgets

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.state.MutableAugment
import cc.pe3epwithyou.trident.utils.Model
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.CanvasLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.network.chat.Component
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont

class AugmentStackWidget(width: Int, height: Int, entries: List<MutableAugment>) : CompoundWidget(0, 0, width, height) {

    private val leftInset = 12
    private val gap = 28
    private val rowHeight = height + 9
    private val verticalGap = 2

    override val layout: CanvasLayout = run {
        val topCount = kotlin.math.min(5, entries.size)
        val bottomCount = kotlin.math.max(0, entries.size - 5)
        val columns = kotlin.math.max(topCount, bottomCount)
        val totalWidth = leftInset + columns * (width + gap)
        val totalHeight = if (bottomCount > 0) rowHeight * 2 + verticalGap else rowHeight
        CanvasLayout(totalWidth, totalHeight).apply {

            // Top row (first 1..5)
            var x = leftInset
            for (i in 0 until topCount) {
                val augment = entries[i]
                val model = Model(augment.augment.texturePath, width, height)
                val label: Component? = if (augment.usesCurrent != null && augment.usesMax != null) {
                    Component.literal("${augment.usesCurrent}/${augment.usesMax}").mccFont()
                } else null
                IconWithLabelWidget(model, label, marginRight = 0).at(top = 0, left = x)
                x += width + gap
            }

            // Bottom row (6..10)
            if (bottomCount > 0) {
                x = leftInset
                val y = rowHeight + verticalGap
                for (j in 0 until bottomCount) {
                    val idx = topCount + j
                    val augment = entries[idx]
                    val model = Model(augment.augment.texturePath, width, height)
                    val label: Component? = if (augment.usesCurrent != null && augment.usesMax != null) {
                        Component.literal("${augment.usesCurrent}/${augment.usesMax}").mccFont()
                    } else null
                    IconWithLabelWidget(model, label, marginRight = 0).at(top = y, left = x)
                    x += width + gap
                }
            }
        }
    }

    override fun mouseClicked(d: Double, e: Double, i: Int): Boolean = false
    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}