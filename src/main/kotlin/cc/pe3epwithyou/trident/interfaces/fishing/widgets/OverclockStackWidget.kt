package cc.pe3epwithyou.trident.interfaces.fishing.widgets

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.state.Overclock
import cc.pe3epwithyou.trident.state.fishing.OverclockTexture
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
        val levels = TridentClient.playerState.supplies.overclocks
        stableClocks.forEachIndexed { index, overclock ->
            val model = cc.pe3epwithyou.trident.utils.Model(
                overclock.texturePath,
                width,
                height,
            )
            val label = when (overclock) {
                OverclockTexture.STRONG_HOOK, OverclockTexture.WISE_HOOK, OverclockTexture.GLIMMERING_HOOK,
                OverclockTexture.GREEDY_HOOK, OverclockTexture.LUCKY_HOOK -> levelComponent(levels.stableLevels.hook)
                OverclockTexture.FISH_MAGNET, OverclockTexture.TREASURE_MAGNET, OverclockTexture.SPIRIT_MAGNET,
                OverclockTexture.PEARL_MAGNET, OverclockTexture.XP_MAGNET -> levelComponent(levels.stableLevels.magnet)
                OverclockTexture.GLITCHED_ROD, OverclockTexture.GRACEFUL_ROD, OverclockTexture.STABLE_ROD,
                OverclockTexture.BOOSTED_ROD, OverclockTexture.SPEEDY_ROD -> levelComponent(levels.stableLevels.rod)
                else -> null
            }
            +IconWithLabelWidget(
                model,
                label,
                marginRight = 2
            )
        }

        val overclockState = TridentClient.playerState.supplies.overclocks

//        Get the needed overclock texture
        var unstableTexture = OverclockTexture.COOLDOWN
        when {
            overclockState.unstable.isCooldown -> unstableTexture = OverclockTexture.COOLDOWN
            overclockState.unstable.isActive -> unstableTexture =
                overclockState.unstable.texture ?: OverclockTexture.COOLDOWN

            !overclockState.unstable.isActive && !overclockState.unstable.isCooldown -> unstableTexture =
                OverclockTexture.ACTIVATED
        }
        var supremeTexture = OverclockTexture.SUPREME
        when {
            overclockState.supreme.isCooldown -> supremeTexture = OverclockTexture.COOLDOWN
            overclockState.supreme.isActive -> supremeTexture = OverclockTexture.SUPREME
            !overclockState.supreme.isActive && !overclockState.supreme.isCooldown -> supremeTexture =
                OverclockTexture.ACTIVATED
        }

        if (overclockState.unstable.isAvailable) +UnstableOverclockWidget(
            width,
            height,
            unstableTexture,
            2,
            getOverclockComponent(overclockState.unstable, overclockState.unstable.level),
            levelLabel = levelComponent(overclockState.unstable.level)
        )
        if (overclockState.supreme.isAvailable) +UnstableOverclockWidget(
            height,
            height,
            supremeTexture,
            0,
            getOverclockComponent(overclockState.supreme, overclockState.supreme.level)
        )
    }

    private fun levelComponent(level: Int?): Component? {
        if (level == null) return null
        return Component.literal("$level").mccFont(offset = 1)
    }

    private fun getOverclockComponent(state: Overclock, level: Int?): Component {
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
            component = Component.literal("READY").mccFont()
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