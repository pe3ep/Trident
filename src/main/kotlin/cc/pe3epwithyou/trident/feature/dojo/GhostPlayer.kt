package cc.pe3epwithyou.trident.feature.dojo

import cc.pe3epwithyou.trident.utils.minecraft
import com.mojang.authlib.GameProfile
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.RemotePlayer
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.PlayerSkin
import net.minecraft.world.scores.PlayerTeam

class GhostPlayer(clientLevel: ClientLevel, gameProfile: GameProfile) : RemotePlayer(
    clientLevel, gameProfile
) {
    companion object {
        private const val DISTANCE_LONG = 7.0
        private const val DISTANCE_SHORT = 2.5
    }

    init {
        this.entityData.set(
            DATA_PLAYER_MODE_CUSTOMISATION,
            minecraft().player!!.entityData.get(DATA_PLAYER_MODE_CUSTOMISATION)
        )
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
        return minecraft().player?.skin ?: DefaultPlayerSkin.getDefaultSkin()
    }

    // If false, makes this entity translucent
    override fun isInvisibleTo(player: Player): Boolean {
        if (player == minecraft().player) {
            val delta = player.distanceTo(this)
            if (delta > DISTANCE_SHORT) {
                return false
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

}