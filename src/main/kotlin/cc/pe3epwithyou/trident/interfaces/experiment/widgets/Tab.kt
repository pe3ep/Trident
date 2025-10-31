package cc.pe3epwithyou.trident.interfaces.experiment.widgets

import cc.pe3epwithyou.trident.utils.Texture
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.network.chat.Component

data class Tab(
    val disabled: Boolean = false,
    val icon: Texture,
    val title: Component,
    var isDetached: Boolean = false,
    val layout: () -> Layout
)