package cc.pe3epwithyou.trident.interfaces.shared

import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.dialog.Dialog
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics

abstract class TridentDialog(x: Int, y: Int, private val key: String) : Dialog(x, y) {
    private enum class Quadrant {
        TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT,
    }

    override fun renderBackground(graphics: GuiGraphics) {
        val color = if (parent == null) theme.colors.dialogBackgroundAlt else theme.colors.dialogBackground
        graphics.fillRoundedAll(
            x, y, getWidth(), getHeight(), color
        )
    }

    open fun refresh() {
        val screenWidth = Minecraft.getInstance().window.guiScaledWidth
        val screenHeight = Minecraft.getInstance().window.guiScaledHeight

        val distanceL = x
        val distanceT = y
        val distanceR = screenWidth - width - x
        val distanceB = screenHeight - height - y
        val quadrant = getQuadrant(distanceL, distanceT, distanceR, distanceB)

        /* Init to refresh the dialog and get the new height/width */
        super.init()

        when (quadrant) {
            Quadrant.TOP_LEFT -> {
                x = distanceL
                y = distanceT
            }

            Quadrant.TOP_RIGHT -> {
                x = screenWidth - width - distanceR
                y = distanceT
            }

            Quadrant.BOTTOM_RIGHT -> {
                x = screenWidth - width - distanceR
                y = screenHeight - height - distanceB
            }

            Quadrant.BOTTOM_LEFT -> {
                x = distanceL
                y = screenHeight - height - distanceB
            }
        }
    }

    override fun onClose() {
        ChatUtils.debugLog("onClose dialog $key has been triggered.")
        ChatUtils.debugLog("$key position: ${this.x} ${this.y}")
        DialogCollection.remove(key)
        DialogCollection.saveDialogPosition(key, Pair(this.x, this.y))
        DialogCollection.saveAllDialogs()
        super.onClose()
    }

    private fun getQuadrant(l: Int, t: Int, r: Int, b: Int): Quadrant {
        if ((t <= b) && (l <= r)) return Quadrant.TOP_LEFT
        if (t <= b) return Quadrant.TOP_RIGHT
        if (l <= r) return Quadrant.BOTTOM_LEFT
        return Quadrant.BOTTOM_RIGHT
    }
}