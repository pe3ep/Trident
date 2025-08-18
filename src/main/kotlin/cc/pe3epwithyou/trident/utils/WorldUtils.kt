package cc.pe3epwithyou.trident.utils

import net.minecraft.client.Minecraft
import java.util.*

object WorldUtils {
    fun getGameID(): UUID = checkNotNull(Minecraft.getInstance().level)
        .dimension()
        .location()
        .path
        .removePrefix("temp_world_")
        .let(UUID::fromString)
}