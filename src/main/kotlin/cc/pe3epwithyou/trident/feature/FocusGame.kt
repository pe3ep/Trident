package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.MCCIslandState.game
import cc.pe3epwithyou.trident.state.MCCIslandState.isOnIsland
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.WindowExtensions
import cc.pe3epwithyou.trident.utils.WindowExtensions.focusWindowIfInactive
import cc.pe3epwithyou.trident.utils.WindowExtensions.isMaximized
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW

object FocusGame {
    fun handleSubtitle(subtitle: String) {
        if (!Config.Games.autoFocus) return
        if (isOnIsland() && (game != MCCGame.HUB || game != MCCGame.FISHING)) {
            if (subtitle.contains("►5◄") || subtitle.contains("►3◄")) {
                ChatUtils.debugLog(
                    """
                        isActive: ${WindowExtensions.isActive}
                        isMaximized: ${Minecraft.getInstance().window.isMaximized}
                        
                        IconifiedValue: ${GLFW.glfwGetWindowAttrib(Minecraft.getInstance().window.window, GLFW.GLFW_ICONIFIED)}
                        MaximizedValue: ${GLFW.glfwGetWindowAttrib(Minecraft.getInstance().window.window, GLFW.GLFW_MAXIMIZED)}
                    """.trimIndent()
                )

                Minecraft.getInstance().window.focusWindowIfInactive()
            }
        }
    }
}