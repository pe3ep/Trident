package cc.pe3epwithyou.trident.dialogs.killfeed

import cc.pe3epwithyou.trident.dialogs.DialogCollection
import cc.pe3epwithyou.trident.dialogs.TridentDialog
import cc.pe3epwithyou.trident.dialogs.themes.TridentThemed
import cc.pe3epwithyou.trident.widgets.killfeed.KillMethod
import cc.pe3epwithyou.trident.widgets.killfeed.KillType
import cc.pe3epwithyou.trident.widgets.killfeed.KillWidget
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.layouts.GridLayout

class KillFeedDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    companion object {
        private val killWidgets = mutableListOf<KillWidget>()

        fun addKill(killWidget: KillWidget) {
            killWidgets.add(killWidget)
            if (killWidgets.size > 5) {
                // Remove elements from the start to keep only last 5
                killWidgets.subList(0, killWidgets.size - 5).clear()
            }
            DialogCollection.refreshDialog("killfeed")
        }

        fun clearKills() {
            killWidgets.clear()
            DialogCollection.refreshDialog("killfeed")
        }
    }

    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font
        val self = Minecraft.getInstance().player?.name?.string!!
//        KillWidget(
//            self,
//            KillMethod.MELEE,
//            self,
//            KillType.SELF_ENEMY
//        ).atBottom(0, settings = LayoutConstants.RIGHT)
//        KillWidget(
//            self,
//            KillMethod.RANGE,
//            self,
//            KillType.TEAM_ENEMY
//        ).atBottom(0, settings = LayoutConstants.RIGHT)
//        KillWidget(
//            self,
//            KillMethod.POTION,
//            self,
//            KillType.ENEMY_SELF
//        ).atBottom(0, settings = LayoutConstants.RIGHT)
//        KillWidget(
//            self,
//            KillMethod.GENERIC,
//            self,
//            KillType.ENEMY_TEAM
//        ).atBottom(0, settings = LayoutConstants.RIGHT)

        killWidgets.forEach {
            it.atBottom(0, settings = LayoutConstants.RIGHT)
        }
    }
}