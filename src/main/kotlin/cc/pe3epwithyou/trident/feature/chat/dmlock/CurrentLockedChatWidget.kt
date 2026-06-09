package cc.pe3epwithyou.trident.feature.chat.dmlock

import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.popped
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withTridentFont
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

class CurrentLockedChatWidget(val user: String) : AbstractWidget(0, 0, 0, 9, Component.empty()) {
    private companion object {
        const val PADDING = 2
    }

    var displayedComponent: Component

    init {
        val lock = Component.literal("\uE016").withTridentFont().popped()
        displayedComponent = lock
            .append(
                Component.literal("\uE002").withFont(Resources.minecraft("padding"))
            )
            .append(
                Component.literal("REPLY LOCK: ").mccFont().withColor(0xFFFFFF)
                    .append(Component.literal(user.uppercase()).withColor(0xfc7dfc))
            ).withoutShadow()

        width = minecraft().font.width(
            displayedComponent
        ) + PADDING * 2
    }

    override fun extractWidgetRenderState(
        graphics: GuiGraphicsExtractor,
        i: Int,
        j: Int,
        f: Float
    ) {
        graphics.fillRoundedAll(
            x, y, width, height,
            when {
                isHovered -> 0xa81bb7 opacity 128
                else -> 0x521d59 opacity 128
            }
        )
        graphics.text(
            minecraft().font,
            displayedComponent,
            x + PADDING,
            y,
            0xFFFFFF.opaqueColor()
        )
    }

    override fun onClick(mouseButtonEvent: MouseButtonEvent, bl: Boolean) {
        ReplyLock.disableLock()
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit
}