package cc.pe3epwithyou.trident.interfaces.shared

import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.dialog.Dialog
import net.minecraft.client.gui.GuiGraphics

abstract class TridentDialog(x: Int, y: Int, private val key: String) : Dialog(x, y) {
    override fun renderBackground(graphics: GuiGraphics) {
        val color = if (parent == null) theme.colors.dialogBackgroundAlt else theme.colors.dialogBackground
        graphics.fillRoundedAll(
            x,
            y,
            getWidth(),
            getHeight(),
            color
        )
    }

    open fun refresh() {
        super.init()
    }

    override fun onClose() {
        ChatUtils.debugLog("onClose dialog $key has been triggered.")
        ChatUtils.debugLog("$key position: ${this.x} ${this.y}")
        DialogCollection.remove(key)
        DialogCollection.saveDialogPosition(key, Pair(this.x, this.y))
        super.onClose()
    }
}