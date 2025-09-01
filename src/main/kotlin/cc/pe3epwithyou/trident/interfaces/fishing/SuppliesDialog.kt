package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.fishing.widgets.AugmentStackWidget
import cc.pe3epwithyou.trident.interfaces.fishing.widgets.OverclockStackWidget
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.state.fishing.Augment
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
            .mccFont("icon")
            .withStyle(
                Style.EMPTY
                    .withShadowColor(0x0 opacity 0)
            )
        val text = Component.literal(" SUPPLIES".uppercase())
            .withTridentFont("hud_title")

        val baseTitle = icon.append(text)

        return if (TridentClient.playerState.supplies.baitDesynced) {
            val warn = Component.literal(" ⚠")
                .defaultFont()
                .withStyle(ChatFormatting.GOLD)
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
        val supplies = TridentClient.playerState.supplies
        val isBaitDesynced = supplies.baitDesynced

        if (supplies.needsUpdating) {
            StringWidget(
                Component.literal("Fishing data missing".uppercase())
                    .mccFont()
                    .withStyle(ChatFormatting.GOLD),
                mcFont
            ).atBottom(0, settings = LayoutConstants.CENTRE)
            MultiLineTextWidget(
                Component.literal(
                    """
                    In order to update 
                    the Supplies Module, 
                    please open the following 
                    menu: A.N.G.L.R. Panel -> 
                    Fishing Supplies
                """.trimIndent()
                )
                    .defaultFont()
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
            .withTridentFont()
            .append(Component.empty().withStyle(ChatFormatting.RESET))
            .append(
                Component.literal(" $baitAmount")
                    .mccFont()
                    .withColor(if (isBaitDesynced) ChatFormatting.GOLD.color!! else supplies.bait.type.color)
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
            .withTridentFont()
            .append(Component.empty().withStyle(ChatFormatting.RESET))
            .append(Component.literal(" $lineDurability/50").mccFont().withColor(supplies.line.type.color))
        StringWidget(lineComponent, mcFont)
            .at(0, 1, settings = LayoutConstants.LEFT)
            .apply {
                alignLeft()
                width = 46
            }

        // Overclocks
        StringWidget(Component.literal("Overclocks".uppercase()).mccFont(), mcFont)
            .atBottom(0, 2, LayoutConstants.LEFT)

        val stableOverclocks = listOfNotNull(
            supplies.overclocks.hook?.asociatedOverclockTexture,
            supplies.overclocks.magnet?.asociatedOverclockTexture,
            supplies.overclocks.rod?.asociatedOverclockTexture
        )
        if (stableOverclocks.isEmpty()) {
            StringWidget(
                Component.literal("OVERCLOCKS UNAVAILABLE")
                    .mccFont()
                    .withStyle(ChatFormatting.GOLD),
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
                .mccFont()
                .append(
                    Component.literal("($augmentsEquipped/$augmentsTotal)")
                        .mccFont()
                        .withStyle(ChatFormatting.GRAY)
                ),
            mcFont
        ).atBottom(0, 2, settings = LayoutConstants.LEFT)

        val augmentLine = supplies.augments.toMutableList()
        if (augmentLine.isEmpty()) {
            StringWidget(
                Component.literal("AUGMENTS UNAVAILABLE")
                    .mccFont()
                    .withStyle(ChatFormatting.GOLD),
                mcFont
            ).atBottom(0, 2, LayoutConstants.LEFT)
        } else {
            AugmentStackWidget(
                width = 12,
                height = 12,
                entries = augmentLine
            ).atBottom(0, 2, LayoutConstants.LEFT)
        }

        // Footnote
        StringWidget(Component.literal("Module Credit: pe3ep, Hydrogen").mccFont().withStyle(ChatFormatting.GRAY), mcFont)
            .atBottom(0, 3, LayoutConstants.LEFT)
    }

    override fun refresh() {
        title = getWidgetTitle()
        super.refresh()
    }
}