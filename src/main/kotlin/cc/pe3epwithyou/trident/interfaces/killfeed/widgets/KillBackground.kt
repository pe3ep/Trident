package cc.pe3epwithyou.trident.interfaces.killfeed.widgets

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.killfeed.KillMethod
import cc.pe3epwithyou.trident.utils.NoxesiumUtils
import cc.pe3epwithyou.trident.utils.TridentColor
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withTridentFont
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedLeft
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedRight
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.LinearLayout
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class KillBackground(
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
                .withTridentFont()
            StringWidget(c, mcFont).add(LayoutSettings.defaults().apply {
                padding(4, 3, if (isLeft) 2 else 4, 3)
            })
            return@LinearLayout
        }
        val playerUUID = client.playerSocialManager.getDiscoveredUUID(player!!)
        val c = NoxesiumUtils.skullComponent(playerUUID)
            .append(
                Component.literal((if (isSelf && Config.KillFeed.showYouInKill) " (YOU) " else " ") + player.uppercase())
                    .mccFont()
                    .withStyle(
                        Style.EMPTY
                            .withColor(TridentColor(0xFFFFFF).textColor)
                    )
            )
        if (killMethod != null) {
            c.append(
                Component.literal(" ${killMethod.icon}")
                    .withTridentFont()
            )
        }
        StringWidget(c, mcFont).add(LayoutSettings.defaults().apply {
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