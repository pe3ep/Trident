package cc.pe3epwithyou.trident.utils

import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.network.chat.TextColor

class TridentColor(
    val color: Int
) {
    val red: Int = (color shr 16) and 0xFF
    val green: Int = (color shr 8) and 0xFF
    val blue: Int = color and 0xFF
    val textColor: TextColor
        get() = TextColor.fromRgb(color)
    val opaqueColor: Int
        get() = color.opaqueColor()

    fun opacity(v: Int): Int = color.opacity(v)

    fun lighten(f: Float): TridentColor {
        val t = f.coerceIn(0f, 1f)
        val r = (red + (255 - red) * t).toInt()
        val g = (green + (255 - green) * t).toInt()
        val b = (blue + (255 - blue) * t).toInt()
        return TridentColor((r shl 16) or (g shl 8) or b)
    }

    fun darken(f: Float): TridentColor {
        val t = f.coerceIn(0f, 1f)
        val r = (red * (1 - t)).toInt()
        val g = (green * (1 - t)).toInt()
        val b = (blue * (1 - t)).toInt()
        return TridentColor((r shl 16) or (g shl 8) or b)
    }

    companion object {
        /**
         * Linearly interpolates between two colors.
         * @param from Starting color
         * @param to Ending color
         * @param t Interpolation factor between 0.0 and 1.0
         * @return Interpolated color as a TridentColor instance
         */
        fun lerp(from: TridentColor, to: TridentColor, t: Float): TridentColor {
            val r = (from.red + (to.red - from.red) * t).toInt().coerceIn(0, 255)
            val g = (from.green + (to.green - from.green) * t).toInt().coerceIn(0, 255)
            val b = (from.blue + (to.blue - from.blue) * t).toInt().coerceIn(0, 255)
            val colorInt = (r shl 16) or (g shl 8) or b
            return TridentColor(colorInt)
        }

        /**
         * Linearly interpolates across a list of colors.
         * @param colors List of colors to interpolate between
         * @param t Interpolation factor between 0.0 and 1.0
         * @return Interpolated color as a TridentColor instance
         */
        fun lerpList(colors: List<TridentColor>, t: Float): TridentColor {
            require(colors.isNotEmpty()) { "Color list must not be empty" }
            if (colors.size == 1) return colors[0]

            val clampedT = t.coerceIn(0f, 1f)
            val scaledT = clampedT * (colors.size - 1)
            val index = scaledT.toInt()
            val localT = scaledT - index

            val from = colors[index]
            val to = if (index + 1 < colors.size) colors[index + 1] else colors.last()

            return lerp(from, to, localT)
        }
    }
}