package cc.pe3epwithyou.trident.dialogs.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.dialogs.TridentDialog
import cc.pe3epwithyou.trident.dialogs.themes.DialogTitle
import cc.pe3epwithyou.trident.dialogs.themes.TridentThemed
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.widgets.fishing.AugmentStackWidget
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.dialog.title.DialogTitleWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class SuppliesDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    private fun getWidgetTitle(): DialogTitleWidget {
        val icon = Component.literal("\uE10C")
            .withStyle(
                Style.EMPTY
                    .withFont(TridentFont.getMCCFont("icon"))
                    .withShadowColor(0x0 opacity 0)
            )
        val text = Component.literal(" SUPPLIES".uppercase())
            .withStyle(Style.EMPTY.withFont(TridentFont.getTridentFont("hud_title")))

        val baseTitle = icon.append(text)

        return if (TridentClient.playerState.supplies.updateRequired) {
            val warn = Component.literal(" ⚠")
                .withStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT).withColor(ChatFormatting.GOLD))
            val tooltip = Tooltip.create(
                Component.literal("Module is not synced").withStyle(ChatFormatting.GOLD)
                    .append(
                        Component.literal(
                            "\nTrident has detected that you've received bait, meaning your Supply Module is out of date. " +
                                    "\nPlease open your Supply menu to update it."
                        ).withStyle(ChatFormatting.GRAY)
                    )
            )
            DialogTitle(this, baseTitle.append(warn), 0x640000 opacity 127, tooltip = tooltip, isCloseable = false)
        } else {
            DialogTitle(this, baseTitle, 0x640000 opacity 127, isCloseable = false)
        }
    }

    override var title = getWidgetTitle()

    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font
        val mccIconStyle = Style.EMPTY.withFont(TridentFont.getTridentFont())
        val mccFontStyle = Style.EMPTY.withFont(TridentFont.getMCCFont())
        val supplies = TridentClient.playerState.supplies
        val isDesynced = supplies.updateRequired

        // Bait component
        val baitAmount = supplies.bait.amount ?: "0"
        val baitIcon: String = when {
            supplies.bait.type == Rarity.COMMON    -> "\uE007"
            supplies.bait.type == Rarity.UNCOMMON  -> "\uE008"
            supplies.bait.type == Rarity.RARE      -> "\uE009"
            supplies.bait.type == Rarity.EPIC      -> "\uE00A"
            supplies.bait.type == Rarity.LEGENDARY -> "\uE00B"
            supplies.bait.type == Rarity.MYTHIC    -> "\uE00C"
            else -> "\uE007"
        }

        val baitComponent = Component.literal(baitIcon)
            .withStyle(mccIconStyle)
            .append(Component.empty().withStyle(ChatFormatting.RESET))
            .append(
                Component.literal(" $baitAmount")
                    .withStyle(mccFontStyle)
                    .withColor(if (isDesynced) ChatFormatting.GOLD.color!! else ChatFormatting.WHITE.color!!)
            )
        StringWidget(baitComponent, mcFont)
            .at(0, 0, settings = LayoutConstants.LEFT)
            .apply {
                alignLeft()
                width = 46
            }

        // Line component
        val lineDurability = supplies.line.uses ?: "0"
        val lineIcon: String = when {
            supplies.line.type == Rarity.COMMON    -> "\uE001"
            supplies.line.type == Rarity.UNCOMMON  -> "\uE002"
            supplies.line.type == Rarity.RARE      -> "\uE003"
            supplies.line.type == Rarity.EPIC      -> "\uE004"
            supplies.line.type == Rarity.LEGENDARY -> "\uE005"
            supplies.line.type == Rarity.MYTHIC    -> "\uE006"
            else -> "\uE007"
        }

        val lineComponent = Component.literal(lineIcon)
            .withStyle(mccIconStyle)
            .append(Component.empty().withStyle(ChatFormatting.RESET))
            .append(Component.literal(" $lineDurability/50").withStyle(mccFontStyle))
        StringWidget(lineComponent, mcFont)
            .at(0, 1, settings = LayoutConstants.LEFT)
            .apply {
                alignLeft()
                width = 46
            }

        // Augments
        val augmentsEquipped = supplies.augments.size
        val augmentsTotal = supplies.augmentsAvailable
        StringWidget(
            Component.literal("AUGMENTS ")
                .withStyle(mccFontStyle)
                .append(
                    Component.literal("($augmentsEquipped/$augmentsTotal)")
                        .withStyle(mccFontStyle.withColor(ChatFormatting.GRAY))
                ),
            mcFont
        ).at(1, 0, 1, 2, settings = LayoutConstants.LEFT)

        // Split augments into two lines
        val augmentLine1 = mutableListOf<Augment>()
        val augmentLine2 = mutableListOf<Augment>()
        supplies.augments.forEach { augment ->
            if (augmentLine1.size < 8) {
                augmentLine1.add(augment)
            } else {
                augmentLine2.add(augment)
            }
        }

        if (augmentLine1.isEmpty()) {
            StringWidget(
                Component.literal("NO AUGMENTS SELECTED")
                    .withStyle(mccFontStyle.withColor(ChatFormatting.GOLD)),
                mcFont
            ).at(2, 0, 1, 2, LayoutConstants.CENTRE)
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

//        TODO: Overclocks
//        StringWidget(Component.literal("Overclocl".uppercase()).withStyle(mccFontStyle), mcFont)
//            .at(4, 0, 1, 2, settings = LayoutConstants.LEFT)
    }

    override fun refresh() {
        title = getWidgetTitle()
        super.refresh()
    }
}