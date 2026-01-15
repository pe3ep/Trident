package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.interfaces.fishing.widgets.AugmentStackWidget
import cc.pe3epwithyou.trident.interfaces.fishing.widgets.OverclockStackWidget
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.AugmentContainer
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.fishing.Augment
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
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component

// TODO: Rewrite this dialog to be much cleaner
class SuppliesDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key),
    Themed by TridentThemed {
    private companion object {
        private val TITLE_COLOR: Int = 0xeb0e30 opacity 127
    }

    private fun getWidgetTitle(): DialogTitleWidget {
        val icon =
            FontCollection.get("_fonts/icon/fishing_perk/supply_preserve.png").withoutShadow()
        val text = Component.literal(" SUPPLIES".uppercase()).mccFont().offset(y = -0.5f)

        val baseTitle = icon.append(text)

        return if (Trident.playerState.supplies.baitDesynced) {
            val warn = Component.literal(" ⚠").defaultFont().withStyle(ChatFormatting.GOLD)
            val tooltip = Tooltip.create(Component.translatable("trident.dialog.supplies.desynced.tooltip"))
            DialogTitle(this, baseTitle.append(warn), TITLE_COLOR, tooltip = tooltip)
        } else {
            DialogTitle(this, baseTitle, TITLE_COLOR)
        }
    }

    override var title = getWidgetTitle()

    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font
        val supplies = Trident.playerState.supplies
        val isBaitDesynced = supplies.baitDesynced

        if (supplies.needsUpdating) {
            StringWidget(
                Component.translatable("trident.dialog.supplies.missing_data.title").mccFont()
                    .withStyle(ChatFormatting.GOLD), mcFont
            ).atBottom(0, settings = LayoutConstants.CENTRE)
            MultiLineTextWidget(
                Component.translatable("trident.dialog.supplies.missing_data.description").defaultFont().withStyle(ChatFormatting.GRAY), mcFont
            ).setMaxWidth(150).atBottom(0, settings = LayoutConstants.LEFT)
            return@grid
        }

        // Bait component
        val baitAmount = supplies.bait.amount ?: 0

        val baitComponent =
            FontCollection.texture("island_items/infinibag/fishing_item/bait_${supplies.bait.type.name.lowercase()}")
                .offset(y = 0.5f)
                .append(
                    Component.literal(" $baitAmount").mccFont()
                        .withColor(if (isBaitDesynced) ChatFormatting.GOLD.color!! else supplies.bait.type.color)
                )
        StringWidget(baitComponent, mcFont).at(0, 0, settings = LayoutConstants.LEFT)

        // Line component
        val lineDurability = supplies.line.uses ?: 0
        val lineAmount = supplies.line.amount ?: 0

        val lineComponent =
            FontCollection.texture("island_items/infinibag/fishing_item/line_${supplies.line.type.name.lowercase()}")
                .offset(y = 0.5f)
                .append(Component.empty().withStyle(ChatFormatting.RESET))
                .append(
                    Component.literal(" $lineDurability/$lineAmount").mccFont()
                        .withColor(supplies.line.type.color)
                )
        StringWidget(lineComponent, mcFont).at(0, 1, settings = LayoutConstants.LEFT)

        // Overclocks
        StringWidget(Component.literal("Overclocks".uppercase()).mccFont(), mcFont).atBottom(
            0,
            2,
            LayoutConstants.LEFT
        )

        val stableOverclocks = listOfNotNull(
            supplies.overclocks.hook?.texture,
            supplies.overclocks.magnet?.texture,
            supplies.overclocks.rod?.texture
        )
        if (stableOverclocks.isEmpty()) {
            StringWidget(
                Component.literal("NONE").mccFont().withStyle(ChatFormatting.GRAY), mcFont
            ).atBottom(0, 2, LayoutConstants.LEFT)
        } else {
            OverclockStackWidget(
                width = 14,
                height = 14,
                stableClocks = stableOverclocks,
            ).atBottom(0, 2, LayoutConstants.LEFT)
        }

        // Augments
        val augmentsEquipped = supplies.augmentContainers.size
        val augmentsTotal = supplies.augmentsAvailable
        StringWidget(
            Component.literal("AUGMENTS ").mccFont().append(
                Component.literal("($augmentsEquipped/$augmentsTotal)").mccFont()
                    .withStyle(ChatFormatting.GRAY)
            ), mcFont
        ).atBottom(0, 2, settings = LayoutConstants.LEFT)

        val augmentLine = supplies.augmentContainers.toMutableList()
        if (augmentLine.size < supplies.augmentsAvailable) {
            repeat((1..(supplies.augmentsAvailable - augmentLine.size)).count()) {
                augmentLine.add(AugmentContainer(Augment.EMPTY_AUGMENT))
            }
        }
        if (augmentLine.isEmpty()) {
            StringWidget(
                Component.literal("NONE").mccFont().withStyle(ChatFormatting.GRAY), mcFont
            ).atBottom(0, 2, LayoutConstants.LEFT)
        } else {
            AugmentStackWidget(
                width = 12, height = 12, entries = augmentLine
            ).atBottom(0, 2, LayoutConstants.LEFT)
        }

    }

    override fun refresh() {
        title = getWidgetTitle()
        super.refresh()
    }
}