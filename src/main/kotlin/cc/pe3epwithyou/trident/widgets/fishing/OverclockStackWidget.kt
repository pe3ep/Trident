package cc.pe3epwithyou.trident.widgets.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.state.fishing.OverclockTexture
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.TridentColor
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.widgets.IconWidget
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.LinearLayout
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import java.util.concurrent.TimeUnit

class OverclockStackWidget(
    width: Int,
    height: Int,
    theme: Themed,
    stableClocks: List<OverclockTexture>
) : CompoundWidget(0, 0, width, height) {

    override val layout: LinearLayout = LinearLayout(
        LinearLayout.Orientation.HORIZONTAL,
        0
    ) {
        stableClocks.forEachIndexed { index, overclock ->
            +IconWidget(
                Texture(
                    overclock.texturePath,
                    width,
                    height,
                    overclock.textureWidth,
                    overclock.textureHeight
                ),
                marginRight = if (index == stableClocks.lastIndex) 3 else 0
            )
        }

        val overclockState = TridentClient.playerState.supplies.overclocks

        // Colors for unstable overclock (normal and cooldown)
        val normalColors = listOf(
            TridentColor(0xFFFFFF),
            TridentColor(0xFFFFFF),
            TridentColor(0xFFFFFF),
            TridentColor(0xFFFFFF),
            TridentColor(0xFFC700),
            TridentColor(0xE93C3C)
        )
        val cooldownColors = listOf(
            TridentColor(0xAAAAAA),
            TridentColor(0xAAAAAA),
            TridentColor(0xAAAAAA),
            TridentColor(0xAAAAAA),
            TridentColor(0xAAAAAA),
            TridentColor(0x1EFC00)
        )

        val (colors, ratio, time) = if (overclockState.unstable.isCooldown) {
            val ratio = overclockState.unstable.cooldownLeft.toFloat() / overclockState.unstable.cooldownDuration
            val time = convertTicks(overclockState.unstable.cooldownLeft)
            Triple(cooldownColors, ratio, time)
        } else {
            val ratio = overclockState.unstable.timeLeft.toFloat() / overclockState.unstable.duration
            val time = convertTicks(overclockState.unstable.timeLeft)
            Triple(normalColors, ratio, time)
        }

        val lerpedColor = TridentColor.lerpList(colors, 1F - ratio)
        var component = Component.literal(time).withStyle(
            Style.EMPTY
                .withColor(lerpedColor.textColor)
                .withFont(TridentFont.getMCCFont(offset = 3))
        )

        if (!overclockState.unstable.isActive && !overclockState.unstable.isCooldown) {
            component = Component.literal("READY").withStyle(
                Style.EMPTY
                    .withColor(TridentColor(0x1EFC00).textColor)
                    .withFont(TridentFont.getMCCFont(offset = 3))
            )
        }

        +UnstableOverclockWidget(width, height, OverclockTexture.TREASURE_MAGNET, 3, component)
        +UnstableOverclockWidget(height, height, OverclockTexture.BOOSTED_ROD, 0, component)
    }

    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    override fun mouseClicked(d: Double, e: Double, i: Int): Boolean = false

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }

    private fun convertTicks(ticks: Long): String {
        var seconds = ticks / 20
        val minutes = TimeUnit.SECONDS.toMinutes(seconds)
        seconds -= TimeUnit.MINUTES.toSeconds(minutes)

        return when {
            minutes >= 5 -> "${minutes}m"
            minutes > 0 -> {
                val secondsStr = if (seconds < 10) "0${seconds}s" else "${seconds}s"
                if (seconds == 0L) "${minutes}m" else "${minutes}m $secondsStr"
            }
            seconds < 10 -> "0${seconds}s"
            else -> "${seconds}s"
        }
    }
}