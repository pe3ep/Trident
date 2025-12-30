package cc.pe3epwithyou.trident.interfaces.themes

import cc.pe3epwithyou.trident.interfaces.experiment.widgets.DetachIconWidget
import cc.pe3epwithyou.trident.interfaces.experiment.widgets.Tab
import cc.pe3epwithyou.trident.interfaces.experiment.widgets.TabView
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
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

class DialogTitle(
    override val dialog: Dialog,
    private val component: Component,
    private val color: Int = 0x111111 opacity 127,
    private val isCloseable: Boolean = true,
    private val tooltip: Tooltip? = null,
    private val tab: Tab? = null,
    private val tabView: TabView? = null
) :
    CompoundWidget(0, 0, dialog.width, FONT_HEIGHT + PADDING * 2),
    DialogTitleWidget,
    Themed by dialog {
    companion object {
        const val FONT_HEIGHT = 7
        const val PADDING = 4
    }

    override fun getWidth(): Int = layout.width
    override fun setWidth(i: Int) {
        layout.width = i
    }

    override fun getHeight(): Int = layout.height

    override val layout: CanvasLayout = CanvasLayout(
        100,
        FONT_HEIGHT + PADDING * 2,
    ).apply {
        val font = Minecraft.getInstance().font
        val w = StringWidget(
            component,
            font
        )
        if (tooltip != null) {
            w.setTooltip(tooltip)
        }
        w.at(top = PADDING, left = PADDING)
        if (isCloseable) {
            IconButton(
                theme.icons.close,
                marginY = PADDING + 1,
                marginX = PADDING,
            ) { _, _ -> dialog.close() }
                .at(top = 0, right = 0)
        }
        if (tab != null && tabView != null) {
            DetachIconWidget(
                theme,
                tab,
                tabView,
                marginX = PADDING,
                marginY = PADDING,
            ).at(top = 0, right = 0)
        }
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
            getWidth(),
            getHeight(),
            color
        )
        super.renderWidget(graphics, i, j, f)
    }

    override fun onDialogResize() {
        setWidth(dialog.width)
        layout.arrangeElements()
    }
}