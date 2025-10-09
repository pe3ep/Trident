package cc.pe3epwithyou.trident.interfaces

import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption

object DialogIO {
    // configDir/trident/dialog_positions.json
    private val path: Path = FabricLoader.getInstance()
        .configDir
        .resolve("trident")
        .resolve("dialog_positions.json")

    private val json = Json { prettyPrint = true }

    fun save(positions: Map<String, TridentDialog.Position>) {

        // Ensure the trident directory exists
        Files.createDirectories(path.parent)

        val text = json.encodeToString(positions)

        // Write atomically: write to temp file then move/replace
        val tmp = path.resolveSibling("${path.fileName}.tmp")
        Files.writeString(
            tmp,
            text,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
        )
        Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
    }

    fun load(): Map<String, TridentDialog.Position> {
        if (!Files.exists(path)) return emptyMap()

        val text = Files.readString(path)
        val serializable: Map<String, TridentDialog.Position> = json.decodeFromString(text)
        return serializable
    }
}