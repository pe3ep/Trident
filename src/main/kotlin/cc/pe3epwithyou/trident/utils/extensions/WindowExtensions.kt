package cc.pe3epwithyou.trident.utils.extensions

import cc.pe3epwithyou.trident.utils.minecraft
import com.mojang.blaze3d.platform.Window
import org.lwjgl.sdl.SDLVideo

object WindowExtensions {
    val isActive: Boolean
        get() = minecraft().isWindowActive

    val Window.isMaximized: Boolean
        get() = SDLVideo.SDL_GetWindowFlags(handle()) and SDLVideo.SDL_WINDOW_MAXIMIZED != 0L

    val Window.isFullscreen: Boolean
        get() = SDLVideo.SDL_GetWindowFlags(handle()) and SDLVideo.SDL_WINDOW_FULLSCREEN != 0L

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
        SDLVideo.SDL_FlashWindow(handle(), SDLVideo.SDL_FLASH_UNTIL_FOCUSED)
    }

    fun Window.requestAttentionIfInactive() {
        if (!isActive) requestAttention()
    }

    fun Window.focusWindow() {
        if (isIconified) SDLVideo.SDL_RestoreWindow(handle())
        SDLVideo.SDL_RaiseWindow(handle())
    }
}
