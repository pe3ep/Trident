package cc.pe3epwithyou.trident.feature.doll

import cc.pe3epwithyou.trident.mixin.AbstractContainerScreenAccessor
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.playMaster
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.inventory.Slot
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.atan
import kotlin.math.min
import kotlin.math.pow

object Doll {


    var dollYRot = Y_CENTER
    var dollXRot = X_DEFAULT

    @JvmStatic
    fun resetDoll() {
        dollXRot = X_DEFAULT
        dollYRot = Y_CENTER
    }

    private const val X_DEFAULT = -10f
    private const val Y_MIN = 5f
    private const val Y_MAX = 360f - Y_MIN
    private const val Y_CENTER = 150f

    var dragStartX = -1
    var dragStartY = -1

    @JvmStatic
    fun onClick(mouseButtonEvent: MouseButtonEvent) {
        dragStartX = mouseButtonEvent.x.toInt()
        dragStartY = mouseButtonEvent.y.toInt()
        val client = Minecraft.getInstance()
        val screen = client.screen as? ContainerScreen ?: return
        val item = screen.hoveredSlot?.item ?: return
        // middle click
        if (mouseButtonEvent.button() == 2) {
            val type = DollCosmetics.findCosmeticType(item) ?: return
            val lockedSlot = DollCosmetics.lockedSlots[type]
            client.soundManager.playMaster(Resources.mcc("ui.click_normal"))
            if (lockedSlot?.slot?.item == item) {
                DollCosmetics.lockedSlots.remove(type)
            } else {
                DollCosmetics.lockedSlots[type] = DollCosmetics.Cosmetic(type.slot(item))
            }
            if (type == DollCosmetics.CosmeticType.WEAPON_SKIN) {
                // TODO: Add chromas
            }
        }
    }

    @JvmStatic
    fun onReleased() {
        dragStartX = -1
        dragStartY = -1
    }

    @JvmStatic
    fun rotateDoll(draggedX: Float, draggedY: Float) {
        if (!MCCIState.isOnIsland()) return

        val screen = Minecraft.getInstance().screen as? ContainerScreen ?: return
        val accessed = screen as AbstractContainerScreenAccessor
        val x1 = accessed.leftPos + 4

        val rectangle = ScreenRectangle(0, 0, x1, screen.height)
        if (dragStartX == -1 || dragStartY == -1) return
        if (!rectangle.containsPoint(dragStartX, dragStartY)) return

        dollXRot -= draggedX

        val current = dollYRot

        // Small soft zone so resistance appears close to the hard limit.
        val softZone = 60f
        val curveExp = 0.5f

        val distToLower = (current - Y_MIN).coerceAtLeast(0f)
        val distToUpper = (Y_MAX - current).coerceAtLeast(0f)
        val distToNearest = min(distToLower, distToUpper)

        val delta = -draggedY

        val nearestIsLower = distToLower <= distToUpper
        val movingTowardNearest = if (nearestIsLower) delta < 0f else delta > 0f

        if (distToNearest >= softZone || !movingTowardNearest) {
            dollYRot += delta
        } else {
            val normalized = ((softZone - distToNearest) / softZone).coerceIn(0f, 1f)

            val reduction = normalized.pow(curveExp)
            val scale = 1f - reduction

            dollYRot += delta * scale
        }

        dollYRot = dollYRot.coerceIn(Y_MIN, Y_MAX)
    }

    @JvmStatic
    fun render(graphics: GuiGraphics) {
        if (!MCCIState.isOnIsland()) return

        val screen = Minecraft.getInstance().screen as? ContainerScreen ?: return
        // If there's at least 1 item that can be previewed, we show the doll
        screen.menu.items.find { DollCosmetics.validItem(it) } ?: return

        val player = Minecraft.getInstance().player ?: return
        val accessed = screen as AbstractContainerScreenAccessor
        screen.hoveredSlot?.item?.let(DollCosmetics::setCosmetic)

        val size = accessed.imageHeight.toFloat() / 2.75f
        val x0 = accessed.leftPos - 250
        val x1 = accessed.leftPos + 4

        DollCosmetics.currentCosmetics.forEach { (_, v) -> v.slot.push(player) }

        renderDoll(
            graphics,
            x0,
            0,
            x1,
            screen.height,
            size,
            player
        )

//        graphics.drawString(screen.font, "$dollYRot", x0, accessed.leftPos, 0xffffff opacity 255)

        DollCosmetics.currentCosmetics.forEach { (_, v) -> v.slot.pop(player) }
    }



    @JvmStatic
    fun modifyTooltip(original: MutableList<Component>): List<Component> {
        val screen = Minecraft.getInstance().screen as? ContainerScreen ?: return original
        val item = screen.hoveredSlot?.item ?: return original
        if (!DollCosmetics.validItem(item)) return original

        val component =
            FontCollection.get("_fonts/icon/click_action_middle.png", 7, 7).withColor(0xffffff)
                .mccFont("icon")
                .append(Component.literal(" > ").withStyle(ChatFormatting.DARK_GRAY).defaultFont())
                .append(
                    Component.literal("Middle-Click to ")
                        .withColor(0xe9d282)
                        .defaultFont()
                )
                .append(
                    Component.literal("Toggle Preview")
                        .withColor(0xfbe460)
                        .defaultFont()
                )


        original.add(component)
        return original
    }

    fun renderDoll(
        guiGraphics: GuiGraphics,
        x0: Int,
        y0: Int,
        x1: Int,
        y1: Int,
        size: Float,
        livingEntity: LivingEntity,
    ) {
        val yRot = dollYRot
        val xRot = atan(dollXRot / 40.0f)

        val f = 0.0625f
        val livingEntityScale = livingEntity.scale

        val quaternion = Quaternionf().rotateZ(Math.PI.toFloat())
        val quaternion2 = Quaternionf().rotateX(xRot * 20.0f * (Math.PI.toFloat() / 180.0f))
        quaternion.mul(quaternion2)
        val vector3f =
            Vector3f(0.0f, livingEntity.bbHeight / 2.0f + f * livingEntityScale, 0.0f)
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

    private val SELECTED_TEXTURE = Texture(
        Resources.trident("textures/interface/selected.png"), 20, 22
    )

    @JvmStatic
    fun renderSlot(graphics: GuiGraphics, slot: Slot) {
        if (!DollCosmetics.validItem(slot.item)) return
        val type = DollCosmetics.findCosmeticType(slot.item) ?: return
        if (DollCosmetics.lockedSlots[type]?.slot?.item == slot.item) {
            SELECTED_TEXTURE.blit(graphics, slot.x - 2, slot.y - 4)
        }
    }



    @JvmStatic
    fun onClose() {
        resetDoll()
        DollCosmetics.resetCosmetics()
        DollCosmetics.setShownCosmetics()
    }



}