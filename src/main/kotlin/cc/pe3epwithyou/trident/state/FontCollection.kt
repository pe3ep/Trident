package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.ConcurrentHashMap

object FontCollection {
    val collection = ConcurrentHashMap<Icon, String>()

    fun get(path: String) = get(path, 7, 8)

    fun get(path: String, ascent: Int, height: Int): MutableComponent {
        val loc = Resources.mcc(path)
        val icon = Icon(loc, ascent, height)
        return get(icon)
    }

    fun get(icon: Icon): MutableComponent {
        val char = collection[icon]
        if (char == null) {
            ChatUtils.error("Failed to get a char ${icon.path} from the font collection")
            return Component.literal("?").defaultFont()
        }
        val comp = Component.literal(char).mccFont("icon")
        return comp
    }

    fun clear() {
        collection.clear()
    }

    fun loadDefinition(location: ResourceLocation, char: String, ascent: Int, height: Int) {
        ChatUtils.debugLog("Received a character definition: ${location.path} $char $ascent $height")
        val i = Icon(location, ascent, height)
        collection[i] = char
    }

    data class Icon(
        val path: ResourceLocation, val ascent: Int, val height: Int
    )
}