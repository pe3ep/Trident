package cc.pe3epwithyou.trident.config.groups

import cc.pe3epwithyou.trident.config.Config.Companion.handler
import cc.pe3epwithyou.trident.utils.utilsCompatible
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionEventListener
import dev.isxander.yacl3.dsl.CategoryRegistrar
import dev.isxander.yacl3.dsl.available
import dev.isxander.yacl3.dsl.binding
import dev.isxander.yacl3.dsl.tickBox
import net.minecraft.network.chat.Component

fun meterTrackerCategory(categoryRegistrar: CategoryRegistrar) {
    categoryRegistrar.register("meter_tracker") {
        name(Component.translatable("config.trident.meter_tracker.name"))

        rootOptions.register("meter_tracker_enabled") {
            name(Component.translatable("config.trident.meter_tracker.enabled.name"))
            description(OptionDescription.of(Component.translatable("config.trident.meter_tracker.enabled.description")))
            binding(handler.instance()::meterTrackerEnabled, true)
            controller(tickBox())
        }
    }
}