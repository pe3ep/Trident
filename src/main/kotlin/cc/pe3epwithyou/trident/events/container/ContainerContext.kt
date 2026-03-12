package cc.pe3epwithyou.trident.events.container

import cc.pe3epwithyou.trident.events.StopExecution
import cc.pe3epwithyou.trident.mixin.accessors.AbstractContainerScreenAccessor
import cc.pe3epwithyou.trident.mixin.accessors.ScreenAccessor
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.context
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.inventory.Slot

@DslMarker
annotation class ContainerDsl

@Suppress("unused")
@ContainerDsl
open class ContainerContext(val handledScreen: ContainerScreen) {
    fun requireTitle(title: String) { if (!titleContains(title)) throw StopExecution() }

    fun titleContains(title: String): Boolean = title.lowercase() in handledScreen.title.string.lowercase()

    fun slot(index: Int) = handledScreen.menu.slots.getOrNull(index)

    fun hoveredSlot(): Slot? = (handledScreen as AbstractContainerScreenAccessor).hoveredSlot
    fun hoveredItem() = hoveredSlot()?.item

    fun item(index: Int) = slot(index)?.item

    fun <T> addRenderable(widget: T) where T : GuiEventListener, T : Renderable, T : NarratableEntry {
        val accessed = handledScreen as ScreenAccessor
        accessed.`trident$addRenderableWidget`(widget)
    }

    fun imageHeight() = (handledScreen as AbstractContainerScreenAccessor).imageHeight
    fun topPos() = (handledScreen as AbstractContainerScreenAccessor).topPos
    fun leftPos() = (handledScreen as AbstractContainerScreenAccessor).leftPos
}

fun withContainerCtx(screen: ContainerScreen, block: ContainerContext.() -> Unit) {
    try {
        if (!MCCIState.isOnIsland()) return
        screen.context().block()
    } catch (_: StopExecution) {} // Ignored
}
