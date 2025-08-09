package cc.pe3epwithyou.trident.dialogs.killfeed

import cc.pe3epwithyou.trident.dialogs.DialogCollection
import cc.pe3epwithyou.trident.dialogs.TridentDialog
import cc.pe3epwithyou.trident.dialogs.themes.TransparentTheme
import cc.pe3epwithyou.trident.widgets.killfeed.KillWidget
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.events.ContainerEventHandler
import net.minecraft.client.gui.layouts.GridLayout

class KillFeedDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TransparentTheme {
    override fun getX(): Int = positionKillFeed()
    companion object {
        private val killWidgets = mutableListOf<KillWidget>()

        fun addKill(killWidget: KillWidget) {
            killWidgets.add(killWidget)
            if (killWidgets.size > 5) {
                // Remove elements from the start to keep only last 5
                killWidgets.subList(0, killWidgets.size - 6).clear()
            }
            DialogCollection.refreshDialog("killfeed")
        }

        fun clearKills() {
            killWidgets.clear()
            DialogCollection.refreshDialog("killfeed")
        }
    }

    private fun positionKillFeed(): Int {
        val client = Minecraft.getInstance()
        val screenWidth = client.window.guiScaledWidth
        val pos = screenWidth - width
        return pos
    }

    override fun layout(): GridLayout = grid {
        killWidgets.forEach {
            it.atBottom(0, settings = LayoutConstants.RIGHT)
        }
    }
}