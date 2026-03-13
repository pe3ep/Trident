package cc.pe3epwithyou.trident.feature.dojo

import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.main
import cc.pe3epwithyou.trident.utils.nonCriticalIO
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import net.minecraft.world.entity.Pose
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.jvm.optionals.getOrNull

object RecordingManager {
    private const val RECORDING_VERSION = 1
    private val path: Path =
        FabricLoader.getInstance().configDir.resolve("trident").resolve("recordings")

    var isRecording = false
    var recordedFrames: List<Frame> = emptyList()

    fun startRecording() {
        if (isRecording) {
            Logger.sendMessage("Already recording")
            return
        }
        isRecording = true
        Logger.sendMessage("Recording started")
    }

    fun saveRecording() {
        Logger.sendMessage("Saving recording...")
        val now = System.currentTimeMillis()
        nonCriticalIO().launch {
            try {
                val root = CompoundTag().apply {
                    putInt("version", RECORDING_VERSION)
                    putLong("startTick", recordedFrames.first().tick)
                }

                val list = ListTag()
                for (frame in recordedFrames) {
                    val entry = CompoundTag().apply {
                        putLong("tick", frame.tick)
                        putDouble("x", frame.x)
                        putDouble("y", frame.y)
                        putDouble("z", frame.z)
                        putFloat("yRot", frame.yRot)
                        putFloat("xRot", frame.xRot)
                        putFloat("yHeadRot", frame.yHeadRot)
                        putFloat("yBodyRot", frame.yBodyRot)
                        putString("pose", frame.pose.name)
                        putBoolean("onGround", frame.onGround)
                    }
                    list.add(entry)
                }
                root.put("frames", list)

                val file = path.resolve("recording.nbt")
                Files.createDirectories(file.parent)
                NbtIo.writeCompressed(root, file)
                val end = System.currentTimeMillis()
                main {
                    Logger.sendMessage("Recording saved in ${end - now}ms")
                }
                discardRecording()
            } catch (e: Exception) {
                Logger.error("Failed to save recording", e)
            }
        }
    }

    fun stopRecording(shouldSave: Boolean = true) {
        isRecording = false
        Logger.sendMessage("Recording stopped")
        if (shouldSave) saveRecording()
    }

    fun discardRecording() {
        recordedFrames = emptyList()
        Logger.sendMessage("Recording discarded")
    }

    fun loadRecording() {
        Logger.sendMessage("Loading recording...")
        val now = System.currentTimeMillis()
        nonCriticalIO().launch {
            try {
                val file = path.resolve("recording.nbt")
                require(file.exists()) { "Recording File doesn't exist " }

                val root = NbtIo.readCompressed(file, NbtAccounter.defaultQuota())
                val version = requireNotNull(root.getInt("version").getOrNull()) { "Recording version is null" }
                require(version == RECORDING_VERSION) { "Recording version is not supported" }
                val frames = requireNotNull(root.getList("frames").getOrNull())

                // Parse frames
                val parsedFrames = frames.map { frameTag ->
                    val frame = requireNotNull(frameTag.asCompound().getOrNull()) { "Frame $frameTag is null?" }
                    val tick = requireNotNull(frame.getLong("tick").getOrNull())
                    val x = requireNotNull(frame.getDouble("x").getOrNull())
                    val y = requireNotNull(frame.getDouble("y").getOrNull())
                    val z = requireNotNull(frame.getDouble("z").getOrNull())
                    val yRot = requireNotNull(frame.getFloat("yRot").getOrNull())
                    val xRot = requireNotNull(frame.getFloat("xRot").getOrNull())
                    val yHeadRot = requireNotNull(frame.getFloat("yHeadRot").getOrNull())
                    val yBodyRot = requireNotNull(frame.getFloat("yBodyRot").getOrNull())
                    val poseString = requireNotNull(frame.getString("pose").getOrNull())
                    val onGround = requireNotNull(frame.getBoolean("onGround").getOrNull())

                    val pose = Pose.valueOf(poseString)

                    Frame(tick, x, y, z, yRot, xRot, yHeadRot, yBodyRot, pose, onGround)
                }

                PlaybackManager.loadedFrames = parsedFrames
                val end = System.currentTimeMillis()
                main {
                    Logger.sendMessage("Recording loaded in ${end - now}ms")
                    Logger.sendMessage("${parsedFrames.size} frames loaded")
                }
            } catch (e: Exception) {
                Logger.error("Failed to load recording", e)
            }
        }
    }


    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            val player = client.player ?: return@register
            if (!isRecording) return@register
            val frame = Frame(
                tick = client.level!!.gameTime,
                x = player.x,
                y = player.y,
                z = player.z,
                yRot = player.yRot,
                xRot = player.xRot,
                yHeadRot = player.yHeadRot,
                yBodyRot = player.yBodyRot,
                pose = player.pose,
                onGround = player.onGround(),
            )
            recordedFrames += frame
        }
    }

    data class Frame(
        val tick: Long,
        val x: Double,
        val y: Double,
        val z: Double,
        val yRot: Float,
        val xRot: Float,
        val yHeadRot: Float,
        val yBodyRot: Float,
        val pose: Pose,
        val onGround: Boolean
    )
}