package cc.pe3epwithyou.trident.interfaces

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption

@Serializable
private data class Position(val x: Int, val y: Int)

object DialogIO {
    // configDir/trident/dialog_positions.json
    private val path: Path = FabricLoader.getInstance()
        .configDir
        .resolve("trident")
        .resolve("dialog_positions.json")

    private val json = Json { prettyPrint = true }

    fun save(positions: Map<String, Pair<Int, Int>>) {
        val serializable = positions.mapValues { (_, pair) -> Position(pair.first, pair.second) }

        // Ensure the trident directory exists
        Files.createDirectories(path.parent)

        val text = json.encodeToString(serializable)

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

    fun load(): Map<String, Pair<Int, Int>> {
        if (!Files.exists(path)) return emptyMap()

        val text = Files.readString(path)
        val serializable: Map<String, Position> = json.decodeFromString(text)
        return serializable.mapValues { (_, pos) -> Pair(pos.x, pos.y) }
    }
}