package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.events.container.ContainerContext
import cc.pe3epwithyou.trident.events.container.withContainerCtx
import cc.pe3epwithyou.trident.interfaces.fishing.widgets.AugmentWidget
import cc.pe3epwithyou.trident.state.AugmentContainer
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.state.fishing.AugmentStatus
import cc.pe3epwithyou.trident.state.fishing.getAugmentContainer
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.findInLore
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import cc.pe3epwithyou.trident.utils.minecraft
import cc.pe3epwithyou.trident.utils.parseFormattedInt
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.inventory.Slot

object AugmentStatusInterface {
    private val AUGMENT_SLOTS = listOf(30, 31, 32, 33, 34, 39, 40, 41, 42, 43)

    @JvmStatic
    fun render(graphics: GuiGraphics, slot: Slot) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Fishing.showAugmentStatusInInterface) return
        withContainerCtx(minecraft().screen as? ContainerScreen ?: return) {
            supplies(this, graphics, slot)
            crabPots(this, graphics, slot)
        }
    }

    private fun crabPots(ctx: ContainerContext, graphics: GuiGraphics, slot: Slot) = with(ctx) {
        if (!titleContains("CRAB POTS")) return@with
        slot.item.findInLore(Regex("""Durability: (\d+)/(\d+)"""))?.let {
            val durability = it.groups[1]?.value?.parseFormattedInt() ?: return@let
            val total = it.groups[2]?.value?.parseFormattedInt() ?: return@let
            if (durability / total.toDouble() <= 0.15) {
                Texture(
                    AugmentWidget.REPAIR_AUGMENT, 16, 16, 12, 12
                ).blit(graphics, slot.x, slot.y)
            }
        }
    }

    private fun supplies(ctx: ContainerContext, graphics: GuiGraphics, slot: Slot) = with(ctx) {
        if (!titleContains("FISHING SUPPLIES")) return@with
        if (slot.index !in AUGMENT_SLOTS) return@with
        val x = slot.x
        val y = slot.y
        val container = getContainer(slot) ?: return@with
        when (container.status) {
            AugmentStatus.NEEDS_REPAIRING -> {
                Texture(
                    AugmentWidget.REPAIR_AUGMENT, 16, 16, 12, 12
                ).blit(graphics, x, y)
            }

            AugmentStatus.BROKEN -> {
                Texture(
                    AugmentWidget.BROKEN_AUGMENT, 16, 16, 12, 12
                ).blit(graphics, x, y)
            }

            else -> {}
        }

        if (container.paused) {
            Texture(
                AugmentWidget.PAUSED_AUGMENT, 16, 16, 12, 12
            ).blit(graphics, x, y)
        }
    }

    private fun getContainer(slot: Slot): AugmentContainer? {
        val itemStack = slot.item
        val cleanedName = itemStack.hoverName.string.replace(
            Regex("""(A\.N\.G\.L\.R\.|\[|]|Augment)"""), ""
        ).trim()
        val container =
            getAugmentContainer(cleanedName, itemStack.getLore().map { it.string }) ?: return null
        return container
    }
}