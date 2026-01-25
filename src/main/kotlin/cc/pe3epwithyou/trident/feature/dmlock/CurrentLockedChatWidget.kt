package cc.pe3epwithyou.trident.feature.dmlock

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.dmlock.ReplyLock.currentLock
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.popped
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withTridentFont
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
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
        width =
            Minecraft.getInstance().font.width(
                displayedComponent
            ) + PADDING * 2 + 3
    }

    override fun renderWidget(
        graphics: GuiGraphics,
        i: Int,
        j: Int,
        f: Float
    ) {
        graphics.fillRoundedAll(
            x, y, width - 3, height,
            when {
                isHovered -> 0xa81bb7 opacity 128
                else -> 0x521d59 opacity 128
            }
        )
        graphics.drawString(
            Minecraft.getInstance().font,
            displayedComponent,
            x + PADDING,
            y,
            0xFFFFFF.opaqueColor()
        )
        if (!Config.Global.chatChannelButtons) return
        graphics.fill(x + width - 1, y + height / 2 - 2, x + width, y + height / 2 + 3, 0x000000 opacity 64)
    }

    override fun onClick(mouseButtonEvent: MouseButtonEvent, bl: Boolean) {
        val screen = Minecraft.getInstance().screen
        Minecraft.getInstance().connection?.sendCommand("trident setReplyLock $user false")
        currentLock = null
        Minecraft.getInstance().setScreen(screen) // re-init screen (hacky, but works)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit
}