package cc.pe3epwithyou.trident.dialogs.questing

import cc.pe3epwithyou.trident.utils.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.dialog.Dialog
import com.noxcrew.sheeplib.dialog.title.DialogTitleWidget
import com.noxcrew.sheeplib.layout.CanvasLayout
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.widget.IconButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component

class QuestDialogTitle(
    override val dialog: Dialog,
    private val component: Component,
    private val color: Int = FALLBACK_COLOR,
    private val isCloseable: Boolean = true,
    private val tooltip: Tooltip? = null,
    private val game: Component,
    private val gameColor: Int = FALLBACK_COLOR
) :
    CompoundWidget(0, 0, dialog.width, FONT_HEIGHT + dialog.theme.dimensions.paddingOuter * 2),
    DialogTitleWidget,
    Themed by dialog {
    companion object {
        const val FONT_HEIGHT = 7
        const val PADDING = 1
        val FALLBACK_COLOR = 0x111111 opacity 127
    }

    override fun getWidth(): Int = layout.width
    override fun setWidth(i: Int) {
        layout.width = i
    }

    override fun getHeight(): Int = layout.height

    override val layout: CanvasLayout = CanvasLayout(
        100,
        FONT_HEIGHT + theme.dimensions.paddingOuter * 2,
    ).apply {
        val font = Minecraft.getInstance().font
        val rightPadding = height + PADDING

        val w = StringWidget(
            component,
            font
        )
        if (tooltip != null) {
            w.setTooltip(tooltip)
        }
        w.at(top = theme.dimensions.paddingOuter, left = theme.dimensions.paddingOuter)

        if (isCloseable) {
            IconButton(
                theme.icons.close,
                marginY = theme.dimensions.paddingOuter + 1,
                marginX = theme.dimensions.paddingOuter,
            ) { _, _ -> dialog.close() }
                .at(top = 0, right = rightPadding)
        }

        val icon = StringWidget(
            game,
            font
        )
        icon.alignRight()
        icon.at(top = theme.dimensions.paddingOuter, right = theme.dimensions.paddingOuter - 1)
    }

    override fun getRectangle(): ScreenRectangle = super<DialogTitleWidget>.getRectangle()

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }

    override fun renderWidget(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
        graphics.fillRoundedAll(
            x,
            y,
            getWidth() - getHeight() - PADDING,
            getHeight(),
            color
        )
        graphics.fillRoundedAll(
            x + getWidth() - getHeight(),
            y,
            getHeight(),
            getHeight(),
            gameColor
        )
        super.renderWidget(graphics, i, j, f)
    }

    override fun onDialogResize() {
        setWidth(dialog.width)
        layout.arrangeElements()
    }
}