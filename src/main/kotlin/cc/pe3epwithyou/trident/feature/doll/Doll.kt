package cc.pe3epwithyou.trident.feature.doll

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.doll.chroma.ChromaWidgets
import cc.pe3epwithyou.trident.mixin.accessors.AbstractContainerScreenAccessor
import cc.pe3epwithyou.trident.mixin.accessors.InventoryScreenAccessor
import cc.pe3epwithyou.trident.mixin.accessors.ScreenAccessor
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
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.inventory.Slot
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.function.Consumer
import kotlin.jvm.optionals.getOrNull
import kotlin.math.atan
import kotlin.math.max
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
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.cosmeticPreview) return
        val client = Minecraft.getInstance()
        val screen = client.screen as? ContainerScreen ?: return
        if (screen.getChildAt(mouseButtonEvent.x, mouseButtonEvent.y).getOrNull() != null) return
        dragStartX = mouseButtonEvent.x.toInt()
        dragStartY = mouseButtonEvent.y.toInt()
        val item = (screen as AbstractContainerScreenAccessor).hoveredSlot?.item ?: return
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
        if (!Config.Global.cosmeticPreview) return

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

    fun getTopPos(screen: ContainerScreen): Int {
        val accessed = screen as AbstractContainerScreenAccessor
        return max(accessed.topPos - 64, 4)
    }

    fun getBottomPos(screen: ContainerScreen, widgetHeight: Int): Int {
        val accessed = screen as AbstractContainerScreenAccessor
        return min(accessed.topPos + accessed.imageHeight, screen.height - widgetHeight - 4)
    }

    fun getLeftPos(screen: ContainerScreen): Int {
        val accessed = screen as AbstractContainerScreenAccessor
        return max(accessed.leftPos - 250, 0)
    }

    fun addWidgets(screen: ContainerScreen) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.cosmeticPreview) return
        if (!shouldRender(screen)) return
        val accessed = screen as AbstractContainerScreenAccessor
        val x0 = getLeftPos(screen)
        val x1 = accessed.leftPos + 6
        val center = accessed.leftPos - ((x1 - x0) / 2)
        val widget = CosmeticWidgets(2, getTopPos(screen))
        widget.x = center - widget.width / 2
        (screen as ScreenAccessor).`trident$addRenderableWidget`(widget)

        val chromas = ChromaWidgets(2, 0)
        chromas.x = center - chromas.width / 2
        chromas.y = getBottomPos(screen, chromas.height)
        (screen as ScreenAccessor).`trident$addRenderableWidget`(chromas)
    }

    @JvmStatic
    fun shouldRender(screen: Screen): Boolean =
        (screen as ContainerScreen).menu.items.find { DollCosmetics.validItem(it) } != null

    @JvmStatic
    fun render(graphics: GuiGraphics) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.cosmeticPreview) return

        val screen = Minecraft.getInstance().screen as? ContainerScreen ?: return
        // If there's at least 1 item that can be previewed, we show the doll
        if (!shouldRender(screen)) return

        val player = Minecraft.getInstance().player ?: return
        val accessed = screen as AbstractContainerScreenAccessor
        screen.hoveredSlot?.item?.let(DollCosmetics::setCosmetic)
        wardrobePreview(screen)

        val size = accessed.imageHeight.toFloat() / 2.75f
        val x0 = getLeftPos(screen)
        val x1 = accessed.leftPos + 6

        DollCosmetics.currentCosmetics.forEach { (_, v) -> v.slot.push(player) }

        renderDoll(
            graphics, x0, 0, x1, screen.height, size, player
        )

//        graphics.drawString(screen.font, "$dollYRot", x0, accessed.topPos, 0xffffff.opaqueColor())
//        graphics.drawString(screen.font, "$dollXRot", x0, accessed.topPos + 10, 0xffffff.opaqueColor())
//        graphics.drawString(screen.font, "${DollCosmetics.currentCosmetics.mapValues { it.value.slot.item?.hoverName?.string }}", x0, accessed.topPos + accessed.imageHeight + 64, 0xffffff.opaqueColor())

        DollCosmetics.currentCosmetics.forEach { (_, v) -> v.slot.pop(player) }
    }

    fun wardrobePreview(screen: ContainerScreen) {
        if ("WARDROBE EDITOR" in screen.title.string) {
            val item = screen.menu.items.getOrNull(29) ?: return
            DollCosmetics.setCosmetic(item)
        }
    }

    @JvmStatic
    fun modifyTooltip(consumer: Consumer<Component>) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.cosmeticPreview) return
        val screen = Minecraft.getInstance().screen as? ContainerScreen ?: return
        val item = (screen as AbstractContainerScreenAccessor).hoveredSlot?.item ?: return
        if (!DollCosmetics.validItem(item)) return

        val component =
            FontCollection.get("_fonts/icon/click_action_middle.png", 7, 7).withColor(0xffffff)
                .mccFont("icon")
                .append(Component.literal(" > ").withStyle(ChatFormatting.DARK_GRAY).defaultFont())
                .append(
                    Component.literal("Middle-Click to ").withColor(0xe9d282).defaultFont()
                ).append(
                    Component.literal("Toggle Preview").withColor(0xfbe460).defaultFont()
                )

        consumer.accept(component)
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
        val vector3f = Vector3f(0.0f, livingEntity.bbHeight / 2.0f + f * livingEntityScale, 0.0f)

        val renderState = InventoryScreenAccessor.`trident$extractRenderState`(livingEntity)
        if (renderState is LivingEntityRenderState) {
            renderState.bodyRot = yRot
            renderState.yRot = 0f
            renderState.xRot = xRot * 20f
            renderState.boundingBoxWidth /= renderState.scale
            renderState.boundingBoxHeight /= renderState.scale
            renderState.scale = 1f
            renderState.walkAnimationSpeed = 0f
        }
        guiGraphics.submitEntityRenderState(
            renderState, size, vector3f, quaternion, quaternion2, x0, y0, x1, y1
        )
    }

    private val SELECTED_TEXTURE = Texture(
        Resources.trident("textures/interface/selected.png"), 20, 22
    )

    @JvmStatic
    fun renderSlot(graphics: GuiGraphics, slot: Slot) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.cosmeticPreview) return
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
    }

}