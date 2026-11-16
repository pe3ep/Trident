package cc.pe3epwithyou.trident.utils.animation

data class CubicBezier(
    val x1: Float, val y1: Float, val x2: Float, val y2: Float
) {
    init {
        require(x1 in 0f..1f && x2 in 0f..1f) {
            "X Bezier points must be between 0 and 1."
        }
    }

    fun transform(progress: Float): Float {
        val x = progress.coerceIn(0f, 1f)

        fun coordinate(t: Float, p1: Float, p2: Float): Float {
            val inverse = 1f - t
            return 3f * inverse * inverse * t * p1 + 3f * inverse * t * t * p2 + t * t * t
        }

        var low = 0f
        var high = 1f

        repeat(16) {
            val middle = (low + high) * 0.5f

            if (coordinate(middle, x1, x2) < x) {
                low = middle
            } else {
                high = middle
            }
        }

        return coordinate((low + high) * 0.5f, y1, y2)
    }

    @Suppress("unused")
    companion object {
        val LINEAR = CubicBezier(0f, 0f, 1f, 1f)
        val EASE = CubicBezier(0.25f, 0.1f, 0.25f, 1f)
        val EASE_IN = CubicBezier(0.42f, 0f, 1f, 1f)
        val EASE_OUT = CubicBezier(0f, 0f, 0.58f, 1f)
        val EASE_IN_OUT = CubicBezier(0.42f, 0f, 0.58f, 1f)
        val SMOOTH = CubicBezier(.13f, .82f, 0.3f, 1f)
    }
}