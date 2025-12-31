package cc.pe3epwithyou.trident.interfaces

import cc.pe3epwithyou.trident.config.ConfigUtil
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path

object DialogIO {
    // configDir/trident/dialog_positions.json
    private val path: Path = FabricLoader.getInstance()
        .configDir
        .resolve("trident")
        .resolve("dialog_positions.json")

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun save(positions: Map<String, TridentDialog.Position>) {
        val text = json.encodeToString(positions)
        ConfigUtil.writeToConfig(path, text)
    }

    fun load(): Map<String, TridentDialog.Position> {
        if (!Files.exists(path)) return emptyMap()

        val text = ConfigUtil.readFromConfig(path) ?: return emptyMap()
        val serializable: Map<String, TridentDialog.Position> = json.decodeFromString(text)
        return serializable
    }
}