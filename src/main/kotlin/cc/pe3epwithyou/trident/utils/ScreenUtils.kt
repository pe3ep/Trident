package cc.pe3epwithyou.trident.utils

import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class ScreenManagerOld(val screen: ContainerScreen) {
    fun checkName(checkFor: String, block: ScreenManagerOld.(ContainerScreen) -> Unit) {
        if (!screen.title.string.lowercase().contains(checkFor.lowercase())) return
        this.apply { block(screen) }
    }

    fun await(block: ScreenManagerOld.(ContainerScreen) -> Unit) {
        waitForItems(screen) { this.apply { block(screen) } }
    }

    fun slot(index: Int): Slot = screen.menu.slots[index]
    fun getItem(index: Int): ItemStack = slot(index).item
}

fun useScreen(
    screen: ContainerScreen, block: ScreenManagerOld.(ContainerScreen) -> Unit
) {
    ScreenManagerOld(screen).apply { block(screen) }
}