package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.feature.dmlock.ReplyLock
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
        clearCache()
    }

    fun loadDefinition(location: Identifier, char: String, ascent: Int, height: Int) {
        val i = Icon(location, ascent, height)
        collection[i] = char
        populateCache(i)
    }

    data class Icon(
        val path: Identifier, val ascent: Int, val height: Int
    )

    fun texture(path: String): MutableComponent = texture(Resources.mcc(path))

    fun texture(resource: Identifier): MutableComponent {
        if (!Identifier.isValidPath(resource.path)) return Component.literal("?").defaultFont()
        return Component.`object`(AtlasSprite(AtlasSprite.DEFAULT_ATLAS, resource))
    }

    fun texture(resource: Identifier, atlas: Identifier): MutableComponent {
        if (!Identifier.isValidPath(resource.path)) return Component.literal("?").defaultFont()
        return Component.`object`(AtlasSprite(atlas, resource))
    }

    fun populateCache(icon: Icon) {
        if ("_fonts/icon/xp_bonus" in icon.path.path) {
            val char = collection[icon] ?: return
            ReplyLock.Icon.xpBonusCharCache.add(char)
        }
    }

    fun clearCache() {
        ReplyLock.Icon.xpBonusCharCache.clear()
    }
}