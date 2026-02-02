package cc.pe3epwithyou.trident.utils

import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.sounds.SoundManager
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundSource

fun SoundManager.playMaster(sound: Identifier, volume: Float = 1.0f, pitch: Float = 1.0f) {
    play(SimpleSoundInstance(
        sound,
        SoundSource.MASTER,
        volume, pitch,
        SoundInstance.createUnseededRandom(),
        false,
        0,
        SoundInstance.Attenuation.NONE,
        0.0,
        0.0,
        0.0,
        true
    ))
}