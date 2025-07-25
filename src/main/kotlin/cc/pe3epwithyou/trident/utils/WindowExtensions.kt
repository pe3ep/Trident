package cc.pe3epwithyou.trident.utils

import com.mojang.blaze3d.platform.Window
import org.lwjgl.glfw.GLFW

object WindowExtensions {
    val Window.isActive: Boolean
        get() = GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_FOCUSED) != 0

    fun Window.focusWindowIfInactive() {
        if (!isActive) focusWindow()
    }

    fun Window.requestAttention() {
        GLFW.glfwRequestWindowAttention(window)
    }

    fun Window.requestAttentionIfInactive() {
        if (!isActive) requestAttention()
    }

    fun Window.focusWindow() {
        GLFW.glfwFocusWindow(window)
        GLFW.glfwRestoreWindow(window)
    }
}