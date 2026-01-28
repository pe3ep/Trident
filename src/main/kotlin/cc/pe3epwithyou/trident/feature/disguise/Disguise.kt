package cc.pe3epwithyou.trident.feature.disguise

import cc.pe3epwithyou.trident.mixin.GuiAccessor
import net.minecraft.client.Minecraft

object Disguise {
    var disguiseIconCache: String? = null

    fun isDisguised(): Boolean {
        val gui = Minecraft.getInstance().gui as GuiAccessor
        val actionbar = gui.overlayMessageString ?: return false
        disguiseIconCache?.let { return actionbar.string.contains(it) }
        return false
    }
}