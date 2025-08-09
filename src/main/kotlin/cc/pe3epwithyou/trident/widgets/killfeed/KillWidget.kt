package cc.pe3epwithyou.trident.widgets.killfeed

import cc.pe3epwithyou.trident.utils.NoxesiumUtils
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.widgets.IconWidget
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.LinearLayout
import com.noxcrew.sheeplib.layout.LinearLayoutBuilder
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation

class KillWidget(
    private val victim: String,
    private val killMethod: KillMethod,
    private val attacker: String? = null,
    private val killType: KillType
) : CompoundWidget(0, 0, 0, 0) {
    private companion object {
        private val ENEMY_COLOR: Int = 0xff0404 opacity 128
        private val TEAM_COLOR: Int = 0x00aaff opacity 128
        private val SELF_COLOR: Int = 0xefa80e opacity 128
    }

    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    override val layout: LinearLayout = LinearLayout(
        LinearLayout.Orientation.HORIZONTAL,
        0
    ) {
        val firstColor = when (killType) {
            KillType.ENEMY_SELF -> ENEMY_COLOR
            KillType.ENEMY_TEAM -> ENEMY_COLOR
            KillType.TEAM_ENEMY -> TEAM_COLOR
            KillType.SELF_ENEMY -> SELF_COLOR
        }

        val secondColor = when (killType) {
            KillType.ENEMY_SELF -> SELF_COLOR
            KillType.ENEMY_TEAM -> TEAM_COLOR
            KillType.TEAM_ENEMY -> ENEMY_COLOR
            KillType.SELF_ENEMY -> ENEMY_COLOR
        }

        if (attacker != null) {
            +KillBackground(firstColor, attacker, killMethod)
            +IconWidget(
                Texture(
                    killType.transitionSprite,
                    8,
                    15
                )
            )
        }
        +KillBackground(secondColor, victim, isLeft = false)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}

private class KillBackground(
    private val color: Int,
    private val player: String,
    private val killMethod: KillMethod? = null,
    private val isLeft: Boolean = true,
) : CompoundWidget(0, 0, 0, 0) {
    private companion object {
        private val ROUNDED_LEFT = ResourceLocation.fromNamespaceAndPath("trident", "interface/background/rounded_left")
        private val ROUNDED_RIGHT = ResourceLocation.fromNamespaceAndPath("trident", "interface/background/rounded_right")
    }

    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    override val layout: LinearLayout = LinearLayout(
        LinearLayout.Orientation.HORIZONTAL,
        0,
    ) {
        val client = Minecraft.getInstance()
        val mcFont = client.font
        val playerUUID = client.playerSocialManager.getDiscoveredUUID(player)
        val c = NoxesiumUtils.skullComponent(playerUUID)
            .append(Component.literal(" " + player.uppercase())
                .withStyle(Style.EMPTY
                    .withFont(TridentFont.getMCCFont())
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
//        graphics.fill(x, y, x + layout.width, y + layout.height, color)
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            if (isLeft) ROUNDED_LEFT else ROUNDED_RIGHT,
            x,
            y,
            layout.width,
            layout.height,
            color
        )
        super.renderWidget(graphics, i, j, f)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit
}

