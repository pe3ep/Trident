package cc.pe3epwithyou.trident.config.groups

import cc.pe3epwithyou.trident.config.Config.Companion.handler
import cc.pe3epwithyou.trident.interfaces.fishing.WayfinderModuleDisplay
import cc.pe3epwithyou.trident.utils.Resources
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionEventListener
import dev.isxander.yacl3.dsl.CategoryRegistrar
import dev.isxander.yacl3.dsl.available
import dev.isxander.yacl3.dsl.binding
import dev.isxander.yacl3.dsl.enumSwitch
import dev.isxander.yacl3.dsl.tickBox
import net.minecraft.network.chat.Component

fun fishingCategory(categoryRegistrar: CategoryRegistrar) {
    categoryRegistrar.register("fishing") {
        name(Component.translatable("config.trident.fishing.name"))

        lateinit var suppliesModuleDurability: Option<Boolean>

        rootOptions.register("supplies_module") {
            name(Component.translatable("config.trident.fishing.supplies_module.name"))
            description(
                OptionDescription.createBuilder()
                    .text(Component.translatable("config.trident.fishing.supplies_module.description"))
                    .image(
                        Resources.trident("textures/config/supplies.png"), 507, 333
                    ).build()
            )
            binding(handler.instance()::fishingSuppliesModule, true)
            controller(tickBox())
            addListener { option, event ->
                if (event == OptionEventListener.Event.STATE_CHANGE) {
                    suppliesModuleDurability.setAvailable(option.pendingValue())
                }
            }
        }

        suppliesModuleDurability = rootOptions.register("supplies_module_durability") {
            name(Component.translatable("config.trident.fishing.supplies_module.durability.name"))
            description(OptionDescription.of(Component.translatable("config.trident.fishing.supplies_module.durability.description")))
            binding(
                handler.instance()::fishingSuppliesModuleShowAugmentDurability,
                false
            )
            controller(tickBox())
            available { handler.instance().fishingSuppliesModule }

        }

        rootOptions.register("show_augment_status_in_interface") {
            name(Component.translatable("config.trident.fishing.show_augment_status_in_interface.name"))
            description(OptionDescription.of(Component.translatable("config.trident.fishing.show_augment_status_in_interface.description")))
            binding(
                handler.instance()::fishingShowAugmentStatusInInterface,
                true
            )
            controller(tickBox())
        }

        rootOptions.register("flash_if_depleted") {
            name(Component.translatable("config.trident.fishing.flash_if_depleted.name"))
            description(OptionDescription.of(Component.translatable("config.trident.fishing.flash_if_depleted.description")))
            binding(handler.instance()::fishingFlashIfDepleted, true)
            controller(tickBox())
        }

        rootOptions.register("island_indicators") {
            name(Component.translatable("config.trident.fishing.island_indicators.name"))
            description(OptionDescription.of(Component.translatable("config.trident.fishing.island_indicators.description")))
            binding(handler.instance()::fishingIslandIndicators, true)
            controller(tickBox())
        }

        lateinit var wayfinderModuleDisplayOption: Option<WayfinderModuleDisplay>

        rootOptions.register("wayfinder_module") {
            name(Component.translatable("config.trident.fishing.wayfinder_module.name"))
            description(
                OptionDescription.createBuilder()
                    .text(Component.translatable("config.trident.fishing.wayfinder_module.description"))
                    .image(
                        Resources.trident("textures/config/wayfinder.png"), 768, 310
                    ).build()
            )
            binding(handler.instance()::fishingWayfinderModule, true)
            controller(tickBox())
            addListener { option, event ->
                if (event == OptionEventListener.Event.STATE_CHANGE) {
                    wayfinderModuleDisplayOption.setAvailable(option.pendingValue())
                }
            }
        }

        wayfinderModuleDisplayOption = rootOptions.register("wayfinder_module_display") {
            name(Component.translatable("config.trident.fishing.wayfinder_module.display.name"))
            description(
                OptionDescription.of(Component.translatable("config.trident.fishing.wayfinder_module.display.description"))
            )
            binding(
                handler.instance()::fishingWayfinderModuleDisplay,
                WayfinderModuleDisplay.FULL
            )
            controller(enumSwitch<WayfinderModuleDisplay> { v -> v.displayName })
            available { handler.instance().fishingWayfinderModule }
        }
    }
}