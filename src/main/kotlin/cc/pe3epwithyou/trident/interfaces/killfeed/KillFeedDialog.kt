package cc.pe3epwithyou.trident.interfaces.killfeed

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.killfeed.KillfeedLifecycle
import cc.pe3epwithyou.trident.feature.killfeed.KillfeedPosition
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.TransparentTheme
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.gui.layouts.GridLayout

class KillFeedDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key),
    Themed by TransparentTheme {
    override fun getX(): Int = positionKillFeed()
    override fun getY(): Int = Config.KillFeed.positionY

    /** Disable dragging */
    override fun isDragging(): Boolean = false

    private fun positionKillFeed(): Int {
        val client = minecraft()
        val screenWidth = client.window.guiScaledWidth

        return when (Config.KillFeed.positionSide) {
            KillfeedPosition.LEFT -> 0
            KillfeedPosition.RIGHT -> screenWidth - width
        }
    }

    override fun layout(): GridLayout = grid {
        val side = when (Config.KillFeed.positionSide) {
            KillfeedPosition.RIGHT -> LayoutConstants.RIGHT
            KillfeedPosition.LEFT -> LayoutConstants.LEFT
        }

        val widgets = KillfeedLifecycle.killWidgets.values.toMutableList()

        if (Config.KillFeed.reverseOrder) {
            widgets.reverse()
        }

        widgets.forEach {
            it.atBottom(0, settings = side)
        }
    }
}