package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.util.Util
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

// TODO: Finish this
class ScreenManager(val screen: ContainerScreen) {
    companion object {
        var isWaitingForItems = false
    }

    fun checkName(checkFor: String, block: ScreenManager.(ContainerScreen) -> Unit) {
        if (!screen.title.string.lowercase().contains(checkFor.lowercase())) return
        this.apply { block(screen) }
    }

    fun slot(index: Int): Slot = screen.menu.slots[index]
    fun getItem(index: Int): ItemStack = slot(index).item
}

fun useScreen(
    screen: ContainerScreen, block: ScreenManager.(ContainerScreen) -> Unit
) {
    ScreenManager(screen).apply { block(screen) }
}

fun useScreenWait(
    screen: ContainerScreen, block: ScreenManager.(ContainerScreen) -> Unit
) {
    waitForItems { useScreen(screen, block) }
}

private fun waitForItems(block: () -> Unit) {
    val ctx = Util.backgroundExecutor().asCoroutineDispatcher()
    ScreenManager.isWaitingForItems = true
    CoroutineScope(ctx).launch {
        var time = 0
        // timeout of 3 seconds
        while (time < 60) {
            if (!ScreenManager.isWaitingForItems) {
                delay(50) // Extra 1 tick wait due to some items not arriving together
                block()
                return@launch
            }
            delay(50)
            time++
        }
        ScreenManager.isWaitingForItems = false
        Logger.sendMessage(
            Component.literal("Timed out waiting for items to arrive, waited for 3 seconds")
                .withSwatch(TridentFont.ERROR)
        )
        Logger.error("Timed out waiting for items")
    }
}