package cc.pe3epwithyou.trident.feature.crafting

import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.toasts.Toast
import net.minecraft.client.gui.components.toasts.ToastManager
import net.minecraft.network.chat.Component

class CraftingToast(
    val notif: CraftingNotifications.Notification
) : Toast {
    companion object {
        private const val TOAST_DURATION_MS = 4000L
        private val FUSION_ICON =
            Texture(Resources.trident("textures/interface/crafting/fusion.png"), 16, 16)
        private val ASSEMBLER_ICON =
            Texture(Resources.trident("textures/interface/crafting/blueprint.png"), 16, 16)
    }

    private var wantedVisibility: Toast.Visibility = Toast.Visibility.HIDE

    private val sourceTexture: Texture
        get() = if (notif.source == CraftingNotifications.Source.FUSION) FUSION_ICON else ASSEMBLER_ICON

    override fun height(): Int = 26

    override fun width(): Int = 180

    override fun getWantedVisibility(): Toast.Visibility = this.wantedVisibility

    override fun update(
        toastManager: ToastManager,
        l: Long
    ) {
        this.wantedVisibility =
            if (l > TOAST_DURATION_MS * toastManager.notificationDisplayTimeMultiplier) Toast.Visibility.HIDE else Toast.Visibility.SHOW
    }

    override fun render(
        guiGraphics: GuiGraphics,
        font: Font,
        l: Long
    ) {
        guiGraphics.fillRoundedAll(1, 2, this.width() - 3, this.height() - 2, 0x111111 opacity 192)
        sourceTexture.blit(guiGraphics, 5, 6)
        val notification = this.notif
        val component =
            Component.literal("${notification.itemName}${if (notification.count > 1) " x${notification.count}" else ""}")
                .withColor(notification.rarity.color)
        guiGraphics.drawString(font, component, 26, 6, 0xFFFFFF.opaqueColor())
        guiGraphics.drawString(
            font, Component.literal("has finished crafting").withSwatch(
                TridentFont.TRIDENT_COLOR
            ), 26, 15, 0xFFFFFF.opaqueColor()
        )
    }
}