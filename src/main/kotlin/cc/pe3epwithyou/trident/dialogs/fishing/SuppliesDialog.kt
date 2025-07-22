package cc.pe3epwithyou.trident.dialogs.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.dialogs.TridentDialog
import cc.pe3epwithyou.trident.dialogs.themes.DialogTitle
import cc.pe3epwithyou.trident.dialogs.themes.TridentThemed
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.widgets.fishing.AugmentStackWidget
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.dialog.title.DialogTitleWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Theme
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class SuppliesDialog(x: Int, y: Int) : TridentDialog(x, y), Themed by TridentThemed {
    private fun getWidgetTitle(): DialogTitleWidget {
        val icon = Component.literal("\uE10C").withStyle(Style.EMPTY.withFont(TridentFont.getMCCFont("icon")).withShadowColor(0x0 opacity 0))
        val text = Component.literal(" Supplies".uppercase()).withStyle(Style.EMPTY.withFont(TridentFont.getTridentFont("hud_title")))
        if (TridentClient.playerState.supplies.updateRequired) {
            val warn = Component.literal(" ⚠").withStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT).withColor(ChatFormatting.GOLD))
            val tooltip = Tooltip.create(Component.literal("Module is not synced").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("\nTrident has detected that you've received bait, meaning your Supply Module is out of date. \nPlease open your Supply menu to update it.")
                    .withStyle(ChatFormatting.GRAY))
            )
            val titleWidget = DialogTitle(this, icon.append(text).append(warn), 0x640000 opacity 63, tooltip = tooltip, isCloseable = false)
            return titleWidget
        }
        val titleWidget = DialogTitle(this, icon.append(text), 0x640000 opacity 63, isCloseable = false)
        return titleWidget
    }

    override var title = getWidgetTitle()
    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font
        val mccIconStyle = Style.EMPTY.withFont(TridentFont.getTridentFont())
        val mccFontStyle = Style.EMPTY.withFont(TridentFont.getMCCFont())
        val isDesynced = TridentClient.playerState.supplies.updateRequired
//        Bait component
        val baitComponent = Component.literal("\uE00A").withStyle(mccIconStyle)
            .append(Component.empty().withStyle(ChatFormatting.RESET))
            .append(Component.literal(" ${TridentClient.playerState.supplies.bait.amount ?: "0"}").withStyle(mccFontStyle).withColor(if (isDesynced) ChatFormatting.GOLD.color!! else ChatFormatting.WHITE.color!!))
        StringWidget(baitComponent, mcFont).at(0,0, settings = LayoutConstants.LEFT)
            .alignLeft()
            .width = 46


//        Line component
        val lineDurability = TridentClient.playerState.supplies.line.uses
        val lineComponent = Component.literal("\uE004").withStyle(mccIconStyle)
            .append(Component.empty().withStyle(ChatFormatting.RESET))
            .append(Component.literal(" ${lineDurability ?: "0"}/50").withStyle(mccFontStyle))
        StringWidget(lineComponent, mcFont).at(0,1, settings = LayoutConstants.LEFT)
            .alignLeft()
            .width = 46

        val augmentsEquipped = TridentClient.playerState.supplies.augments.size
        val augmentsTotal = TridentClient.playerState.supplies.augmentsAvailable
        StringWidget(Component.literal("Augments ".uppercase()).withStyle(mccFontStyle)
            .append(Component.literal("(${augmentsEquipped}/${augmentsTotal})").withStyle(mccFontStyle.withColor(ChatFormatting.GRAY))), mcFont)
            .at(1,0, 1, 2, settings = LayoutConstants.LEFT)

        val augmentLine1 = mutableListOf<Augment>()
        val augmentLine2 = mutableListOf<Augment>()
        TridentClient.playerState.supplies.augments.forEach { augment: Augment ->
            if (augmentLine1.size <= 7) {
                augmentLine1.add(augment)
            } else {
                augmentLine2.add(augment)
            }
        }
        if (augmentLine1.isEmpty()) {
            StringWidget(Component.literal("No augments selected".uppercase()).withStyle(
                mccFontStyle.withColor(ChatFormatting.GOLD)
            ), mcFont).at(2, 0, 1, 2, LayoutConstants.CENTRE)
        } else {
            AugmentStackWidget(
                width = 12,
                height = 12,
                theme = this@SuppliesDialog,
                entries = augmentLine1
            ).at(2, 0, 1, 2, LayoutConstants.LEFT)
        }

        if (augmentLine2.isNotEmpty()) {
            AugmentStackWidget(
                width = 12,
                height = 12,
                theme = this@SuppliesDialog,
                entries = augmentLine2
            ).at(3, 0, 1, 2, LayoutConstants.LEFT)
        }

        StringWidget(Component.literal("Overclocks".uppercase()).withStyle(mccFontStyle), mcFont).at(4,0, 1, 2, settings = LayoutConstants.LEFT)

    }

    override fun refresh() {
        this.title = getWidgetTitle()
        super.refresh()
    }
}