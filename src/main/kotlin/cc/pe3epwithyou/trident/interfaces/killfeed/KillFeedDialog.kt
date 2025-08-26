package cc.pe3epwithyou.trident.interfaces.killfeed

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.killfeed.Position
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.killfeed.widgets.KillWidget
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.TransparentTheme
import cc.pe3epwithyou.trident.utils.DelayedAction
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.layouts.GridLayout

class KillFeedDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TransparentTheme {
    override fun getX(): Int = positionKillFeed()
    override fun getY(): Int = TOP_OFFSET

    companion object {
        private const val TOP_OFFSET = 20
        private val killWidgets = mutableListOf<KillWidget>()

        fun addKill(killWidget: KillWidget) {
            killWidgets.add(killWidget)
            val maxKills = Config.KillFeed.maxKills
            if (killWidgets.size > maxKills) {
                // Remove elements from the start to keep only last 5
                killWidgets.subList(0, killWidgets.size - maxKills).clear()
            }
            DialogCollection.refreshDialog("killfeed")
            val delay = Config.KillFeed.removeKillTime
            if (delay != 0) {
                DelayedAction.delay(delay * 1000L) {
                    removeWidget(killWidget)
                }
            }
        }

        private fun removeWidget(widget: KillWidget) {
            killWidgets.remove(widget)
            DialogCollection.refreshDialog("killfeed")
        }

        fun clearKills() {
            killWidgets.clear()
            DialogCollection.refreshDialog("killfeed")
        }
    }


    /** Disable dragging */
    override fun isDragging(): Boolean = false

    private fun positionKillFeed(): Int {
        val client = Minecraft.getInstance()
        val screenWidth = client.window.guiScaledWidth
        val pos = when (Config.KillFeed.positionSide) {
            Position.LEFT -> 0
            Position.RIGHT -> screenWidth - width
        }
        return pos

    }


    override fun layout(): GridLayout = grid {
        val side = when (Config.KillFeed.positionSide) {
            Position.RIGHT -> LayoutConstants.RIGHT
            Position.LEFT -> LayoutConstants.LEFT
        }
        val widgets = killWidgets.toMutableList()
        if (Config.KillFeed.reverseOrder) {
            widgets.reverse()
        }
        widgets.forEach {
            it.atBottom(0, settings = side)
        }
    }
}