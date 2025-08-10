package cc.pe3epwithyou.trident.dialogs

import cc.pe3epwithyou.trident.utils.ChatUtils
import com.noxcrew.sheeplib.dialog.Dialog
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import kotlin.jvm.optionals.getOrNull

abstract class TridentDialog(x: Int, y: Int, private val key: String) : Dialog(x, y) {
    private companion object {
        private val ROUNDED_BOTTOM = ResourceLocation.fromNamespaceAndPath("trident", "interface/background/rounded_bottom")
        private val ROUNDED_ALL = ResourceLocation.fromNamespaceAndPath("trident", "interface/background/rounded_all")
        private val BORDER_BOTTOM = ResourceLocation.fromNamespaceAndPath("trident", "interface/background/border/border_bottom")
        private val BORDER_ALL = ResourceLocation.fromNamespaceAndPath("trident", "interface/background/border/border_all")
    }

    override fun renderBackground(graphics: GuiGraphics) {
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            ROUNDED_ALL,
            x,
            y,
            getWidth(),
            getHeight(),
            if (parent == null) theme.colors.dialogBackgroundAlt else theme.colors.dialogBackground
        )

        if (theme.dialogBorders) {
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
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