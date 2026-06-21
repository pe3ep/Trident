package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.Slot
import kotlin.jvm.optionals.getOrNull

object EnhancedCompactInfinibag {
    fun formatNumber(value: Long): String {
        return when {
            value < 1_000 -> value.toString()
            value < 1_000_000 -> String.format("%.1fK", value / 1_000.0)
            value < 1_000_000_000 -> String.format("%.1fM", value / 1_000_000.0)
            else -> String.format("%.1fB", value / 1_000_000_000.0)
        }.replace(".0", "")
    }

    @JvmStatic
    fun render(graphics: GuiGraphicsExtractor, slot: Slot) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.enhancedCompactInfinibag) return
        val tag = slot.item.components.get(DataComponents.CUSTOM_DATA)?.copyTag() ?: return
        val amount = parseTag(tag) ?: return
        if (amount <= 99) return
        val formatted = Component.literal(formatNumber(amount)).withoutShadow()
        val font = minecraft().font
        val width = font.width(formatted)
        graphics.fill(slot.x, slot.y + 8, slot.x + 16, slot.y + 16, 0x000 opacity 192)
        graphics.pose().pushMatrix()

        val pose = graphics.pose()
        val factor = ((16f / width) * 0.9f).coerceAtMost(0.9f)
        pose.scaleAround(factor, factor, slot.x.toFloat(), slot.y.toFloat() + (8 * (2 - factor)), pose)
        graphics.text(font, formatted, slot.x + (16-((width * factor).toInt()))/2, slot.y + 9, 0xffffff.opaqueColor())

        graphics.pose().popMatrix()
    }

    private fun parseTag(tag: CompoundTag): Long? {
        tag.getCompound("PublicBukkitValues").getOrNull()?.let { pbv ->
            pbv.getInt("mcc:amount").getOrNull()?.let {
                return it.toLong()
            }
        }
        return null
    }
}