package cc.pe3epwithyou.trident.feature.meter.daily

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.utils.*
import cc.pe3epwithyou.trident.utils.animation.KeyframeAnimator
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.withTimeoutOrNull
import kotlinx.coroutines.withTimeoutOrNull
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.DeathScreen
import net.minecraft.network.chat.Component
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object DailyMeterXPAnimation {
    private val accumulatedXp = AtomicInteger(0)
    private val animation = KeyframeAnimator(50L)
    private val INTRO_FRAMES: List<Texture> = buildList {
        repeat(4) {
            add(
                Texture(
                    location = Resources.trident("textures/interface/meter/xp_animation/a$it.png"),
                    width = 48,
                    height = 112
                )
            )
        }
    }

    private val OUTRO_FRAMES: List<Texture> = buildList {
        repeat(10) {
            add(
                Texture(
                    location = Resources.trident("textures/interface/meter/xp_animation/b$it.png"),
                    width = 48,
                    height = 112
                )
            )
        }
    }


    fun trigger(xpReceived: Int) {
        accumulatedXp.addAndGet(xpReceived)

        debounce(this, 500L) {
            val totalXp = accumulatedXp.getAndSet(0)
            if (!Config.MeterTracker.Daily.animation || totalXp < 60 || DailyMeter.currentClaim >= DailyMeter.totalClaims) {
                DailyMeter.handleIncomingXP(totalXp)
                return@debounce
            }

            background().launch {
                withTimeoutOrNull(10.seconds) {
                    while (true) {
                        if (minecraft().gui.screen() is DeathScreen) continue

                        triggerFrames(totalXp)
                        break
                    }
                }
            }
        }
    }

    fun render(graphics: GuiGraphicsExtractor, x0: Int, y0: Int) =
        animation.render(graphics, x0, y0)

    private fun triggerFrames(xpReceived: Int) {
        animation.clear()
        val xOffset = DailyMeter.getIconXOffset() - 19
        val yOffset = 111 - 20

        val text = Component.literal("+${xpReceived}XP").mccFont().withoutShadow()
        val font = minecraft().font
        val textWidth = font.width(text)

        // Intro animation
        INTRO_FRAMES.forEach { introTexture ->
            animation.frame {
                draw { graphics, x0, y0, _ ->
                    introTexture.blit(graphics, x0 + xOffset, y0 - yOffset)

                    48
                }
            }
        }

        // Frames with XP text
        fun drawWhiteBoxFrame() {
            animation.frame(100) {
                draw { graphics, x0, y0, _ ->
                    val x = x0 + xOffset + 24 - ((textWidth + 3) / 2)
                    val y = y0 - yOffset + 45

                    graphics.fillRoundedAll(x, y, textWidth + 3, 9, 0xFFFFFF.opaqueColor())

                    48
                }
            }
        }

        drawWhiteBoxFrame()
        animation.frame(1500L) {
            onFrameStart {
                if (Config.MeterTracker.Daily.sound) {
                    minecraft().soundManager.playMaster(
                        sound = Resources.mcc("ui.ranking_points_gain"),
                        volume = 0.5f,
                    )
                }
            }
            draw { graphics, x0, y0, _ ->
                val x = x0 + xOffset + 24 - ((textWidth + 3) / 2)
                val y = y0 - yOffset + 45

                graphics.fillRoundedAll(x, y, textWidth + 3, 9, 0x000000 opacity 128)
                graphics.text(font, text, x + 2, y, 0xffc211.opaqueColor())

                48
            }
        }
        drawWhiteBoxFrame()

        // Outro animation
        OUTRO_FRAMES.forEachIndexed { index, outroTexture ->
            animation.frame(if (index == OUTRO_FRAMES.size - 1) 50 else 30) {
                if (index == OUTRO_FRAMES.size - 1) {
                    onFrameEnd {
                        if (Config.MeterTracker.Daily.sound) {
                            minecraft().soundManager.playMaster(
                                sound = Resources.mcc("ui.experience_receive"),
                                volume = 0.25f,
                                pitch = 1.25f,
                            )
                        }

                        DailyMeter.handleIncomingXP(xpReceived)
                        DailyMeter.animateExpand()
                    }
                }

                draw { graphics, x0, y0, _ ->
                    outroTexture.blit(graphics, x0 + xOffset, y0 - yOffset)

                    48
                }
            }
        }
    }
}
