package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.events.container.ContainerContext
import cc.pe3epwithyou.trident.state.PlayerState
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import com.noxcrew.sheeplib.layout.GridLayoutBuilder
import kotlinx.coroutines.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.util.Util

inline fun gridLayout(spacing: Int, x: Int = 0, y: Int = 0, builder: GridLayoutBuilder.() -> Unit): GridLayout {
    return GridLayoutBuilder(x, y, spacing).also(builder).build()
}

fun String.parseFormattedInt(): Int? {
    return this.replace(",", "").toIntOrNull()
}

fun minecraft(): Minecraft = Minecraft.getInstance()
fun playerState(): PlayerState = Trident.playerState

fun nonCriticalIO() = CoroutineScope(Util.nonCriticalIoPool().asCoroutineDispatcher())
fun background() = CoroutineScope(Util.backgroundExecutor().asCoroutineDispatcher())

fun main(
    block: () -> Unit
) {
    minecraft().execute(block)
}

fun ContainerScreen.context() = ContainerContext(this)

fun screenWidth(): Int = minecraft().window.guiScaledWidth
fun screenHeight(): Int = minecraft().window.guiScaledHeight

object ScreenManager {
    var isWaitingForItems = false
    var waitingOnScreen: ContainerScreen? = null

    @JvmStatic
    fun setWaiting(waiting: Boolean) {
        isWaitingForItems = waiting
    }
}

fun waitForItems(screen: ContainerScreen, block: () -> Unit) {
    if (ScreenManager.isWaitingForItems) return

    ScreenManager.isWaitingForItems = true
    ScreenManager.waitingOnScreen = screen
    background().launch {
        val completed = withTimeoutOrNull(3_000) {
            while (ScreenManager.isWaitingForItems) {
                delay(50)
            }
            true
        } ?: false

        if (completed) {
            main {
                if (!(ScreenManager.waitingOnScreen?.equals(screen) ?: false)) return@main
                ScreenManager.waitingOnScreen = null
                block()
            }
            return@launch
        }

        ScreenManager.isWaitingForItems = false
        ScreenManager.waitingOnScreen = null
        main {
            Logger.sendMessage(
                Component.literal("An error occurred while handling your current screen. Please try again.")
                    .withSwatch(TridentFont.ERROR)
            )
            Logger.error("Timed out waiting for items")
        }
    }
}