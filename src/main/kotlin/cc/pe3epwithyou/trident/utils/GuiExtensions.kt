package cc.pe3epwithyou.trident.utils

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics


object GuiExtensions {
    inline fun GuiGraphics.pose(apply: PoseStack.() -> Unit) {
        pose().pushPose()
        apply(pose())
        pose().popPose()
    }

}