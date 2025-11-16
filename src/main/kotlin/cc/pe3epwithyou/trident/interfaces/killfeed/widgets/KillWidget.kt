package cc.pe3epwithyou.trident.interfaces.killfeed.widgets

import cc.pe3epwithyou.trident.feature.killfeed.KillMethod
import cc.pe3epwithyou.trident.utils.TridentColor
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.LinearLayout
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.layouts.LinearLayout

class KillWidget(
    val victim: String,
    val killMethod: KillMethod,
    val attacker: String? = null,
    val killColors: Pair<Int, Int>,
    val streak: Int = 0,
    val hasAssist: Boolean = false
) : CompoundWidget(0, 0, 0, 0) {

    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height
    override val layout: LinearLayout = LinearLayout(
        LinearLayout.Orientation.HORIZONTAL, 0
    ) {
        val self = Minecraft.getInstance().player?.name?.string ?: "Unknown"

        val firstSelfColor = TridentColor(killColors.first).color opacity 192
        val secondSelfColor = TridentColor(killColors.second).color opacity 192
        val attackerColor = if (self == attacker) firstSelfColor else killColors.first
        val victimColor = if (self == victim) secondSelfColor else killColors.second

        if (attacker != null) {
            if (hasAssist) {
                +KillAssist(firstSelfColor)
            }
            if (streak in 2..3) {
                +KillStreak(attackerColor, streak)
            }
            if (streak >= 4) {
                +KillStreakFire()
            }
            +KillBackground(attackerColor, attacker, killMethod, isSelf = (self == attacker))
            +KillTransition(attackerColor, victimColor)
            +KillBackground(victimColor, victim, isLeft = false, isSelf = (self == victim))
        } else {
            +KillBackground(attackerColor, killMethod = killMethod)
            +KillTransition(attackerColor, victimColor)
            +KillBackground(victimColor, victim, isLeft = false, isSelf = (self == victim))
        }
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}