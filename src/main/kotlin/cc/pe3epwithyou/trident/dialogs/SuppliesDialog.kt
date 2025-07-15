package cc.pe3epwithyou.trident.dialogs

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.dialogs.themes.DialogTheme
import cc.pe3epwithyou.trident.dialogs.themes.DialogTitle
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.widgets.fishing.AugmentStackWidget
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.dialog.Dialog
import com.noxcrew.sheeplib.dialog.title.DialogTitleWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class SuppliesDialog(x: Int, y: Int) : Dialog(x, y), Themed by DialogTheme {
    private fun getWidgetTitle(): DialogTitleWidget {
        val icon = Component.literal("\uE0E4").withStyle(Style.EMPTY.withFont(TridentFont.getMCCFont("icon")).withShadowColor(0x0 opacity 0))
        val text = Component.literal(" Supplies".uppercase()).withStyle(Style.EMPTY.withFont(TridentFont.getTridentFont("hud_title")))
        val titleWidget = DialogTitle(this, icon.append(text), 0x640000 opacity 63)
        return titleWidget
    }

    private companion object {
        private val mockAugments = listOf(
            Augment.WISE_HOOK,
            Augment.ELUSUVE_ULTRALURE,
            Augment.GRACEFUL_ROD,
            Augment.STRONG_HOOK,
            Augment.WAYFINDER_LURE,
            Augment.SPIRIT_ULTRALURE,
            Augment.XP_MAGNET,
            Augment.BOOSTED_ROD,
            Augment.GLIMMERING_HOOK,
            Augment.ELUSIVE_SODA,
        )
    }

    override val title = getWidgetTitle()
    override fun getWidth(): Int = 106
    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font
        val mccIconStyle = Style.EMPTY.withFont(TridentFont.getTridentFont())
        val mccFontStyle = Style.EMPTY.withFont(TridentFont.getMCCFont())

//        Bait component
        val baitComponent = Component.literal("\uE00A").withStyle(mccIconStyle)
            .append(Component.empty().withStyle(ChatFormatting.RESET))
            .append(Component.literal(" ${TridentClient.playerState.supplies.bait.amount}").withStyle(mccFontStyle))
        StringWidget(baitComponent, mcFont).at(0,0)
            .alignLeft()
            .width = 46


//        Line component
        val lineComponent = Component.literal("\uE004").withStyle(mccIconStyle)
            .append(Component.empty().withStyle(ChatFormatting.RESET))
            .append(Component.literal(" 25/100").withStyle(mccFontStyle))
        StringWidget(lineComponent, mcFont).at(0,1)
            .alignLeft()
            .width = 46


        StringWidget(Component.literal("Augments (4/6)".uppercase()).withStyle(mccFontStyle), mcFont).at(1,0, 1, 2, settings = LayoutConstants.LEFT)

        AugmentStackWidget(
            width = 12,
            height = 12,
            theme = this@SuppliesDialog,
            entries = mockAugments.subList(0, 7)
        ).at(2, 0, 1, 2, LayoutConstants.LEFT)
        AugmentStackWidget(
            width = 12,
            height = 12,
            theme = this@SuppliesDialog,
            entries = mockAugments.subList(7, 10)
        ).at(3, 0, 1, 2, LayoutConstants.LEFT)

        StringWidget(Component.literal("Overclocks".uppercase()).withStyle(mccFontStyle), mcFont).at(4,0, 1, 2, settings = LayoutConstants.LEFT)

    }

    fun refresh() {
        super.init()
    }

    override fun onClose() {
        TridentClient.openedDialogs.remove("supplies")
    }
}