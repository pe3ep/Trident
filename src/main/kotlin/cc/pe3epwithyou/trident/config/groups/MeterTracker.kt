package cc.pe3epwithyou.trident.config.groups

import cc.pe3epwithyou.trident.config.Config.Companion.handler
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.popped
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withTridentFont
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionEventListener
import dev.isxander.yacl3.dsl.CategoryRegistrar
import dev.isxander.yacl3.dsl.binding
import dev.isxander.yacl3.dsl.tickBox
import net.minecraft.network.chat.Component

fun meterTrackerCategory(categoryRegistrar: CategoryRegistrar) {
    categoryRegistrar.register("meter_tracker") {
        name(Component.literal("\uE01F ").withTridentFont().withoutShadow().popped().append(Component.translatable("config.trident.meter_tracker.name")))

        groups.register("daily") {
            name(Component.translatable("config.trident.meter_tracker.daily.name"))
            options.register("daily_meter_enabled") {
                name(Component.translatable("config.trident.meter_tracker.daily.enabled.name"))
                description(OptionDescription.of(Component.translatable("config.trident.meter_tracker.daily.enabled.description")))
                binding(handler.instance()::meterTrackerDailyMeterEnabled, true)
                controller(tickBox())
            }

            lateinit var sfxOption: Option<Boolean>

            options.register("daily_meter_xp_animation") {
                name(Component.translatable("config.trident.meter_tracker.daily.xp_animation.name"))
                description(OptionDescription.of(Component.translatable("config.trident.meter_tracker.daily.xp_animation.description")))
                binding(handler.instance()::meterTrackerDailyMeterAnimation, true)
                controller(tickBox())
                addListener { option, event ->
                    if (event == OptionEventListener.Event.STATE_CHANGE) {
                        sfxOption.setAvailable(option.pendingValue())
                    }
                }
            }

            sfxOption = options.register("daily_meter_sound_effects") {
                name(Component.translatable("config.trident.meter_tracker.daily.sound_effects.name"))
                description(OptionDescription.of(Component.translatable("config.trident.meter_tracker.daily.sound_effects.description")))
                binding(handler.instance()::meterTrackerDailyMeterSound, true)
                controller(tickBox())
                available(handler.instance().meterTrackerDailyMeterAnimation)
            }
        }

        groups.register("weekly") {
            name(Component.translatable("config.trident.meter_tracker.weekly.name"))
            options.register("weekly_meter_enabled") {
                name(Component.translatable("config.trident.meter_tracker.weekly.enabled.name"))
                description(OptionDescription.of(Component.translatable("config.trident.meter_tracker.weekly.enabled.description")))
                binding(handler.instance()::meterTrackerWeeklyMeterEnabled, true)
                controller(tickBox())
            }
        }
    }
}