package cc.pe3epwithyou.trident.events.click

import cc.pe3epwithyou.trident.events.container.ContainerContext
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.client.input.MouseButtonEvent

@DslMarker
annotation class ContainerClickDsl

@ContainerClickDsl
class ContainerClickContext(val isDoubleClick: Boolean, val screen: ContainerScreen, val mouseEvent: MouseButtonEvent) : ContainerContext(screen) {
    fun left() = key() == 0
    fun right() = key() == 1
    fun alt() = mouseEvent.hasAltDown()
    fun ctrl() = mouseEvent.hasControlDown()
    fun shift() = mouseEvent.hasShiftDown()
    fun key() = mouseEvent.button()
    fun doubleClick() = isDoubleClick
}