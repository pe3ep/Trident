package cc.pe3epwithyou.trident.utils

import net.minecraft.resources.Identifier

object Resources {
    fun trident(path: String): Identifier = Identifier.fromNamespaceAndPath("trident", path)
    fun mcc(path: String): Identifier = Identifier.fromNamespaceAndPath("mcc", path)
    fun minecraft(path: String): Identifier = Identifier.withDefaultNamespace(path)
}