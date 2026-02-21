package cc.pe3epwithyou.trident.feature.doll

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.events.click.ClickEvents
import cc.pe3epwithyou.trident.events.click.ContainerClickContext
import cc.pe3epwithyou.trident.events.container.ContainerContext
import cc.pe3epwithyou.trident.events.container.ContainerEvents
import cc.pe3epwithyou.trident.events.container.withContainerCtx
import cc.pe3epwithyou.trident.feature.doll.chroma.ChromaWidgets
import cc.pe3epwithyou.trident.mixin.accessors.AbstractContainerScreenAccessor
import cc.pe3epwithyou.trident.mixin.accessors.InventoryScreenAccessor
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.*
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
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

    fun onClick(ctx: ContainerClickContext) = with(ctx) {
        if (!Config.Global.cosmeticPreview) return@with
        if (screen.getChildAt(x, y).getOrNull() != null) return@with
        dragStartX = x.toInt()
        dragStartY = y.toInt()
        val item = clickedItem() ?: return@with

        if (middle) {
            val type = DollCosmetics.findCosmeticType(item) ?: return@with
            val lockedSlot = DollCosmetics.lockedSlots[type]
            minecraft().soundManager.playMaster(Resources.mcc("ui.click_normal"))
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

        val screen = minecraft().screen as? ContainerScreen ?: return
        withContainerCtx(screen) {
            val x1 = leftPos() + 4
            val rectangle = ScreenRectangle(0, 0, x1, screenHeight())
            if (dragStartX == -1 || dragStartY == -1) return@withContainerCtx
            if (!rectangle.containsPoint(dragStartX, dragStartY)) return@withContainerCtx

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
    }

    fun getTopPos(ctx: ContainerContext): Int {
        return max(ctx.topPos() - 64, 4)
    }

    fun getBottomPos(ctx: ContainerContext, widgetHeight: Int): Int {
        return min(ctx.topPos() + ctx.imageHeight(), screenHeight() - widgetHeight - 4)
    }

    fun getLeftPos(ctx: ContainerContext): Int {
        return max(ctx.leftPos() - 250, 0)
    }

    fun addWidgets(ctx: ContainerContext) = with(ctx) {
        if (!MCCIState.isOnIsland()) return@with
        if (!Config.Global.cosmeticPreview) return@with

        val x0 = getLeftPos(this)
        val x1 = leftPos() + 6
        val center = leftPos() - ((x1 - x0) / 2)
        val widget = CosmeticWidgets(2, getTopPos(this))
        widget.x = center - widget.width / 2
        addRenderable(widget)

        val chromas = ChromaWidgets(2, 0)
        chromas.x = center - chromas.width / 2
        chromas.y = getBottomPos(this, chromas.height)
        addRenderable(chromas)
    }

    fun shouldRender(screen: Screen): Boolean {
        if (MCCIState.game != Game.HUB && MCCIState.game != Game.FISHING) return false
        return (screen as? ContainerScreen ?: return false).menu.items.find {
            DollCosmetics.validItem(
                it
            )
        } != null
    }

    fun register() {
        ContainerEvents.onOpen {
            addWidgets(this)
        }
        ContainerEvents.onClose {
            resetDoll()
            DollCosmetics.resetCosmetics()
        }
        ClickEvents.onClick(::onClick)
    }

    @JvmStatic
    fun render(graphics: GuiGraphics) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.cosmeticPreview) return

        val screen = minecraft().screen as? ContainerScreen ?: return
        // If there's at least 1 item that can be previewed, we show the doll
        if (!shouldRender(screen)) return

        withContainerCtx(screen) {
            val player = minecraft().player ?: return@withContainerCtx
            hoveredItem()?.let(DollCosmetics::setCosmetic)
            wardrobePreview(this)

            val size = imageHeight().toFloat() / 2.75f
            val x0 = getLeftPos(this)
            val x1 = leftPos() + 6

            DollCosmetics.currentCosmetics.values.forEach { it.slot.push(player) }

            renderDoll(
                graphics, x0, 0, x1, screenHeight(), size, player
            )

            DollCosmetics.currentCosmetics.values.forEach { it.slot.pop(player) }
        }
    }

    fun wardrobePreview(ctx: ContainerContext) = with(ctx) {
        if (!titleContains("WARDROBE EDITOR")) return@with
        DollCosmetics.setCosmetic(item(29) ?: return@with)
    }

    @JvmStatic
    fun modifyTooltip(consumer: Consumer<Component>) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.cosmeticPreview) return
        val screen = minecraft().screen as? ContainerScreen ?: return
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

}