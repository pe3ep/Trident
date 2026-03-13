package cc.pe3epwithyou.trident.feature.dojo

import cc.pe3epwithyou.trident.feature.dojo.RecordingManager.Frame
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.minecraft
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.PropertyMap
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.ChatFormatting
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.Team
import java.util.*

object PlaybackManager {
    var loadedFrames: List<Frame> = emptyList()
    var currentFrame: Int = 0
    var isPlaying: Boolean = false
    private var ghostRef: Player? = null

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
            if (!isPlaying) return@register
            val ghost = ghostRef ?: return@register
            if (currentFrame >= loadedFrames.size) {
                stopPlaying()
                return@register
            }
            val frame = loadedFrames[currentFrame]
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
            ghost.calculateEntityAnimation(false)
            currentFrame++
        }
    }



    private fun createGhostEntity() {
        val level = minecraft().level ?: return
        val original = minecraft().gameProfile
        val props = PropertyMap(original.properties)
        val profile = GameProfile(UUID.randomUUID(), original.name, props)

        val ghost = GhostPlayer(level, profile)
        ghost.noPhysics = true
        ghost.isNoGravity = true
        ghost.isInvulnerable = true
        ghost.isCustomNameVisible = false
        ghostRef = ghost


        Logger.sendMessage("Created ghost entity")
    }

    private fun removeGhostEntity() {
        ghostRef?.let {
            val level = minecraft().level ?: return
            level.removeEntity(it.id, Entity.RemovalReason.DISCARDED)
        }
        ghostRef = null
    }

    fun startPlaying() {
        require(loadedFrames.isNotEmpty()) { "No frames loaded" }
        createGhostEntity()
        ghostRef?.let {
            currentFrame = 0
            val level = minecraft().level ?: return
            val initialFrame = loadedFrames[0]
            level.addEntity(it)
            it.setPos(initialFrame.x, initialFrame.y, initialFrame.z)
            it.yRot = initialFrame.yRot
            it.xRot = initialFrame.xRot
            it.yHeadRot = initialFrame.yHeadRot
            it.yBodyRot = initialFrame.yBodyRot
            it.pose = initialFrame.pose

            isPlaying = true
            Logger.sendMessage("Started playing")
        }
    }

    fun stopPlaying() {
        removeGhostEntity()
        isPlaying = false
        currentFrame = 0
        Logger.sendMessage("Stopped playing")
    }

    fun unload() {
        removeGhostEntity()
        loadedFrames = emptyList()
    }
}