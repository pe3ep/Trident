package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withTridentFont
import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import com.noxcrew.sheeplib.layout.GridLayout
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component


object Introduction {
    fun displayIntroductionIfNeeded(original: Operation<Void>) {
        if (Config.handler.instance().sawIntroduction) {
            original.call()
            return
        }
        Minecraft.getInstance().setScreen(IntroductionScreen(original))
    }

    fun finishIntroduction(original: Operation<Void>) {
        original.call()
        Config.handler.instance().sawIntroduction = true
        Config.handler.save()
    }

    class IntroductionScreen(val original: Operation<Void>) : Screen(TITLE_TEXT) {
        companion object {
            private val TITLE_TEXT = Component.literal("\uE000").withTridentFont("glyph")
                .append(Component.literal(" Trident").defaultFont())
        }

        private val layout = HeaderAndFooterLayout(this, 61, 33)

        override fun init() {
            val linearLayout: LinearLayout =
                this.layout.addToHeader(LinearLayout.vertical().spacing(8))

            // title
            linearLayout.addChild(
                StringWidget(
                    TITLE_TEXT, this.font
                )
            ) { obj: LayoutSettings -> obj.alignHorizontallyCenter() }

            val key = Component.literal("[").withSwatch(
                TridentFont.TRIDENT_ACCENT
            )
                .append(Component.keybind("key.trident.config"))
                .append(Component.literal("]"))

            val intro = Component.translatable(
                "trident.introduction", key
            )


            val gridWidget = GridLayout(16) {
                MultiLineTextWidget(
                    intro, this@IntroductionScreen.font
                ).setMaxWidth(400).setCentered(true).atBottom(0)
                Button.builder(
                    Component.literal("Open Trident's Config")
                ) { _ ->
                    Minecraft.getInstance().setScreen(Config.getScreen(this@IntroductionScreen))
                }.width(200).build().atBottom(0)
            }

            this.layout.addToContents(gridWidget)

            val footer = GridLayout(2) {
                Button.builder(
                    CommonComponents.GUI_CANCEL
                ) { _ -> this@IntroductionScreen.onClose() }.width(200).build().at(0, 0)

                Button.builder(
                    CommonComponents.GUI_CONTINUE
                ) { _ ->
                    finishIntroduction(original)
                }.width(200).build().at(0, 1)
            }

            this.layout.addToFooter(footer)

            this.layout.arrangeElements()
            this.layout.visitWidgets(this::addRenderableWidget)
        }
    }
}