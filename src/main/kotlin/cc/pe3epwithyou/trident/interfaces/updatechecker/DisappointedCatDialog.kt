package cc.pe3epwithyou.trident.interfaces.updatechecker

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.state.PlayerStateIO
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Theme
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.widget.ThemedButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class DisappointedCatDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    override fun layout(): GridLayout = grid {
        val font = Minecraft.getInstance().font
        CatImageWidget().atBottom(0, 2)
        MultiLineTextWidget(Component.literal("""
            please update Trident.
            this cat represents developer's emotions
            when people are using outdated versions
        """.trimIndent()), font).setCentered(true).atBottom(0, 2)
        val c = Component.literal("ok, ill update")
        val hater = Component.literal("no, i hate you and your cat")
        ThemedButton(
            message = c,
            theme = this@DisappointedCatDialog,
            width = font.width(c) + 8,
            clickHandler = {
                close()
            }
        ).at(2, 0)
        ThemedButton(
            message = hater,
            theme = this@DisappointedCatDialog,
            style = this@DisappointedCatDialog.theme.buttonStyles.negative,
            width = font.width(hater) + 8,
            clickHandler = {
                TridentClient.playerState.hatesUpdates = true
                PlayerStateIO.save()
                close()
            }
        ).at(2, 1)
    }

    private class CatImageWidget : AbstractWidget(0, 0, 201 ,110, Component.empty()) {
        override fun renderWidget(
            guiGraphics: GuiGraphics,
            i: Int,
            j: Int,
            f: Float
        ) {
            Texture(
                Resources.trident("textures/interface/grumpycat.png"),
                201,
                110,
                402,
                219
            ).blit(
                guiGraphics,
                x,
                y,
            )
        }

        override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput): Unit = Unit

    }
}