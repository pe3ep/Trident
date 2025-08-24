package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.fishing.widgets.AugmentStackWidget
import cc.pe3epwithyou.trident.interfaces.fishing.widgets.OverclockStackWidget
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.utils.ComponentExtensions.withDefault
import cc.pe3epwithyou.trident.utils.ComponentExtensions.withHudMCC
import cc.pe3epwithyou.trident.utils.TridentFont
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.dialog.title.DialogTitleWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class SuppliesDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    private companion object {
        private val TITLE_COLOR: Int = 0xeb0e30 opacity 127
    }
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

        return if (TridentClient.playerState.supplies.baitDesynced) {
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
            DialogTitle(this, baseTitle.append(warn), TITLE_COLOR, tooltip = tooltip)
        } else {
            DialogTitle(this, baseTitle, TITLE_COLOR)
        }
    }

    override var title = getWidgetTitle()

    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font
        val mccIconStyle = Style.EMPTY.withFont(TridentFont.getTridentFont())
        val mccFontStyle = Style.EMPTY.withFont(TridentFont.getMCCFont())
        val supplies = TridentClient.playerState.supplies
        val isBaitDesynced = supplies.baitDesynced

        if (supplies.needsUpdating) {
            StringWidget(
                Component.literal("Fishing data missing".uppercase())
                    .withHudMCC()
                    .withStyle(ChatFormatting.GOLD),
                mcFont
            ).atBottom(0, settings = LayoutConstants.CENTRE)
            MultiLineTextWidget(
                Component.literal("""
                    In order to update 
                    the Supplies Module, 
                    please open the following 
                    menu: A.N.G.L.R. Panel -> 
                    Fishing Supplies
                """.trimIndent())
                    .withDefault()
                    .withStyle(ChatFormatting.GRAY),
                mcFont
            ).atBottom(0, settings = LayoutConstants.LEFT)
            return@grid
        }

        // Bait component
        val baitAmount = supplies.bait.amount ?: "0"
        val baitIcon: String = when (supplies.bait.type) {
            Rarity.COMMON -> "\uE007"
            Rarity.UNCOMMON -> "\uE008"
            Rarity.RARE -> "\uE009"
            Rarity.EPIC -> "\uE00A"
            Rarity.LEGENDARY -> "\uE00B"
            Rarity.MYTHIC -> "\uE00C"
        }

        val baitComponent = Component.literal(baitIcon)
            .withStyle(mccIconStyle)
            .append(Component.empty().withStyle(ChatFormatting.RESET))
            .append(
                Component.literal(" $baitAmount")
                    .withStyle(mccFontStyle)
                    .withColor(if (isBaitDesynced) ChatFormatting.GOLD.color!! else ChatFormatting.WHITE.color!!)
            )
        StringWidget(baitComponent, mcFont)
            .at(0, 0, settings = LayoutConstants.LEFT)
            .apply {
                alignLeft()
                width = 46
            }

        // Line component
        val lineDurability = supplies.line.uses ?: "0"
        val lineIcon: String = when (supplies.line.type) {
            Rarity.COMMON -> "\uE001"
            Rarity.UNCOMMON -> "\uE002"
            Rarity.RARE -> "\uE003"
            Rarity.EPIC -> "\uE004"
            Rarity.LEGENDARY -> "\uE005"
            Rarity.MYTHIC -> "\uE006"
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

        // Overclocks
        StringWidget(Component.literal("Overclocks".uppercase()).withStyle(mccFontStyle), mcFont)
            .atBottom(0, 2, LayoutConstants.LEFT)

        val stableOverclocks = listOfNotNull(
            supplies.overclocks.hook?.asociatedOverclockTexture,
            supplies.overclocks.magnet?.asociatedOverclockTexture,
            supplies.overclocks.rod?.asociatedOverclockTexture
        )
        if (stableOverclocks.isEmpty()) {
            StringWidget(
                Component.literal("OVERCLOCKS UNAVAILABLE")
                    .withStyle(mccFontStyle.withColor(ChatFormatting.GOLD)),
                mcFont
            ).atBottom(0, 2, LayoutConstants.LEFT)
        } else {
            OverclockStackWidget(
                width = 14,
                height = 14,
                stableClocks = stableOverclocks,
            ).atBottom(0, 2, LayoutConstants.LEFT)
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
        ).atBottom(0, 2, settings = LayoutConstants.LEFT)

        val augmentLine = supplies.augments.toMutableList()
        if (augmentLine.size < supplies.augmentsAvailable) {
            for (i in 1..(supplies.augmentsAvailable - augmentLine.size)) {
                augmentLine.add(Augment.EMPTY_AUGMENT)
            }
        }
        if (augmentLine.isEmpty()) {
            StringWidget(
                Component.literal("AUGMENTS UNAVAILABLE")
                    .withStyle(mccFontStyle.withColor(ChatFormatting.GOLD)),
                mcFont
            ).atBottom(0, 2, LayoutConstants.LEFT)
        } else {
            AugmentStackWidget(
                width = 12,
                height = 12,
                entries = augmentLine
            ).atBottom(0, 2, LayoutConstants.LEFT)
        }

    }

    override fun refresh() {
        title = getWidgetTitle()
        super.refresh()
    }
}