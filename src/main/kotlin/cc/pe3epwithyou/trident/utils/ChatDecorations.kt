package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.ChatSwitcherButtons
import cc.pe3epwithyou.trident.feature.ChatSwitcherButtons.Widget.Companion.CHAT_HEIGHT
import cc.pe3epwithyou.trident.feature.ChatSwitcherButtons.Widget.Companion.HEIGHT
import cc.pe3epwithyou.trident.feature.ChatSwitcherButtons.checkCompatibility
import cc.pe3epwithyou.trident.feature.dmlock.CurrentLockedChatWidget
import cc.pe3epwithyou.trident.feature.dmlock.ReplyLock
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.GridLayout
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput

object ChatDecorations {
    fun getWidgets(): List<AbstractWidget> {
        val components = mutableListOf<AbstractWidget>()

        val lock = ReplyLock.currentLock
        if (lock != null) {
            val widget = CurrentLockedChatWidget(lock)
            components.add(widget)
        }

        if (Config.Global.chatChannelButtons && checkCompatibility()) {
            components.addAll(ChatSwitcherButtons.getCurrentButtons())
        }

        return components
    }

    class Widget : CompoundWidget(0, 0, 0, 0) {
        override fun getY(): Int = Minecraft.getInstance().window.guiScaledHeight - CHAT_HEIGHT - HEIGHT - 1
        override fun getX(): Int = 2

        override val layout = GridLayout(2) {
            var col = 0
            getWidgets().forEach {
                it.at(0, col)
                col++
            }
        }

        init {
            layout.x = x
            layout.y = y

            layout.arrangeElements()
            layout.visitWidgets(this::addChild)
        }

        override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit

    }
}