package cc.pe3epwithyou.trident.interfaces.fishing.widgets

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.shared.widgets.ItemWidget
import cc.pe3epwithyou.trident.state.OverclockState
import cc.pe3epwithyou.trident.state.fishing.OverclockTexture
import cc.pe3epwithyou.trident.utils.Model
import cc.pe3epwithyou.trident.utils.TridentColor
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.LinearLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import java.util.concurrent.TimeUnit

class OverclockStackWidget(
    width: Int,
    height: Int,
    stableClocks: List<OverclockTexture>
) : CompoundWidget(0, 0, width, height) {

    override val layout: LinearLayout = LinearLayout(
        LinearLayout.Orientation.HORIZONTAL,
        0
    ) {
        stableClocks.forEachIndexed { index, overclock ->
            +ItemWidget(
                Model(
                    overclock.texturePath,
                    width,
                    height,
                ),
                marginRight = if (index == stableClocks.lastIndex) 3 else 0
            )
        }

        val overclockState = TridentClient.playerState.supplies.overclocks

//        Get the needed overclock texture
        var unstableTexture = OverclockTexture.COOLDOWN
        when {
            overclockState.unstable.state.isCooldown -> unstableTexture = OverclockTexture.COOLDOWN
            overclockState.unstable.state.isActive -> unstableTexture =
                overclockState.unstable.texture ?: OverclockTexture.COOLDOWN

            !overclockState.unstable.state.isActive && !overclockState.unstable.state.isCooldown -> unstableTexture =
                OverclockTexture.ACTIVATED
        }
        var supremeTexture = OverclockTexture.SUPREME
        when {
            overclockState.supreme.state.isCooldown -> supremeTexture = OverclockTexture.COOLDOWN
            overclockState.supreme.state.isActive -> supremeTexture = OverclockTexture.SUPREME
            !overclockState.supreme.state.isActive && !overclockState.supreme.state.isCooldown -> supremeTexture =
                OverclockTexture.ACTIVATED
        }

        if (overclockState.unstable.state.isAvailable) +UnstableOverclockWidget(
            width,
            height,
            unstableTexture,
            3,
            getOverclockComponent(overclockState.unstable.state)
        )
        if (overclockState.supreme.state.isAvailable) +UnstableOverclockWidget(
            height,
            height,
            supremeTexture,
            0,
            getOverclockComponent(overclockState.supreme.state)
        )
    }

    private fun getOverclockComponent(state: OverclockState): Component {
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

        val (colors, ratio, time) = if (state.isCooldown) {
            val ratio = state.cooldownLeft.toFloat() / state.cooldownDuration
            val time = convertTicks(state.cooldownLeft)
            Triple(cooldownColors, ratio, time)
        } else {
            val ratio = state.timeLeft.toFloat() / state.duration
            val time = convertTicks(state.timeLeft)
            Triple(normalColors, ratio, time)
        }

        val lerpedColor = TridentColor.lerpList(colors, 1F - ratio)
        var component = Component.literal(time)
            .mccFont(offset = 3)
            .withStyle(
                Style.EMPTY
                    .withColor(lerpedColor.textColor)

            )

        if (!state.isActive && !state.isCooldown) {
            component = Component.literal("READY")
                .mccFont(offset = 3)
                .withStyle(
                    Style.EMPTY
                        .withColor(TridentColor(0x1EFC00).textColor)
                )
        }
        return component
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