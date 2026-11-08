package cc.pe3epwithyou.trident.feature.meter

import cc.pe3epwithyou.trident.interfaces.hotbar.HotbarDecoration
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.network.chat.Component

object WeeklyVault : Meter(60) {
    private val VAULT_ICON_EMPTY = Texture(Resources.trident("textures/interface/meter/vault_empty.png"), 10, 7)
    private val VAULT_ICON_HALF = Texture(Resources.trident("textures/interface/meter/vault_half.png"), 10, 7)
    private val VAULT_ICON_FULL = Texture(Resources.trident("textures/interface/meter/vault_full.png"), 10, 7)

    override fun claimXPSolver(claim: Int): Int {
        require(claim in 1..maxSupportedClaims)

        // Last five levels always require 12000 XP
        if (claim in (totalClaims - 4)..totalClaims) {
            return 12000
        }

        return when (claim) {
             in 1..5 -> 500
             in 6..10 -> 1000
             in 11..15 -> 1500
             in 16..20 -> 2000
             in 21..25 -> 3000
             in 26..30 -> 4000
             in 31..35 -> 5000
             in 36..40 -> 6000
             in 41..45 -> 7000
             in 46..50 -> 8000
             in 51..55 -> 10000
             else -> 12000
        }
    }

    override fun getDecoration() = HotbarDecoration { graphics, _, x0, y0 ->
        val font = minecraft().font
        val text = Component.literal("$currentClaim/$totalClaims").mccFont().withoutShadow()

        val progress = when {
            totalClaims <= 0 -> 0f
            currentClaim >= totalClaims -> 1f
            else -> (totalXP.toFloat() / claimXPSolver(currentClaim + 1).toFloat()).coerceIn(0f, 1f)
        }

        // pad + vertical progress + pad + icon + pad + text + pad
        val width = 2 + 5 + 2 + 10 + 2 + font.width(text) + 2

        graphics.fill(
            x0, y0, x0 + width, y0 + 9, 0x000000.opacity(128)
        )

        renderVerticalProgress(graphics, x0 + 2, y0 + 2, progress)

        val iconX = x0 + 2 + 5 + 2
        val iconY = y0 + 1

        if (currentClaim >= totalClaims) {
            VAULT_ICON_FULL.blit(graphics, iconX, iconY)
        } else if (currentClaim == 0) {
            VAULT_ICON_EMPTY.blit(graphics, iconX, iconY)
        } else {
            VAULT_ICON_HALF.blit(graphics, iconX, iconY)
        }

        graphics.text(
            font, text, x0 + 2 + 5 + 2 + 10 + 2, y0, 0xFFFFFF.opaqueColor()
        )

        width
    }
}