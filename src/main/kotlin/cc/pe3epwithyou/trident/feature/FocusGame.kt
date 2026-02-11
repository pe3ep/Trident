package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState.game
import cc.pe3epwithyou.trident.state.MCCIState.isOnIsland
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.extensions.WindowExtensions
import cc.pe3epwithyou.trident.utils.extensions.WindowExtensions.focusWindowIfInactive
import cc.pe3epwithyou.trident.utils.extensions.WindowExtensions.isMaximized
import cc.pe3epwithyou.trident.utils.minecraft
import org.lwjgl.glfw.GLFW

object FocusGame {
    fun handleSubtitle(subtitle: String) {
        if (!Config.Games.autoFocus) return
        if (isOnIsland() && (game != Game.HUB || game != Game.FISHING)) {
            if (subtitle.contains("►5◄") || subtitle.contains("►3◄")) {
                Logger.debugLog(
                    """
                        isActive: ${WindowExtensions.isActive}
                        isMaximized: ${minecraft().window.isMaximized}
                        
                        IconifiedValue: ${
                        GLFW.glfwGetWindowAttrib(
                            minecraft().window.handle(),
                            GLFW.GLFW_ICONIFIED
                        )
                    }
                        MaximizedValue: ${
                        GLFW.glfwGetWindowAttrib(
                            minecraft().window.handle(),
                            GLFW.GLFW_MAXIMIZED
                        )
                    }
                    """.trimIndent()
                )

                minecraft().window.focusWindowIfInactive()
            }
        }
    }
}