package cc.pe3epwithyou.trident.feature.doll

import cc.pe3epwithyou.trident.feature.doll.back.Back
import cc.pe3epwithyou.trident.feature.doll.back.BackRenderLayer
import cc.pe3epwithyou.trident.feature.doll.slots.AccessorySlot
import cc.pe3epwithyou.trident.feature.doll.slots.CosmeticSlot
import cc.pe3epwithyou.trident.state.MCCIState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.atan
import kotlin.math.ceil

object Doll {
    var currentCosmetics = listOf<Cosmetic>(
        Cosmetic(
            AccessorySlot(), CosmeticType.ACCESSORY, Component.literal("Test")
        )
    )

    @Suppress("unused")
    enum class CosmeticType(
        val pathPrefix: String
    ) {
        HAT("island_cosmetics/general/hat"),
        ACCESSORY("island_cosmetics/general/accessory"),
        CLOAK("island_cosmetics/general/back"),
        ROD("island_lobby/fishing/rods"),
        WEAPON_SKIN("island_cosmetics/weapon_skins")
    }

    data class Cosmetic(
        val slot: CosmeticSlot,
        val type: CosmeticType,
        val name: Component,
    )

    @JvmStatic
    fun render(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
        if (!MCCIState.isOnIsland()) return

        val screen = (Minecraft.getInstance().screen ?: return) as ContainerScreen
        val player = Minecraft.getInstance().player ?: return

        val item = screen.hoveredSlot?.item ?: ItemStack.EMPTY

        currentCosmetics.forEach { it.slot.push(player, item) }

        val size = ceil(166 / 2.5).toFloat()
        val bounds = 166
        val x: Int = (screen.width - 176) / 4
        val y: Int = (screen.height / 2)

        BackRenderLayer.itemForNextPass = Back.getPlayerBackItem(player) ?: return
        BackRenderLayer.playerForNextPass = player
        renderEntityInInventory(
            graphics,
            x - bounds,  // x0
            y - bounds,  // y0
            x + bounds,  // x1
            y + bounds,  // y1
            size,  // size
            player
        )

        currentCosmetics.forEach { it.slot.pop(player) }
    }

    fun renderEntityInInventory(
        guiGraphics: GuiGraphics,
        x0: Int,
        y0: Int,
        x1: Int,
        y1: Int,
        size: Float,
        livingEntity: LivingEntity,
        heightMultiplier: Float = 1.0f
    ) {
        val yRot = 160f
        val xRot = atan(0f / 40.0f)

        val f = 0.0625f
        val livingEntityScale = livingEntity.scale

        val quaternion = (Quaternionf()).rotateZ(Math.PI.toFloat())
        val quaternion2 = (Quaternionf()).rotateX(xRot * 20.0f * (Math.PI.toFloat() / 180.0f))
        quaternion.mul(quaternion2)
        val vector3f =
            Vector3f(0.0f, (livingEntity.bbHeight / 2.0f + f * livingEntityScale) * heightMultiplier, 0.0f)
        val renderState = InventoryScreen.extractRenderState(livingEntity)
        if (renderState is LivingEntityRenderState) {
            renderState.bodyRot = yRot
            renderState.yRot = 0f
            renderState.xRot = xRot * 20f
            renderState.boundingBoxWidth /= renderState.scale
            renderState.boundingBoxHeight /= renderState.scale
            renderState.scale = 1f
        }
        guiGraphics.submitEntityRenderState(
            renderState,
            size,
            vector3f,
            quaternion,
            quaternion2,
            x0,
            y0,
            x1,
            y1
        )
    }
}