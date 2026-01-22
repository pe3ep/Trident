package cc.pe3epwithyou.trident.interfaces.fishing

import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.network.chat.Component

enum class WayfinderModuleDisplay : NameableEnum {
    FULL,
    COMPACT;

    override fun getDisplayName(): Component =
        Component.translatable("config.trident.fishing.wayfinder_module.display.${name.lowercase()}")
}
