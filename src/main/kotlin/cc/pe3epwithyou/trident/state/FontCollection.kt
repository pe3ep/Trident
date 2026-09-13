package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.feature.chat.dmlock.ReplyLock
import cc.pe3epwithyou.trident.feature.disguise.Disguise
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.minecraft
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

    private val fallbackComponent = Component.literal("?").defaultFont()

    fun get(icon: Icon): MutableComponent {
        if (!isGameRunning()) return fallbackComponent
        val char = collection[icon]
        if (char == null) {
            Logger.error("Failed to get a char ${icon.path} from the font collection")
            return fallbackComponent
        }
        return Component.literal(char).mccFont("icon")
    }

    fun clear() {
        try {
            collection.clear()
            clearCache()
        } catch (t: Throwable) {
            Logger.error("Failed to clear the font collection: ${t.message}")
        }
    }

    fun loadDefinition(location: Identifier, char: String, ascent: Int, height: Int) {
        if (!isGameRunning()) return
        minecraft().execute {
            /*
             * Sometimes when force-quitting the game using ⌘Q (or Alt+F4), the process would
             * crash since it's trying to access memory that has been unloaded. We check both
             * before scheduling this task and again right before touching anything, since the
             * game can finish shutting down in the gap between the two.
             */
            try {
                if (!isGameRunning()) return@execute
                val i = Icon(location, ascent, height)
                collection[i] = char
                populateCache(i)
            } catch (t: Throwable) {
                Logger.error("Failed to load font definition $location: ${t.message}")
            }
        }
    }

    fun isGameRunning(): Boolean = try {
        minecraft().isRunning
    } catch (_: Throwable) {
        false
    }

    data class Icon(
        val path: Identifier, val ascent: Int, val height: Int
    )

    fun texture(path: String): MutableComponent = texture(Resources.mcc(path))

    fun texture(resource: Identifier, atlas: Identifier = AtlasSprite.DEFAULT_ATLAS): MutableComponent {
        if (!isGameRunning()) return fallbackComponent
        if (!Identifier.isValidPath(resource.path)) return fallbackComponent
        return Component.`object`(AtlasSprite(atlas, resource))
    }

    fun populateCache(icon: Icon) {
        if ("_fonts/icon/xp_bonus" in icon.path.path) {
            val char = collection[icon] ?: return
            ReplyLock.Icon.xpBonusCharCache.add(char)
        }
        if ("_fonts/icon/chat_channel/disguised" in icon.path.path) {
            val char = collection[icon] ?: return
            Disguise.disguiseIconCache = char
        }
    }

    fun clearCache() {
        ReplyLock.Icon.xpBonusCharCache.clear()
        Disguise.disguiseIconCache = null
    }
}