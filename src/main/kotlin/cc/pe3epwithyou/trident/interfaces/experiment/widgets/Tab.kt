package cc.pe3epwithyou.trident.interfaces.experiment.widgets

import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.network.chat.Component

data class Tab(
    val id: String,
    val disabled: Boolean = false,
    val icon: Texture,
    val title: Component,
    var isDetached: Boolean = false,
    val layout: () -> Layout
) {
    companion object {
        val DETACH_ICON: Texture = Texture(
            Resources.trident("textures/interface/dialog_actions/detach.png"), 8, 8
        )

        val ATTACH_ICON: Texture = Texture(
            Resources.trident("textures/interface/dialog_actions/attach.png"), 8, 8
        )
    }
}