package cc.pe3epwithyou.trident.feature.meter

import cc.pe3epwithyou.trident.interfaces.hotbar.HotbarDecoration
import cc.pe3epwithyou.trident.utils.Model
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.network.chat.Component

object DailyMeter : Meter() {
    private val DAILY_ICON = Texture(Resources.trident("textures/interface/meter/daily.png"), 9, 7)
    private val DAILY_ICON_COMPLETE = Texture(Resources.trident("textures/interface/meter/daily_complete.png"), 9, 7)
    private val DAILY_ICON_GOLD = Resources.trident("interface/daily_gold_animated")

    override fun claimXPSolver(claim: Int): Int {
        require(claim in 1..maxSupportedClaims)

        return when (claim) {
            in 1..2 -> 500
            in 3..4 -> 1000
            in 5..6 -> 2000
            in 7..10 -> 3000
            in 11..14 -> 4000
            else -> 5000
        }
    }

    override fun getDecoration() = HotbarDecoration { graphics, _, x0, y0 ->
        val font = minecraft().font
        val text = Component.literal("$currentClaim/$totalClaims").mccFont().withoutShadow()

        val isComplete = currentClaim >= totalClaims

        val progress = when {
            totalClaims <= 0 -> 0f
            isComplete -> 1f
            else -> (totalXP.toFloat() / claimXPSolver(currentClaim + 1).toFloat()).coerceIn(0f, 1f)
        }

        val progressBarOffset = if (!isComplete) 5 + 2 else -1

        // pad + vertical progress + pad + icon + pad + text + pad
        val width = 2 + progressBarOffset + 9 + 2 + font.width(text) + 2

        graphics.fill(
            x0, y0, x0 + width, y0 + 9, 0x000000.opacity(128)
        )

        if (!isComplete) renderVerticalProgress(graphics, x0 + 2, y0 + 2, progress)

        val iconX = x0 + 2 + progressBarOffset
        val iconY = y0 + 1

        if (hasUnclaimedReward) {
            Model(DAILY_ICON_GOLD, 9, 7).render(
                graphics, iconX, iconY
            )
        } else if (isComplete) {
            DAILY_ICON_COMPLETE.blit(graphics, iconX, iconY)
        } else {
            DAILY_ICON.blit(graphics, iconX, iconY)
        }

        graphics.text(
            font, text, x0 + 2 + progressBarOffset + 9 + 2, y0, 0xFFFFFF.opaqueColor()
        )

        width
    }
}