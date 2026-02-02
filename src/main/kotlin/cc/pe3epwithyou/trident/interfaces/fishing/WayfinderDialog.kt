package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.interfaces.fishing.widgets.WayfinderWidget
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.offset
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.dialog.title.DialogTitleWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component

class WayfinderDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key),
    Themed by TridentThemed {
    private companion object {
        private val TITLE_COLOR: Int = 0x7fb713 opacity 127
    }

    private fun getWidgetTitle(): DialogTitleWidget {
        val icon = FontCollection.get("_fonts/icon/fishing/wayfinder_data.png").withoutShadow()
        val text = Component.literal(" WAYFINDER".uppercase()).mccFont().offset(y = -0.5f)

        val baseTitle = icon.append(text)

        return DialogTitle(this, baseTitle, TITLE_COLOR)
    }

    override var title = getWidgetTitle()

    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font
        val wayfinderData = Trident.playerState.wayfinderData

        if (wayfinderData.needsUpdating) {
            StringWidget(
                Component.literal("Wayfinder data missing".uppercase()).mccFont()
                    .withStyle(ChatFormatting.GOLD), mcFont
            ).atBottom(0, settings = LayoutConstants.CENTRE)
            MultiLineTextWidget(
                Component.literal(
                    """
                    In order to update 
                    the Wayfinder Data Module, 
                    please open the following 
                    menu: Navigator -> 
                    Fishing
                """.trimIndent()
                ).defaultFont().withStyle(ChatFormatting.GRAY), mcFont
            ).atBottom(0, settings = LayoutConstants.LEFT)
            return@grid
        }
        if (wayfinderData.temperate.unlocked) WayfinderWidget(
            wayfinderData.temperate,
            this@WayfinderDialog
        ).atBottom(
            0, settings = LayoutConstants.LEFT
        )
        if (wayfinderData.tropical.unlocked) WayfinderWidget(
            wayfinderData.tropical,
            this@WayfinderDialog
        ).atBottom(
            0, settings = LayoutConstants.LEFT
        )
        if (wayfinderData.barren.unlocked) WayfinderWidget(
            wayfinderData.barren,
            this@WayfinderDialog
        ).atBottom(
            0, settings = LayoutConstants.LEFT
        )
    }

    override fun refresh() {
        title = getWidgetTitle()
        super.refresh()
    }
}