package cc.pe3epwithyou.trident.utils

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object Title {
    fun sendTitle(title: Component, subtitle: Component, fadeIn: Int, stay: Int, fadeOut: Int, resetTime: Boolean = true) {
        Minecraft.getInstance().gui.setTimes(fadeIn, stay, fadeOut)
        Minecraft.getInstance().gui.setSubtitle(subtitle)
        Minecraft.getInstance().gui.setTitle(title)
        if (resetTime) Minecraft.getInstance().gui.resetTitleTimes()
    }
}