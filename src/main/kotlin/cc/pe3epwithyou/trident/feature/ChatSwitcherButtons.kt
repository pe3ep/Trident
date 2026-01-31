package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.feature.discord.ActivityManager
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

    fun getChatModes(): List<ChatMode> = buildList {
        add(ChatMode.LOCAL)
        ActivityManager.Party.size?.let { if (it > 1) add(ChatMode.PARTY) }
        if (MCCIState.game.hasTeamChat) add(ChatMode.TEAM)
        if (MCCIState.isInPlobby()) add(ChatMode.PLOBBY)
    }

    fun getCurrentButtons(): List<Widget> = getChatModes().map(::Widget)

    class Widget(val mode: ChatMode) :
        AbstractWidget(0, 0, WIDTH, HEIGHT, Component.empty()) {
        companion object {
            const val HEIGHT = 9
            const val WIDTH = 44

            const val CHAT_HEIGHT = 16
            private val HOVERED_SPRITE =
                Resources.trident("textures/interface/chat_channels/hovered.png")
        }

        private var texture = Texture(mode.sprite, WIDTH, HEIGHT)

        var isOnCooldown = false

        override fun renderWidget(
            graphics: GuiGraphics, i: Int, j: Int, f: Float
        ) {
            if (isHovered()) Texture(HOVERED_SPRITE, WIDTH, 2).blit(graphics, x, y + HEIGHT - 1)
            texture.blit(graphics, x, y)
        }

        override fun onClick(mouseButtonEvent: MouseButtonEvent, bl: Boolean) {
            if (isOnCooldown) return
            isOnCooldown = true
            val connection = Minecraft.getInstance().connection ?: return
            connection.sendCommand("chat ${mode.commandName}")
            DelayedAction.delayTicks(20) {
                isOnCooldown = false
            }
        }

        override fun updateWidgetNarration(narratigonElementOutput: NarrationElementOutput) = Unit
    }

    data class ChatMode(
        val commandName: String,
        val sprite: Identifier,
    ) {
        companion object {
            val LOCAL =
                ChatMode("local", Resources.trident("textures/interface/chat_channels/local.png"))
            val PARTY =
                ChatMode("party", Resources.trident("textures/interface/chat_channels/party.png"))
            val TEAM =
                ChatMode("team", Resources.trident("textures/interface/chat_channels/team.png"))
            val PLOBBY =
                ChatMode("plobby", Resources.trident("textures/interface/chat_channels/plobby.png"))
        }
    }
}