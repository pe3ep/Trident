package cc.pe3epwithyou.trident.feature.recording

import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.minecraft
import com.mojang.math.Transformation
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.ChatFormatting
import net.minecraft.util.Mth
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.Team
import org.joml.Vector3f
import kotlin.math.min

object PlaybackManager {
    fun getTeam(): PlayerTeam? {
        minecraft().level?.let {
            val team = PlayerTeam(
                it.scoreboard,
                "dojo_ghost_team"
            )

            team.collisionRule = Team.CollisionRule.NEVER
            team.color = ChatFormatting.WHITE

            return team
        }
        return null
    }

    fun register() {
        ClientTickEvents.END_WORLD_TICK.register {
            val recording = RecordingManager.currentRecording ?: return@register
            if (recording.state != RecordingState.PLAYBACK) return@register
            val ghost = recording.ghostRef ?: return@register
            if (recording.currentFrame >= recording.frames.size) {
                stopPlaying()
                return@register
            }
            val currentFrame = recording.currentFrame
            val frame = recording.frames[currentFrame]
            // Set old values for interpolation
            ghost.setOldPosAndRot()
            ghost.yHeadRotO = ghost.yHeadRot
            ghost.yBodyRotO = ghost.yBodyRot

            // Update with the new frame
            ghost.setPos(frame.x, frame.y, frame.z)
            ghost.yRot = frame.yRot
            ghost.xRot = frame.xRot
            ghost.yHeadRot = frame.yHeadRot
            ghost.yBodyRot = frame.yBodyRot
            ghost.pose = frame.pose
            val f = Mth.length(
                ghost.x - ghost.xo,
                0.0,
                ghost.z - ghost.zo
            ).toFloat()
            val g = min(f * 4f, 1.0f)
            ghost.isSprinting = f > 0.5f
            ghost.walkAnimation.update(g, 0.6f, 1.0f)

            ghost.firstPassenger?.let {
                ghost.positionRider(it)
            }
            recording.markers.forEach { marker ->
                marker.firstPassenger?.let { passenger ->
                    marker.positionRider(passenger)
                }
            }

            recording.currentFrame++
        }
    }

    fun startPlaying() {
        val recording = RecordingManager.currentRecording ?: return
        require(recording.frames.isNotEmpty()) { "No frames loaded" }
        recording.prepareGhost()
        recording.ghostRef?.let { ghost ->
            recording.currentFrame = 0
            val level = minecraft().level ?: return
            val initialFrame = recording.frames.first()
            level.addEntity(ghost)
            val markers = recording.markers
            var offset = 0.7f
            markers.forEachIndexed { index, ent ->
                ent.setPos(initialFrame.x, initialFrame.y, initialFrame.z)
                level.addEntity(ent)
                if (index == 0) {
                    ent.startRiding(ghost)
                } else {
                    markers.getOrNull(index - 1)?.let { prev ->
                        ent.setTransformation(
                            Transformation(
                                Vector3f(0.0f, offset, 0.0f),
                                null,
                                null,
                                null
                            )
                        )
                        offset += 0.3f
                        Logger.sendMessage("${ent.text?.string} started riding")
                        ent.startRiding(prev)
                    }
                    Logger.sendMessage("${ent.text?.string} started riding")
                }
            }

            ghost.setPos(initialFrame.x, initialFrame.y, initialFrame.z)
            ghost.yRot = initialFrame.yRot
            ghost.xRot = initialFrame.xRot
            ghost.yHeadRot = initialFrame.yHeadRot
            ghost.yBodyRot = initialFrame.yBodyRot
            ghost.pose = initialFrame.pose

            recording.state = RecordingState.PLAYBACK
            Logger.sendMessage("Started playing")
        }
    }

    fun stopPlaying() {
        val recording = RecordingManager.currentRecording ?: return
        recording.state = RecordingState.STOPPED
        recording.currentFrame = 0
        recording.removeGhost()
        Logger.sendMessage("Stopped playing")
    }

    fun unload() {
        val recording = RecordingManager.currentRecording ?: return
        recording.removeGhost()
        RecordingManager.currentRecording = null
    }
}