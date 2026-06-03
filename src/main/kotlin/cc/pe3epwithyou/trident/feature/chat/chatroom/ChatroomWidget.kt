package cc.pe3epwithyou.trident.feature.chat.chatroom

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class ChatroomWidget(chatroom: Chatrooms.Chatroom) : AbstractWidget(0, 0, 0, 0, Component.empty()) {

    override fun extractWidgetRenderState(
        graphics: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        a: Float
    ) {
        TODO("Not yet implemented")
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) = Unit
}