package cc.pe3epwithyou.trident.dialogs

import cc.pe3epwithyou.trident.dialogs.themes.DialogTitle
import cc.pe3epwithyou.trident.dialogs.themes.TridentThemed
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.OverclockTexture
import cc.pe3epwithyou.trident.widgets.fishing.AugmentStackWidget
import cc.pe3epwithyou.trident.widgets.fishing.OverclockStackWidget
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component

class DebugDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    override val title = DialogTitle(this, Component.literal("Test dialog"))

    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font
        val augmentLine = listOf(
            Augment.STABLE_ROD,
            Augment.RARITY_ROD,
            Augment.STABLE_ROD,
        )
        AugmentStackWidget(
            width = 12,
            height = 12,
            theme = this@DebugDialog,
            entries = augmentLine
        ).atBottom(0, 2, LayoutConstants.LEFT)
    }
}