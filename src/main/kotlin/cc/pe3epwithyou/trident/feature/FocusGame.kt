package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState.game
import cc.pe3epwithyou.trident.state.MCCIState.isOnIsland
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.extensions.WindowExtensions
import cc.pe3epwithyou.trident.utils.extensions.WindowExtensions.focusWindowIfInactive
import cc.pe3epwithyou.trident.utils.extensions.WindowExtensions.isMaximized
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW

object FocusGame {
    fun handleSubtitle(subtitle: String) {
        if (!Config.Games.autoFocus) return
        if (isOnIsland() && (game != Game.HUB || game != Game.FISHING)) {
            if (subtitle.contains("►5◄") || subtitle.contains("►3◄")) {
                ChatUtils.debugLog(
                    """
                        isActive: ${WindowExtensions.isActive}
                        isMaximized: ${Minecraft.getInstance().window.isMaximized}
                        
                        IconifiedValue: ${
                        GLFW.glfwGetWindowAttrib(
                            Minecraft.getInstance().window.window,
                            GLFW.GLFW_ICONIFIED
                        )
                    }
                        MaximizedValue: ${
                        GLFW.glfwGetWindowAttrib(
                            Minecraft.getInstance().window.window,
                            GLFW.GLFW_MAXIMIZED
                        )
                    }
                    """.trimIndent()
                )

                Minecraft.getInstance().window.focusWindowIfInactive()
            }
        }
    }
}