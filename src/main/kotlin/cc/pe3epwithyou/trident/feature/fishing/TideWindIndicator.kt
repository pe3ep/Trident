package cc.pe3epwithyou.trident.feature.fishing

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.Slot

object TideWindIndicator {
    fun render(graphics: GuiGraphics, slot: Slot) {
        if (!Config.Fishing.islandIndicators) return
        val client = Minecraft.getInstance()
        val screen = client.screen ?: return
        if ("FISHING ISLANDS" !in screen.title.string) return
        val item = slot.item
        item.getLore().forEach { l ->
            if ("Active Tide: " in l.string) {
                val tideString = l.string.split(": ")[1]
                if (tideString == "None") return
                val tide = Tide.valueOf(tideString.uppercase())
                renderTide(graphics, slot, tide)
                return
            }
        }
    }

    private fun renderWind(graphics: GuiGraphics, slot: Slot) {

    }

    private fun renderTide(graphics: GuiGraphics, slot: Slot, tide: Tide) {
        Texture(
            tide.path,
            8,
            8,
            16,
            16
        ).blit(
            graphics,
            slot.x + 8,
            slot.y + 8
        )
    }

    private enum class Tide(
        val path: ResourceLocation
    ) {
        STRONG(
            Resources.mcc("textures/_fonts/fishing/tide_strong.png"),
        ),
        GLIMMERING(
            Resources.mcc("textures/_fonts/fishing/tide_glimmering.png"),
        ),
        GREEDY(
            Resources.mcc("textures/_fonts/fishing/tide_greedy.png"),
        ),
        LUCKY(
            Resources.mcc("textures/_fonts/fishing/tide_lucky.png"),
        ),
        WISE(
            Resources.mcc("textures/_fonts/fishing/tide_wise.png"),
        );
    }

    private enum class Winds(
        val texture: ResourceLocation
    ) {
        STRONG(
            Resources.mcc("textures/_fonts/fishing/winds_strong.png"),
        ),
        GLIMMERING(
            Resources.mcc("textures/_fonts/fishing/winds_glimmering.png"),
        ),
        GREEDY(
            Resources.mcc("textures/_fonts/fishing/winds_greedy.png"),
        ),
        LUCKY(
            Resources.mcc("textures/_fonts/fishing/winds_lucky.png"),
        ),
        WISE(
            Resources.mcc("textures/_fonts/fishing/winds_wise.png"),
        );
    }
}