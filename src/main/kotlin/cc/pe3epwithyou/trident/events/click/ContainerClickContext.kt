package cc.pe3epwithyou.trident.events.click

import cc.pe3epwithyou.trident.events.container.ContainerContext
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.client.input.MouseButtonEvent

@DslMarker
annotation class ContainerClickDsl

@Suppress("unused")
@ContainerClickDsl
class ContainerClickContext(val doubleClick: Boolean, val screen: ContainerScreen, val mouseEvent: MouseButtonEvent) : ContainerContext(screen) {
    val left: Boolean = key == 0
    val right: Boolean = key == 1
    val alt: Boolean = mouseEvent.hasAltDown()
    val ctrl: Boolean = mouseEvent.hasControlDown()
    val shift: Boolean = mouseEvent.hasShiftDown()
    val key: Int
        get() = mouseEvent.button()

    fun clickedSlot() = hoveredSlot()
    fun clickedItem() = clickedSlot()?.item
}