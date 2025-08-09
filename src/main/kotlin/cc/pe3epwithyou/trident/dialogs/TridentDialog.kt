package cc.pe3epwithyou.trident.dialogs

import cc.pe3epwithyou.trident.utils.ChatUtils
import com.noxcrew.sheeplib.dialog.Dialog
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import kotlin.jvm.optionals.getOrNull

abstract class TridentDialog(x: Int, y: Int, private val key: String, private val disableDragAxis: String? = null) : Dialog(x, y) {
    private var dragStartX = -1
    private var dragStartY = -1

    private companion object {
        private val ROUNDED_BOTTOM = ResourceLocation.fromNamespaceAndPath("trident", "interface/background/rounded_bottom")
        private val ROUNDED_ALL = ResourceLocation.fromNamespaceAndPath("trident", "interface/background/rounded_all")
        private val BORDER_BOTTOM = ResourceLocation.fromNamespaceAndPath("trident", "interface/background/border/border_bottom")
        private val BORDER_ALL = ResourceLocation.fromNamespaceAndPath("trident", "interface/background/border/border_all")
    }

    //    Custom dragging to disable the Y axis
    override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
        if ((popup?.mouseClicked(d, e, i) == true) || super.mouseClicked(d, e, i)) return true
        if (!isMouseOver(d, e)) return false
        isDragging = true
        dragStartX = x - d.toInt()
        dragStartY = y - e.toInt()
        return true
    }

    override fun mouseReleased(d: Double, e: Double, i: Int): Boolean {
        popup?.mouseReleased(d, e, i)
        dragStartX = -1

//        Minecraft code, as we can't call it via super without making our own dialog system
        if (i == 0 && this.isDragging) {
            this.isDragging = false
            if (this.focused != null) {
                return focused!!.mouseReleased(d, e, i)
            }
        }
        return false
    }

    private fun mouseDragCheck(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean {
        return getChildAt(d, e)
            .getOrNull()
            ?.mouseDragged(d, e, i, f, g) == true
    }

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean {
        if ((popup?.mouseDragged(d, e, i, f, g) == true) || mouseDragCheck(d, e, i, f, g)) return true
        if (!isDragging || dragStartX == -1) return false
        if (disableDragAxis != "x") {
            x = dragStartX + d.toInt()
        }
        if (disableDragAxis != "y") {
            y = dragStartY + e.toInt()
        }
        return true
    }

    override fun renderBackground(graphics: GuiGraphics) {
        graphics.blitSprite(
            RenderType::guiTextured,
            ROUNDED_ALL,
            x,
            y,
            getWidth(),
            getHeight(),
            if (parent == null) theme.colors.dialogBackgroundAlt else theme.colors.dialogBackground
        )

        if (theme.dialogBorders) {
            graphics.blitSprite(
                RenderType::guiTextured,
                BORDER_ALL,
                x,
                y,
                getWidth(),
                getHeight(),
                theme.colors.border
            )
        }
    }

    open fun refresh() {
        super.init()
    }

    override fun onClose() {
        ChatUtils.info("onClose dialog $key has been triggered.")
        ChatUtils.info("$key position: ${this.x} ${this.y}")
        DialogCollection.remove(key)
        DialogCollection.saveDialogPosition(key, Pair(this.x, this.y))
        super.onClose()
    }
}