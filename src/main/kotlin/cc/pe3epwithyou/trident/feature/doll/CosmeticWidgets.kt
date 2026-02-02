package cc.pe3epwithyou.trident.feature.doll

import cc.pe3epwithyou.trident.feature.doll.DollCosmetics.isWeaponSkin
import cc.pe3epwithyou.trident.utils.ItemRenderer
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import cc.pe3epwithyou.trident.utils.gridLayout
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.component.CustomModelData

class CosmeticWidgets(x: Int, y: Int) : CompoundWidget(x, y, 0, 0) {
    override val layout = gridLayout(2, x, y) {
        var c = 0
        DollCosmetics.currentCosmetics.forEach { (type, value) ->
            CosmeticItemWidget(type).at(0, c)
            c++
        }
    }

    override fun renderWidget(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val screen = Minecraft.getInstance().screen ?: return
        if (!Doll.shouldRender(screen)) return
        super.renderWidget(graphics, i, j, f)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)

        width = layout.width
        height = layout.height
    }

    override fun isFocused(): Boolean = false

    class CosmeticItemWidget(val type: DollCosmetics.CosmeticType) :
        AbstractWidget(0, 0, 18, 18, Component.empty()) {
        override fun renderWidget(
            guiGraphics: GuiGraphics,
            i: Int,
            j: Int,
            f: Float
        ) {
            guiGraphics.fillRoundedAll(x, y, 18, 18, 0x111111 opacity 128)
            val item = DollCosmetics.currentCosmetics[type]?.slot?.item?.copy() ?: return
            if (isWeaponSkin(item)) {
                DollCosmetics.currentChroma?.let {
                    item.set(
                        DataComponents.CUSTOM_MODEL_DATA, CustomModelData(
                            emptyList(),
                            emptyList(),
                            emptyList(),
                            it.colors,
                        )
                    )
                }
            }
            ItemRenderer(item, 16, 16).render(guiGraphics, x + 1, y + 1)
        }

        override fun isFocused(): Boolean = false

        override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit

    }
}