/*
 * Originally based on code from Island Utils
 * Copyright (c) 2024, AsoDesu_
 * Licensed under the MIT License.
 *
 * - Additional changes by Pe3ep, 2026
 */
package cc.pe3epwithyou.trident.feature.doll.back

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.model.player.PlayerModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.entity.state.AvatarRenderState
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class BackRenderLayer(
    parent: RenderLayerParent<AvatarRenderState, PlayerModel>,
    ctx: EntityRendererProvider.Context
) : RenderLayer<AvatarRenderState, PlayerModel>(parent) {
    companion object {
        var playerForNextPass: LivingEntity? = null
        var itemForNextPass: ItemStack? = null
    }

    val itemRenderState = ItemStackRenderState()
    private val resolver = ctx.itemModelResolver

    override fun submit(
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        i: Int,
        entityRenderState: AvatarRenderState,
        f: Float,
        g: Float
    ) {
        val player = playerForNextPass ?: return
        playerForNextPass = null
        val item = itemForNextPass ?: return
        itemForNextPass = null

        val entityModel = this.parentModel
        entityModel.root().translateAndRotate(poseStack)
        entityModel.body.translateAndRotate(poseStack)
        poseStack.translate(0f, -2.1f, 0f)
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f))
        poseStack.scale(0.625f, -0.625f, -0.625f)
        resolver.updateForLiving(itemRenderState, item, ItemDisplayContext.HEAD, player)
        itemRenderState.submit(poseStack, submitNodeCollector, i, OverlayTexture.NO_OVERLAY, entityRenderState.outlineColor)
    }
}