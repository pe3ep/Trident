package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

object ChatSwitcherButtons {
    fun checkCompatibility(): Boolean {
        val islandUtils = FabricLoader.getInstance().getModContainer("islandutils")
        return !islandUtils.isPresent
    }

    fun getCurrentButtons(): List<Widget> {
        val x = 2
        var offset = 0
        val channels = mutableListOf<Widget>()
        ChatMode.entries.forEach {
            if (!MCCIState.game.hasTeamChat && it == ChatMode.TEAM) return@forEach
            channels.add(Widget(x + offset, it))
            offset += Widget.WIDTH + 2
        }
        return channels
    }

    class Widget(x: Int, val mode: ChatMode) :
        AbstractWidget(x, 0, WIDTH, HEIGHT, Component.empty()) {
        companion object {
            const val HEIGHT = 9
            const val WIDTH = 44

            const val CHAT_HEIGHT = 16
            private val HOVERED_SPRITE = Resources.trident("textures/interface/chat_channels/hovered.png")
        }

        var isOnCooldown = false

        override fun renderWidget(
            graphics: GuiGraphics, i: Int, j: Int, f: Float
        ) {
            y = graphics.guiHeight() - CHAT_HEIGHT - HEIGHT
            if (isHovered()) Texture(HOVERED_SPRITE, WIDTH, 2).blit(graphics, x, y + HEIGHT - 1)
            Texture(mode.sprite, WIDTH, HEIGHT).blit(graphics, x, y)
        }

        override fun onClick(mouseButtonEvent: MouseButtonEvent, bl: Boolean) {
            if (isOnCooldown) return
            isOnCooldown = true
            val connection = Minecraft.getInstance().connection ?: return
            connection.sendCommand("chat ${mode.name.lowercase()}")
            DelayedAction.delayTicks(20) {
                isOnCooldown = false
            }
        }

        override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit
    }

    enum class ChatMode(
        val sprite: Identifier
    ) {
        LOCAL(Resources.trident("textures/interface/chat_channels/local.png")),
        PARTY(Resources.trident("textures/interface/chat_channels/party.png")),
        TEAM(Resources.trident("textures/interface/chat_channels/team.png")),
        PLOBBY(Resources.trident("textures/interface/chat_channels/plobby.png"));
    }
}