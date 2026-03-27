package cc.pe3epwithyou.trident.feature.recording

import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.minecraft
import com.google.common.collect.ImmutableMultimap
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.RemotePlayer
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.PlayerSkin
import net.minecraft.world.phys.Vec3
import net.minecraft.world.scores.PlayerTeam
import kotlin.jvm.optionals.getOrNull

class GhostPlayer(val recording: Recording, clientLevel: ClientLevel, gameProfile: GameProfile) : RemotePlayer(
    clientLevel, gameProfile
) {
    companion object {
        private const val DISTANCE_LONG = 8.0
        private const val DISTANCE_SHORT = 3.5
    }

    var playerSkin: PlayerSkin = DefaultPlayerSkin.getDefaultSkin()

    init {
        this.entityData.set(
            DATA_PLAYER_MODE_CUSTOMISATION,
            minecraft().player!!.entityData.get(DATA_PLAYER_MODE_CUSTOMISATION)
        )

        recording.ownerSkin.let { skinData ->
            val propertyMap = PropertyMap(ImmutableMultimap.of<String, Property>(
                "textures", Property("textures", skinData)
            ))
            minecraft().skinManager.get(
                GameProfile(recording.ownerUUID, recording.ownerUsername, propertyMap),
            ).thenAccept { skin ->
                this.playerSkin = skin.getOrNull() ?: return@thenAccept
            }.exceptionally { t ->
                Logger.error("Failed to load skin for ghost player", t)
                null
            }
        }
    }

    override fun push(entity: Entity) {}
    override fun pushEntities() {}
    override fun hurtClient(damageSource: DamageSource): Boolean = false

    override fun shouldShowName(): Boolean = false
    override fun canBeCollidedWith(entity: Entity?): Boolean = false
    override fun isAttackable(): Boolean = false
    override fun getTeam(): PlayerTeam? {
        return PlaybackManager.getTeam()
    }

    override fun getSkin(): PlayerSkin {
        return playerSkin
    }

    // If false, makes this entity translucent
    override fun isInvisibleTo(player: Player): Boolean {
        if (player == minecraft().player) {
            val delta = player.distanceTo(this)
            if (delta > DISTANCE_SHORT) {
                recording.markers.forEach {
                    it.textOpacity = if (delta > DISTANCE_LONG) -1 else 127
                }
                return false
            }
            recording.markers.forEach {
                it.textOpacity = 0
            }
            return true
        }
        return super.isInvisibleTo(player)
    }

    override fun isInvisible(): Boolean {
        val player = minecraft().player ?: return false
        val delta = player.distanceTo(this)
        if (delta > DISTANCE_LONG) return false
        return true
    }

    override fun isCurrentlyGlowing(): Boolean {
        val player = minecraft().player ?: return false
        val delta = player.distanceTo(this)
        if (delta > DISTANCE_LONG) {
            return true
        }
        return false
    }

    override fun getPassengerRidingPosition(entity: Entity): Vec3 {
        return super.getPassengerRidingPosition(entity).add(0.0, 0.5, 0.0)
    }
}