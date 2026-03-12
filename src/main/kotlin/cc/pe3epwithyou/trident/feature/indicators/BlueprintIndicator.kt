package cc.pe3epwithyou.trident.feature.indicators

import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.world.inventory.Slot
import kotlin.jvm.optionals.getOrNull

object BlueprintIndicator {
    @JvmStatic
    fun checkItem(graphics: GuiGraphics, slot: Slot) {
        if (minecraft().screen !is ContainerScreen) return
        checkLore(graphics, slot)
    }

    private fun checkLore(graphics: GuiGraphics, slot: Slot) {
        if (slot.item.isEmpty) return
        val tag = slot.item.components.get(DataComponents.CUSTOM_DATA)?.copyTag() ?: return
        parseTag(tag)?.let {
            if (!it.ownership) {
                renderIcon(graphics, slot, Icons.NEW_COSMETIC)
                return
            }

            if (it.donationsCurrent >= it.donationsLimit) {
                renderIcon(graphics, slot, Icons.MAXED_COSMETIC)
            }
        }
    }

    private fun renderIcon(graphics: GuiGraphics, slot: Slot, icon: Icons) {
        val x = slot.x
        val y = slot.y
        val texture = Texture(
            icon.texturePath,
            10,
            10,
        )
        texture.blit(graphics, x + 8, y - 2)
    }

    private enum class Icons(val texturePath: Identifier) {
        NEW_COSMETIC(
            Resources.trident("textures/interface/blueprint_indicators/new.png")
        ),
        MAXED_COSMETIC(
            Resources.trident("textures/interface/blueprint_indicators/maxed.png")
        )
    }

    private fun parseTag(tag: CompoundTag): BlueprintData? {
        tag.getCompound("PublicBukkitValues")?.getOrNull()?.let { pbv ->
            pbv.getCompound("mcc_island:blueprint")?.getOrNull()?.let {
                return BlueprintData(
                    ownership = it.getBoolean("mcc_island:ownership").getOrNull() ?: return null,
                    donationsCurrent = it.getInt("mcc_island:donations_current").getOrNull() ?: return null,
                    donationsLimit = it.getInt("mcc_island:donations_limit").getOrNull() ?: return null
                )
            }
        }
        return null
    }

    data class BlueprintData(
        val ownership: Boolean,
        val donationsCurrent: Int,
        val donationsLimit: Int
    )
}