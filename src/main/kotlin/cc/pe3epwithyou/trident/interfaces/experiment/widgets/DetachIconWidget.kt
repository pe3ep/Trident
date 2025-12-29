import cc.pe3epwithyou.trident.interfaces.experiment.widgets.Tab
import cc.pe3epwithyou.trident.interfaces.experiment.widgets.TabView
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

class DetachIconWidget(
    val themed: Themed,
    val tab: Tab,
    val view: TabView,
    val marginY: Int = 0,
    val marginX: Int = 0,
    size: Int = FONT_HEIGHT
) : AbstractWidget(0, 0, size + marginX * 2, size + marginY * 2, Component.empty()) {
    companion object {
        const val FONT_HEIGHT: Int = 8
    }

    override fun renderWidget(
        graphics: GuiGraphics, i: Int, j: Int, f: Float
    ) {
        when {
            isHovered() -> if (tab.isDetached) Tab.ATTACH_ICON else Tab.DETACH_ICON
            else -> if (tab.isDetached) Tab.DETACH_ICON else tab.icon
        }.blit(
            graphics,
            x + marginX,
            y + marginY,
        )
    }

    override fun onClick(mouseButtonEvent: MouseButtonEvent, bl: Boolean) {
        if (tab.isDetached) {
            view.attachTab(tab)
            return
        }
        view.detachTab(tab)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput): Unit = Unit

}