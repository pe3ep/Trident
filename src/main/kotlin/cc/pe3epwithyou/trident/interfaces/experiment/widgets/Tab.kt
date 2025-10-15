package cc.pe3epwithyou.trident.interfaces.experiment.widgets

import net.minecraft.client.gui.layouts.Layout
import net.minecraft.network.chat.Component

data class Tab(
    val disabled: Boolean = false,
    val title: Component,
    val layout: Layout
)