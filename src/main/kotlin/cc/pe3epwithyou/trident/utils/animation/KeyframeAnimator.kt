package cc.pe3epwithyou.trident.utils.animation

import net.minecraft.client.gui.GuiGraphicsExtractor

@DslMarker
annotation class KeyframeAnimationDsl

@KeyframeAnimationDsl
class KeyframeAnimator(
    defaultDurationMs: Long = 50L, private val loop: Boolean = false
) {
    @KeyframeAnimationDsl
    class FrameBuilder internal constructor() {
        internal var onStart: (() -> Unit)? = null
        internal var onEnd: (() -> Unit)? = null

        internal var drawBlock: ((
            GuiGraphicsExtractor, Int, Int, Float
        ) -> Int)? = null

        fun onFrameStart(block: () -> Unit) {
            onStart = block
        }

        fun onFrameEnd(block: () -> Unit) {
            onEnd = block
        }

        fun draw(
            block: (
                graphics: GuiGraphicsExtractor, x0: Int, y0: Int, frameProgress: Float
            ) -> Int
        ) {
            drawBlock = block
        }
    }

    private data class Keyframe(
        val durationMs: Long, val onStart: (() -> Unit)?, val onEnd: (() -> Unit)?, val draw: (
            GuiGraphicsExtractor, Int, Int, Float
        ) -> Int
    )

    var defaultDurationMs = defaultDurationMs
        set(value) {
            require(value > 0L)
            field = value
        }

    private val frames = ArrayList<Keyframe>()

    private var startedAt: Long? = null
    private var totalDurationMs = 0L
    private var activeFrameIndex = -1

    init {
        require(defaultDurationMs > 0L)
    }

    fun frame(
        durationMs: Long = defaultDurationMs, block: FrameBuilder.() -> Unit
    ): KeyframeAnimator {
        require(durationMs > 0L)
        require(durationMs <= Long.MAX_VALUE - totalDurationMs)

        val builder = FrameBuilder().apply(block)

        val draw = requireNotNull(builder.drawBlock) {
            "Every keyframe must define a draw block."
        }

        frames.add(
            Keyframe(
                durationMs = durationMs,
                onStart = builder.onStart,
                onEnd = builder.onEnd,
                draw = draw
            )
        )

        totalDurationMs += durationMs

        return this
    }

    fun isAnimating() = frames.isNotEmpty()

    fun render(
        graphics: GuiGraphicsExtractor, x: Int, y: Int
    ): Int? {
        if (frames.isEmpty()) return null

        val now = System.nanoTime() / 1_000_000L
        val start = startedAt ?: now.also { startedAt = it }
        val elapsed = (now - start).coerceAtLeast(0L)

        if (!loop && elapsed >= totalDurationMs) {
            finishActiveFrame()
            clear()
            return null
        }

        val position = if (loop) {
            elapsed % totalDurationMs
        } else {
            elapsed
        }

        var frameStart = 0L

        for ((index, frame) in frames.withIndex()) {
            val frameEnd = frameStart + frame.durationMs

            if (position < frameEnd) {
                if (activeFrameIndex != index) {
                    finishActiveFrame()

                    activeFrameIndex = index
                    frame.onStart?.invoke()
                }

                val progress =
                    ((position - frameStart).toFloat() / frame.durationMs).coerceIn(0f, 1f)

                return frame.draw(
                    graphics, x, y, progress
                )
            }

            frameStart = frameEnd
        }

        return null
    }

    private fun finishActiveFrame() {
        val index = activeFrameIndex
        if (index == -1) return

        activeFrameIndex = -1

        frames.getOrNull(index)?.onEnd?.invoke()
    }

    fun clear() {
        frames.clear()
        startedAt = null
        totalDurationMs = 0L
        activeFrameIndex = -1
    }
}
