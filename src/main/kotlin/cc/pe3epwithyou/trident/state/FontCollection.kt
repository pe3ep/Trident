package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.objects.AtlasSprite
import net.minecraft.resources.Identifier
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
            Logger.error("Failed to get a char ${icon.path} from the font collection")
            return Component.literal("?").defaultFont()
        }
        val comp = Component.literal(char).mccFont("icon")
        return comp
    }

    fun clear() {
        collection.clear()
    }

    fun loadDefinition(location: Identifier, char: String, ascent: Int, height: Int) {
        val i = Icon(location, ascent, height)
        collection[i] = char
    }

    data class Icon(
        val path: Identifier, val ascent: Int, val height: Int
    )

    fun texture(path: String): MutableComponent {
        val resource = Resources.mcc(path)
        if (!Identifier.isValidPath(resource.path)) return Component.literal("?").defaultFont()
        return Component.`object`(AtlasSprite(AtlasSprite.DEFAULT_ATLAS, resource))
    }
}