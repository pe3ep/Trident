package cc.pe3epwithyou.trident.feature.recording

import cc.pe3epwithyou.trident.feature.recording.RecordingManager.currentRecording
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.utils.*
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.offset
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.popped
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withTridentFont
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.PropertyMap
import com.noxcrew.sheeplib.util.opacity
import kotlinx.coroutines.launch
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.util.Brightness
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.*

class Recording {
    companion object {
        const val RECORDING_VERSION = 1
        val RECORDING_PATH: Path =
            FabricLoader.getInstance().configDir.resolve("trident").resolve("recordings")
    }
    var ownerUUID: UUID = UUID.randomUUID()
    var ownerUsername: String = ""
    var courseName: String = ""
    var ownerSkin: String = ""
    var isAnonymous: Boolean = false
    var medals: Int = 0
    var splits: Map<String, String> = emptyMap()

    var frames: List<RecordingManager.Frame> = emptyList()
    var currentFrame: Int = 0
    var state: RecordingState = RecordingState.STOPPED

    var ghostRef: GhostPlayer? = null

    val markers: MutableList<Display.TextDisplay> = mutableListOf()

    fun getFilename() = "$ownerUsername-$courseName.nbt"

    fun prepareGhost() {
        val level = minecraft().level ?: return
        val original = minecraft().gameProfile
        val props = PropertyMap(original.properties)
        val profile = GameProfile(UUID.randomUUID(), original.name, props)

        val ghost = GhostPlayer(this, level, profile)
        ghost.noPhysics = true
        ghost.isNoGravity = true
        ghost.isInvulnerable = true
        ghost.isCustomNameVisible = false

        addMarker(level, Component.literal("\uE017").withTridentFont(), false)
        addMarker(level, Component.literal("Pe3ep").defaultFont())

        val ending = FontCollection.get("_fonts/icon/parkour_warrior/completion_advanced_small.png").withoutShadow().offset(y = 1f).popped()
            .append(
                Component.literal(" ADVANCED").mccFont()
            )

        addMarker(level, ending)

        ghostRef = ghost
        Logger.sendMessage("Created ghost entity")
    }

    private fun addMarker(level: ClientLevel, component: Component, textShadow: Boolean = true): Display.TextDisplay {
        val display = Display.TextDisplay(EntityType.TEXT_DISPLAY, level)
        display.flags = (display.flags.toInt() or Display.TextDisplay.FLAG_SEE_THROUGH.toInt()).toByte()
        if (textShadow) display.flags = (display.flags.toInt() or Display.TextDisplay.FLAG_SHADOW.toInt()).toByte()
        display.text = component
        display.backgroundColor = 0x000000 opacity 0
        display.billboardConstraints = Display.BillboardConstraints.CENTER
        display.brightnessOverride = Brightness.FULL_BRIGHT
        markers += display
        return display
    }

    fun removeGhost() {
        val level = minecraft().level ?: return
        ghostRef?.let {
            level.removeEntity(it.id, Entity.RemovalReason.DISCARDED)
        }
        markers.forEach {
            level.removeEntity(it.id, Entity.RemovalReason.DISCARDED)
        }
        ghostRef = null
        markers.clear()
    }

    fun save() {
        Logger.sendMessage("Saving recording...")
        val now = System.currentTimeMillis()
        nonCriticalIO().launch {
            try {
                val recording = this@Recording

                val root = CompoundTag().apply {
                    put("version", RECORDING_VERSION)
                }

                val owner = CompoundTag().apply {
                    put("uuid", recording.ownerUUID.toString())
                    put("username", recording.ownerUsername)
                    put("skin", recording.ownerSkin)
                }
                root.put("owner", owner)

                val list = mutableListOf<Tag>()
                for (frame in recording.frames) {
                    val entry = CompoundTag().apply {
                        put("x", frame.x)
                        put("y", frame.y)
                        put("z", frame.z)
                        put("yRot", frame.yRot)
                        put("xRot", frame.xRot)
                        put("yHeadRot", frame.yHeadRot)
                        put("yBodyRot", frame.yBodyRot)
                        put("pose", frame.pose.name)
                        put("onGround", frame.onGround)
                    }
                    list.add(entry)
                }
                root.put("frames", list)

                val filePath = RECORDING_PATH.resolve(recording.getFilename())
                val tempFile = filePath.resolveSibling("${filePath.fileName}.tmp")
                Files.createDirectories(tempFile.parent)
                NbtIo.writeCompressed(root, tempFile)

                Files.move(
                    tempFile,
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
                )

                val end = System.currentTimeMillis()
                main {
                    Logger.sendMessage("Recording saved in ${end - now}ms")
                }
            } catch (e: Exception) {
                Logger.error("Failed to save recording", e)
            }
        }
    }

    fun stop(shouldSave: Boolean = true) {
        val recording = currentRecording ?: return
        recording.state = RecordingState.STOPPED
        Logger.sendMessage("Recording stopped")
        if (shouldSave) save()
    }
}

enum class RecordingState {
    RECORD, STOPPED, PLAYBACK
}