package cc.pe3epwithyou.trident.utils.extensions

import net.minecraft.client.Minecraft

object CoroutineScopeExt {
    fun main(
        block: () -> Unit
    ) {
        Minecraft.getInstance().execute(block)
    }
}