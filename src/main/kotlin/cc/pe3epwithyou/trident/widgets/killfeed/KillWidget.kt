package cc.pe3epwithyou.trident.widgets.killfeed

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.utils.GraphicsExtensions.fillRoundedAll
import cc.pe3epwithyou.trident.utils.GraphicsExtensions.fillRoundedLeft
import cc.pe3epwithyou.trident.utils.GraphicsExtensions.fillRoundedRight
import cc.pe3epwithyou.trident.utils.NoxesiumUtils
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.TridentColor
import cc.pe3epwithyou.trident.utils.TridentFont
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.LinearLayout
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation

class KillWidget(
    private val victim: String,
    private val killMethod: KillMethod,
    private val attacker: String? = null,
    private val killColors: Pair<Int, Int>,
    private val streak: Int = 0
) : CompoundWidget(0, 0, 0, 0) {
    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height
    override val layout: LinearLayout = LinearLayout(
        LinearLayout.Orientation.HORIZONTAL,
        0
    ) {
        val self = Minecraft.getInstance().player?.name?.string ?: "Unknown"

        val firstSelfColor = TridentColor(killColors.first).color opacity 192
        val secondSelfColor = TridentColor(killColors.second).color opacity 192
        val attackerColor = if (self == attacker) firstSelfColor else killColors.first
        val victimColor = if (self == victim) secondSelfColor else killColors.second

        if (attacker != null) {
            if (streak >= 2) {
                +KillStreak(attackerColor, streak)
            }
            +KillBackground(attackerColor, attacker, killMethod, isSelf = (self == attacker))
            +KillTransition(attackerColor, victimColor)
            +KillBackground(victimColor, victim, isLeft = false, isSelf = (self == victim))
        } else {
            +KillBackground(attackerColor, killMethod = killMethod)
            +KillTransition(attackerColor, victimColor)
            +KillBackground(victimColor, victim, isLeft = false, isSelf = (self == victim))
        }
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}

private class KillStreak(
    private val color: Int,
    private val streak: Int
) : AbstractWidget(0, 0, 15, 9, Component.empty()) {
    private fun getStreakTexture(): Texture {
        val coercedStreak = streak.coerceIn(1, 6)
        return Texture(
            ResourceLocation.fromNamespaceAndPath(
                "trident",
                "textures/interface/killfeed/streaks/streak$coercedStreak.png"
            ),
            13,
            9
        )
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        guiGraphics.fillRoundedAll(
            x,
            y + 6,
            13,
            9,
            color
        )
        getStreakTexture().blit(guiGraphics, x, y + 6)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit

}

private class KillTransition(
    private val leftColor: Int,
    private val rightColor: Int,
) : AbstractWidget(0, 0, 8, 15, Component.empty()) {
    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val leftPath = ResourceLocation.fromNamespaceAndPath("trident", "interface/killfeed/left")
        val rightPath = ResourceLocation.fromNamespaceAndPath("trident", "interface/killfeed/right")
        guiGraphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            leftPath,
            x,
            y,
            8,
            15,
            leftColor
        )
        guiGraphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            rightPath,
            x,
            y,
            8,
            15,
            rightColor
        )
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit

}

private class KillBackground(
    private val color: Int,
    private val player: String? = null,
    private val killMethod: KillMethod? = null,
    private val isLeft: Boolean = true,
    private val isSelf: Boolean = false
) : CompoundWidget(0, 0, 0, 0) {

    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    override val layout: LinearLayout = LinearLayout(
        LinearLayout.Orientation.HORIZONTAL,
        0,
    ) {
        val client = Minecraft.getInstance()
        val mcFont = client.font
        if (player == null && killMethod != null) {
            val c = Component.literal("${killMethod.icon}")
                .withStyle(Style.EMPTY
                    .withFont(TridentFont.getTridentFont())
                )
            StringWidget(c, mcFont).alignCenter().add(LayoutSettings.defaults().apply {
                padding(4, 3, if (isLeft) 2 else 4, 3)
            })
            return@LinearLayout
        }
        val playerUUID = client.playerSocialManager.getDiscoveredUUID(player!!)
        val c = NoxesiumUtils.skullComponent(playerUUID)
            .append(Component.literal((if (isSelf && Config.KillFeed.showYouInKill) " (YOU) " else " ") + player.uppercase())
                .withStyle(Style.EMPTY
                    .withFont(TridentFont.getMCCFont())
                    .withColor(TridentColor(0xFFFFFF).textColor)
                )
            )
        if (killMethod != null) {
            c.append(Component.literal(" ${killMethod.icon}")
                .withStyle(Style.EMPTY
                    .withFont(TridentFont.getTridentFont())
                )
            )
        }
        StringWidget(c, mcFont).alignCenter().add(LayoutSettings.defaults().apply {
            padding(4, 3, if (isLeft) 2 else 4, 3)
        })
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }

    override fun renderWidget(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
        if (isLeft) {
            graphics.fillRoundedLeft(
                x,
                y,
                layout.width,
                layout.height,
                color
            )
        } else {
            graphics.fillRoundedRight(
                x,
                y,
                layout.width,
                layout.height,
                color
            )

        }
        super.renderWidget(graphics, i, j, f)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit
}