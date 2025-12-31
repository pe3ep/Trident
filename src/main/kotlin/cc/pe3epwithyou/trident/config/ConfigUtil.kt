package cc.pe3epwithyou.trident.config

import cc.pe3epwithyou.trident.utils.Logger
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption

object ConfigUtil {
    fun writeToConfig(path: Path, text: String): Boolean {
        var tmp: Path? = null
        try {
            Files.createDirectories(path.parent)
            // Write atomically: write to a temp file, then move/replace
            tmp = path.resolveSibling("${path.fileName}.tmp")
            Files.writeString(
                tmp,
                text,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            )
            FileChannel.open(tmp, StandardOpenOption.WRITE).use { ch -> ch.force(true) }

            try {
                Files.move(
                    tmp,
                    path,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
                )
            } catch (_: Exception) {
                // Fallback if atomic move is not supported
                Files.move(
                    tmp,
                    path,
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
            return true
        } catch (e: Exception) {
            Logger.error("Failed to write config file ${path.fileName}: ${e.message}")
            tmp?.let { try { Files.deleteIfExists(it) } catch (_: Exception) {} }
            return false
        }
    }

    fun readFromConfig(path: Path): String? {
        try {
            return Files.readString(path)
        } catch (e: Exception) {
            Logger.error("Failed to read config file ${path.fileName}: ${e.message}")
            return null
        }
    }
}