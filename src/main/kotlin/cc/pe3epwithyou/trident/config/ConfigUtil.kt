package cc.pe3epwithyou.trident.config

import cc.pe3epwithyou.trident.utils.Logger
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption

object ConfigUtil {
    fun writeToConfig(path: Path, text: String): Boolean {
        try {
            Files.createDirectories(path.parent)
            // Write atomically: write to a temp file, then move/replace
            val tmp = path.resolveSibling("${path.fileName}.tmp")
            Files.writeString(
                tmp,
                text,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            )
            Files.move(
                tmp,
                path,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            )
            return true
        } catch (e: Exception) {
            Logger.error("Failed to write config file ${path.fileName}: ${e.message}")
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