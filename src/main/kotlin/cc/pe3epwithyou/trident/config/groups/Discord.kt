package cc.pe3epwithyou.trident.config.groups

import dev.isxander.yacl3.dsl.CategoryRegistrar
import net.minecraft.network.chat.Component

fun discordCategory(categoryRegistrar: CategoryRegistrar) {
    categoryRegistrar.register("discord") {
        name(Component.translatable("config.trident.discord"))
    }
}