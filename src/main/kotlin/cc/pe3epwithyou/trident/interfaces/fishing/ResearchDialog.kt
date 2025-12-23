package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.fishing.widgets.ResearchWidget
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withTridentFont
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
import net.minecraft.network.chat.Style

class ResearchDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    private companion object {
        private val TITLE_COLOR: Int = 0x2199f0 opacity 127
    }

    private fun getWidgetTitle(): DialogTitleWidget {
        val icon = Component.literal("\uE10C")
            .mccFont("icon")
            .withStyle(
                Style.EMPTY
                    .withShadowColor(0x0 opacity 0)
            )
        val text = Component.literal(" Fishing Research".uppercase())
            .withTridentFont("hud_title")

        val baseTitle = icon.append(text)

        return DialogTitle(this, baseTitle, TITLE_COLOR)
    }

    override var title = getWidgetTitle()

    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font
        val research = TridentClient.playerState.research

        if (research.needsUpdating or research.researchTypes.isEmpty()) {
            StringWidget(
                Component.literal("Research data missing".uppercase())
                    .mccFont()
                    .withStyle(ChatFormatting.GOLD),
                mcFont
            ).atBottom(0, settings = LayoutConstants.CENTRE)
            MultiLineTextWidget(
                Component.literal(
                    """
                    In order to update 
                    the Research Data Module, 
                    please open the following 
                    menu: A.N.G.L.R Panel -> 
                    Fishing Progress
                """.trimIndent()
                )
                    .defaultFont()
                    .withStyle(ChatFormatting.GRAY),
                mcFont
            ).atBottom(0, settings = LayoutConstants.LEFT)
            return@grid
        }

        for (research in TridentClient.playerState.research.researchTypes) {
            ResearchWidget(research, this@ResearchDialog).atBottom(0, settings = LayoutConstants.LEFT)
        }
    }

    override fun refresh() {
        title = getWidgetTitle()
        super.refresh()
    }
}