package cc.pe3epwithyou.trident.feature.recording

import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.main
import cc.pe3epwithyou.trident.utils.minecraft
import cc.pe3epwithyou.trident.utils.nonCriticalIO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import net.minecraft.world.entity.Pose
import java.util.*
import kotlin.io.path.exists
import kotlin.jvm.optionals.getOrNull

object RecordingManager {
    var currentRecording: Recording? = null

    fun createAndStart(
        course: String,
    ): Recording? {
        val recording = Recording().apply {
            courseName = course
            ownerUUID = minecraft().gameProfile.id
            ownerUsername = minecraft().gameProfile.name
            ownerSkin = minecraft().gameProfile.properties().get("textures").first()?.value.toString()
        }
        if (recording.state == RecordingState.RECORD) {
            Logger.sendMessage("Already recording")
            return null
        }
        recording.state = RecordingState.RECORD
        Logger.sendMessage("Recording started")
        currentRecording = recording
        return recording
    }

    fun discardFrames() {
        val recording = currentRecording ?: return
        recording.frames = emptyList()
        Logger.sendMessage("Recording frames discarded")
    }

    fun loadRecording(
        fileName: String,
        onComplete: (Recording) -> Unit = {}
    ): Job {
        Logger.sendMessage("Loading recording...")
        val now = System.currentTimeMillis()
        return nonCriticalIO().launch {
            try {
                val file = Recording.RECORDING_PATH.resolve(fileName)
                require(file.exists()) { "Recording File doesn't exist " }

                val recInstance = Recording()
                val root = NbtIo.readCompressed(file, NbtAccounter.uncompressedQuota())
                val version = requireNotNull(root.getInt("version").getOrNull()) { "Recording version is null" }
                require(version == Recording.RECORDING_VERSION) { "Recording version is not supported" }
                val frames = requireNotNull(root.getList("frames").getOrNull())

                val owner =
                    requireNotNull(root.getCompound("owner").getOrNull()) { "Owner is null" }
                recInstance.ownerUsername = requireNotNull(owner.getString("username").getOrNull())
                recInstance.ownerSkin = requireNotNull(owner.getString("skin").getOrNull())
                recInstance.ownerUUID =
                    UUID.fromString(requireNotNull(owner.getString("uuid").getOrNull()))


                // Parse frames
                val parsedFrames = frames.map { frameTag ->
                    val frame = requireNotNull(frameTag.asCompound().getOrNull()) { "Frame $frameTag is null?" }
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

                    Frame(x, y, z, yRot, xRot, yHeadRot, yBodyRot, pose, onGround)
                }

                recInstance.frames = parsedFrames
                currentRecording = recInstance
                val end = System.currentTimeMillis()
                main {
                    onComplete(recInstance)
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
            val recording = currentRecording ?: return@register
            if (recording.state != RecordingState.RECORD) return@register
            val frame = Frame(
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
            recording.frames += frame
        }
    }

    data class Frame(
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