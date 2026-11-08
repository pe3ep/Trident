package cc.pe3epwithyou.trident.feature.meter

import cc.pe3epwithyou.trident.interfaces.hotbar.HotbarDecoration
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.GuiGraphicsExtractor

abstract class Meter(
    protected val maxSupportedClaims: Int = 15
) {
    init {
        require(maxSupportedClaims > 0)
    }

    protected var totalXP: Int = 0
        private set

    protected var currentClaim: Int = 0
        private set

    protected var totalClaims: Int = 0
        private set

    protected var hasUnclaimedReward: Boolean = false
        private set

    /**
     * Handles incoming XP, including XP carried over from completed claims.
     */
    fun handleIncomingXP(amount: Int) {
        if (amount <= 0 || currentClaim >= totalClaims) return

        var remainingXP = totalXP.toLong() + amount.toLong()

        while (currentClaim < totalClaims) {
            val requiredXP = claimXPSolver(currentClaim + 1)
            require(requiredXP > 0) {
                "Claim XP requirement must be greater than zero."
            }

            if (remainingXP < requiredXP) break

            remainingXP -= requiredXP
            currentClaim++
            hasUnclaimedReward = true
        }

        totalXP = if (currentClaim >= totalClaims) {
            0
        } else {
            remainingXP.toInt()
        }
    }

    /**
     * Manually sets the XP progress for the current claim.
     * Used when refreshing from the Journal.
     */
    fun setCurrentXP(amount: Int) {
        totalXP = amount.coerceAtLeast(0)
        hasUnclaimedReward = false
    }

    /**
     * Manually sets the current completed claim count.
     */
    fun setCurrentClaim(amount: Int) {
        require(amount in 0..maxSupportedClaims)
        currentClaim = amount
    }

    /**
     * Manually sets the maximum claim count.
     */
    fun setMaxClaims(amount: Int) {
        require(amount in 0..maxSupportedClaims)
        totalClaims = amount
    }

    /**
     * Calculates the XP requirement for a given claim.
     */
    abstract fun claimXPSolver(claim: Int): Int

    /**
     * Gets the decoration used for rendering this meter.
     */
    abstract fun getDecoration(): HotbarDecoration

    /**
     * Draws a 5x5 vertical bar showing the current claim's progress.
     */
    protected fun renderVerticalProgress(
        graphics: GuiGraphicsExtractor, x0: Int, y0: Int, progress: Float
    ) {
        val f = progress.coerceIn(0f, 1f)

        fun color(i: Float): Int = if (f < i) 0x686969.opaqueColor()
        else 0xffc211.opaqueColor()

        graphics.fill(x0, y0, x0 + 5, y0 + 1, color(1.0f))
        graphics.fill(x0, y0 + 1, x0 + 5, y0 + 2, color(0.8f))
        graphics.fill(x0, y0 + 2, x0 + 5, y0 + 3, color(0.6f))
        graphics.fill(x0, y0 + 3, x0 + 5, y0 + 4, color(0.4f))
        graphics.fill(x0, y0 + 4, x0 + 5, y0 + 5, color(0.2f))
    }
}