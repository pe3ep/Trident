package cc.pe3epwithyou.trident.interfaces.fishing

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.interfaces.fishing.widgets.AugmentWidget
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.state.fishing.AugmentStatus
import cc.pe3epwithyou.trident.state.fishing.getAugmentContainer
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.inventory.Slot

object AugmentStatusInterface {
    private val AUGMENT_SLOTS = listOf(30, 31, 32, 33, 34, 39, 40, 41, 42, 43)

    fun render(graphics: GuiGraphics, slot: Slot) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Fishing.showAugmentStatusInInterface) return
        if (slot.index !in AUGMENT_SLOTS) return
        val title = Minecraft.getInstance().screen?.title?.string ?: return
        if ("FISHING SUPPLIES" !in title) return

        val x = slot.x
        val y = slot.y
        val status = getStatus(slot) ?: return
        when (status) {
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

            AugmentStatus.PAUSED -> {
                Texture(
                    AugmentWidget.PAUSED_AUGMENT, 16, 16, 12, 12
                ).blit(graphics, x, y)
            }

            else -> {}
        }
    }

    private fun getStatus(slot: Slot): AugmentStatus? {
        val itemStack = slot.item
        val cleanedName = itemStack.hoverName.string.replace(
            Regex("""(A\.N\.G\.L\.R\.|\[|]|Augment)"""), ""
        ).trim()
        val container =
            getAugmentContainer(cleanedName, itemStack.getLore().map { it.string }) ?: return null
        return container.status
    }
}