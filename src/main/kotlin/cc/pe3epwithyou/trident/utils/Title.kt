package cc.pe3epwithyou.trident.utils

import net.minecraft.network.chat.Component

object Title {
    fun sendTitle(
        title: Component,
        subtitle: Component,
        fadeIn: Int,
        stay: Int,
        fadeOut: Int,
        resetTime: Boolean = true
    ) {
        minecraft().gui.setTimes(fadeIn, stay, fadeOut)
        minecraft().gui.setSubtitle(subtitle)
        minecraft().gui.setTitle(title)
        if (resetTime) minecraft().gui.resetTitleTimes()
    }
}