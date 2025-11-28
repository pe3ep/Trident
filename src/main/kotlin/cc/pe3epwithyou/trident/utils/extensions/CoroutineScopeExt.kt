package cc.pe3epwithyou.trident.utils.extensions

import kotlinx.coroutines.CoroutineScope
import net.minecraft.client.Minecraft

object CoroutineScopeExt {
    fun CoroutineScope.main(
        block: () -> Unit
    ) {
        Minecraft.getInstance().execute(block)
    }
}