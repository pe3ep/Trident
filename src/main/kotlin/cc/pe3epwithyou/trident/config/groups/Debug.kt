package cc.pe3epwithyou.trident.config.groups

import cc.pe3epwithyou.trident.config.Config.Companion.handler
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.dsl.CategoryRegistrar
import dev.isxander.yacl3.dsl.binding
import dev.isxander.yacl3.dsl.tickBox
import net.minecraft.network.chat.Component

fun debugCategory(categoryRegistrar: CategoryRegistrar) {
    categoryRegistrar.register("debug") {
        name(Component.translatable("config.trident.debug"))

        rootOptions.register("logging") {
            name(Component.translatable("config.trident.debug.enable_logging.name"))
            description(OptionDescription.of(Component.translatable("config.trident.debug.enable_logging.description")))
            binding(handler.instance()::debugEnableLogging, false)
            controller(tickBox())
        }

        rootOptions.register("draw_slot_number") {
            name(Component.translatable("config.trident.debug.draw_slot_number.name"))
            description(OptionDescription.of(Component.translatable("config.trident.debug.draw_slot_number.description")))
            binding(handler.instance()::debugDrawSlotNumber, false)
            controller(tickBox())
        }

        rootOptions.register("bypass_on_island") {
            name(Component.translatable("config.trident.debug.bypass_on_island.name"))
            description(OptionDescription.of(Component.translatable("config.trident.debug.bypass_on_island.description")))
            binding(handler.instance()::debugBypassOnIsland, false)
            controller(tickBox())
        }

    }
}