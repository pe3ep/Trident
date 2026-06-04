package cc.pe3epwithyou.trident.feature.chat.chatroom

import cc.pe3epwithyou.trident.feature.chat.ChatControllerManager
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.util.lighten
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

class ChatroomWidget(val chatroom: Chatrooms.Chatroom) : AbstractWidget(0, 0, 0, 9, Component.empty()) {
    companion object {
        private const val PADDING = 2
    }

    private val textComponent = Component.literal(chatroom.id.uppercase()).mccFont().withoutShadow()
    private val texture = Texture(Resources.trident("textures/interface/chat_channels/channel_icon.png"), 9, 7)

    init {
        val font = minecraft().font
        val textWidth = font.width(textComponent)

        width = textWidth + PADDING * 2 + 9 + 3
    }

    override fun extractWidgetRenderState(
        graphics: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        a: Float
    ) {
        val font = minecraft().font
        val isActive = Chatrooms.getActiveChatroom() == chatroom
        val bgColor = when {
            isActive -> chatroom.color.color.lighten(-0.2f) opacity 192
            isHovered -> chatroom.color.color.lighten(-0.3f) opacity 192
            else -> chatroom.color.color.lighten(-0.45f) opacity 192
        }

        graphics.fillRoundedAll(x, y, width, height, bgColor)

        texture.blit(graphics, x + PADDING, y + 1)
        graphics.text(font, textComponent, x + 9 + PADDING + 3, y, chatroom.color.color.lighten(1f).opaqueColor())

        if (isActive) {
            graphics.fill(x, y + height + 2, x + width, y + height + 1, chatroom.color.color.lighten(1f).opaqueColor())
        }
    }

    override fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {
        if (Chatrooms.getActiveChatroom() == chatroom) {
            ChatControllerManager.clearController()
            return
        }

        ChatControllerManager.setController(ChatroomController(chatroom))
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) = Unit
}