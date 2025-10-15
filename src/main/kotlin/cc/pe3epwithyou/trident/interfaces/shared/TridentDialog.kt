package cc.pe3epwithyou.trident.interfaces.shared

import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.dialog.Dialog
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics

abstract class TridentDialog(x: Int, y: Int, private val key: String) : Dialog(x, y) {
    companion object {
        fun getDistances(x: Int, y: Int, width: Int, height: Int): SideDistances {
            val screenWidth = Minecraft.getInstance().window.guiScaledWidth
            val screenHeight = Minecraft.getInstance().window.guiScaledHeight

            val distanceR = screenWidth - width - x
            val distanceB = screenHeight - height - y
            return SideDistances(y, x, distanceR, distanceB)
        }

        fun getQuadrant(distances: SideDistances): Quadrant {
            if ((distances.top <= distances.bottom) && (distances.left <= distances.right)) return Quadrant.TOP_LEFT
            if (distances.top <= distances.bottom) return Quadrant.TOP_RIGHT
            if (distances.left <= distances.right) return Quadrant.BOTTOM_LEFT
            return Quadrant.BOTTOM_RIGHT
        }
    }

    enum class Quadrant {
        TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT,
    }

    @Serializable
    data class Position(val x: Int, val y: Int, val distances: SideDistances)

    @Serializable
    data class SideDistances(val top: Int, val left: Int, val right: Int, val bottom: Int)

    override fun renderBackground(graphics: GuiGraphics) {
        val color = if (parent == null) theme.colors.dialogBackgroundAlt else theme.colors.dialogBackground
        graphics.fillRoundedAll(
            x, y, getWidth(), getHeight(), color
        )
    }

    open fun refresh() {
        val distances: SideDistances = getDistances(x, y, width, height)
        val quadrant = getQuadrant(distances)

        /* Init to refresh the dialog and get the new height/width */
        super.init()
        applyQuadrantPositioning(quadrant, distances)
    }

    override fun onClose() {
        ChatUtils.debugLog("onClose dialog $key has been triggered.")
        ChatUtils.debugLog("$key position: ${this.x} ${this.y}")
        DialogCollection.remove(key)
        DialogCollection.saveDialogPosition(key, this)
        DialogCollection.saveAllDialogs()
        super.onClose()
    }

    val position: Position
        get() = Position(x, y, getDistances(x, y, width, height))


    fun applyQuadrantPositioning(quad: Quadrant, distances: SideDistances) {
        val screenWidth = Minecraft.getInstance().window.guiScaledWidth
        val screenHeight = Minecraft.getInstance().window.guiScaledHeight
        when (quad) {
            Quadrant.TOP_LEFT -> {
                x = distances.left
                y = distances.top
            }

            Quadrant.TOP_RIGHT -> {
                x = screenWidth - width - distances.right
                y = distances.top
            }

            Quadrant.BOTTOM_RIGHT -> {
                x = screenWidth - width - distances.right
                y = screenHeight - height - distances.bottom
            }

            Quadrant.BOTTOM_LEFT -> {
                x = distances.left
                y = screenHeight - height - distances.bottom
            }
        }
    }
}