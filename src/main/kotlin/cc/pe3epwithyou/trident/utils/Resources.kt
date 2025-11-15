package cc.pe3epwithyou.trident.utils

import net.minecraft.resources.ResourceLocation

object Resources {
    fun trident(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath("trident", path)
    fun mcc(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath("mcc", path)
    fun minecraft(path: String): ResourceLocation = ResourceLocation.withDefaultNamespace(path)
}