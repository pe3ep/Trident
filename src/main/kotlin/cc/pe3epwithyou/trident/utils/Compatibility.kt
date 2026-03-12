@file:Suppress("SameParameterValue")

package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.config.Config
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import java.util.*

fun utilsCompatible(): Boolean = check("islandutils")

private fun container(id: String): Optional<ModContainer> = FabricLoader.getInstance().getModContainer(id)

private fun check(id: String): Boolean {
    val modContainer = container(id)
    if (Config.Debug.forceIncompatibility) return false
    if (Config.Debug.forceCompatibility) return true
    return !modContainer.isPresent
}