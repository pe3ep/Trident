package cc.pe3epwithyou.trident.feature.chat.chatroom

import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.util.lighten
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

class ChatroomWidget(val chatroom: Chatrooms.Chatroom) : AbstractWidget(0, 0, 0, 9, Component.empty()) {
    companion object {
        private const val PADDING = 2
    }

    private val textComponent = Component.literal(chatroom.id.uppercase()).mccFont().withoutShadow()

    init {
        val font = minecraft().font
        val textWidth = font.width(textComponent)

        width = textWidth + PADDING * 2 + 9 + 3
    }

    override fun renderWidget(
        graphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        a: Float
    ) {
        val font = minecraft().font
        val isActive = Chatrooms.getActiveChatroom() == chatroom
        val bgColor = when {
            isActive -> chatroom.color.color.lighten(-0.3f) opacity 192
            isHovered -> chatroom.color.color.lighten(-0.3f) opacity 192
            else -> chatroom.color.color.lighten(-0.45f) opacity 192
        }

        graphics.fillRoundedAll(x, y, width, height, bgColor)

        val texture = Texture(chatroom.color.getChatIconTexture(), 9, 7)
        texture.blit(graphics, x + PADDING, y + 1)
        graphics.drawString(font, textComponent, x + 9 + PADDING + 3, y, chatroom.color.color.lighten(1f).opaqueColor())

        if (isActive) {
            graphics.fill(x, y + height + 3, x + width, y + height + 2, chatroom.color.color.lighten(1.5f).opaqueColor())

            graphics.fill(x, y + height - 1, x + 1, y + height, chatroom.color.color.lighten(-0.3f) opacity 64)
            graphics.fill(x + width - 1, y + height - 1, x + width, y + height, chatroom.color.color.lighten(-0.3f) opacity 64)
            graphics.fill(x, y + height, x + width, y + height + 2, chatroom.color.color.lighten(-0.3f) opacity 64)
        }
    }

    override fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {
        if (Chatrooms.getActiveChatroom() == chatroom) {
            Chatrooms.disableLock(true)
            return
        }

        Chatrooms.enableLock(chatroom, true)
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) = Unit
}