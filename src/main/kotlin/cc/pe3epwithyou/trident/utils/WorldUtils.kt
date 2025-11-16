package cc.pe3epwithyou.trident.utils

import net.minecraft.client.Minecraft

object WorldUtils {
    fun getGameID(): String = checkNotNull(Minecraft.getInstance().level)
        .dimension()
        .location()
        .path
}