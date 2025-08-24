package cc.pe3epwithyou.trident.utils.extensions

import com.mojang.blaze3d.platform.Window
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW

object WindowExtensions {
    val isActive: Boolean
        get() = Minecraft.getInstance().isWindowActive

    val Window.isMaximized: Boolean
        get() = GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_MAXIMIZED) != 0

    fun Window.focusWindowIfInactive() {
        if (!isActive) {
            if (isFullscreen) {
                focusWindow()
                return
            }
//            Focusing a windowed application is unreliable, highlighting the icon instead
            requestAttention()
        }
    }

    fun Window.requestAttention() {
        GLFW.glfwRequestWindowAttention(window)
    }

    fun Window.requestAttentionIfInactive() {
        if (!isActive) requestAttention()
    }

    fun Window.focusWindow() {
        if (isIconified) GLFW.glfwRestoreWindow(window)
        GLFW.glfwFocusWindow(window)
    }
}