package cc.pe3epwithyou.trident.utils

import net.minecraft.resources.ResourceLocation

object TridentFont {
    fun getMCCFont(font: String = "hud", offset: Int = 0): ResourceLocation {
        if (offset == 0) {
            return ResourceLocation.fromNamespaceAndPath("mcc", font)
        }
        return ResourceLocation.fromNamespaceAndPath("mcc", "${font}_offset_${offset}")
    }
    fun getTridentFont(font: String = "icon", offset: Int = 0): ResourceLocation {
        if (offset == 0) {
            return ResourceLocation.fromNamespaceAndPath("trident", font)
        }
        return ResourceLocation.fromNamespaceAndPath("trident", "${font}_offset_${offset}")
    }
}