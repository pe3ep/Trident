package cc.pe3epwithyou.trident.config.groups

import cc.pe3epwithyou.trident.config.Config.Companion.handler
import cc.pe3epwithyou.trident.utils.Resources
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.dsl.CategoryRegistrar
import dev.isxander.yacl3.dsl.binding
import dev.isxander.yacl3.dsl.tickBox
import net.minecraft.network.chat.Component

fun indicatorsCategory(categoriesRegistrar: CategoryRegistrar) {
    categoriesRegistrar.register("indicators") {
        name(Component.translatable("config.trident.indicators.name"))

        rootOptions.register("blueprint_indicators") {
            name(Component.translatable("config.trident.indicators.blueprint_indicators.name"))
            description(
                OptionDescription.createBuilder()
                    .text(Component.translatable("config.trident.indicators.blueprint_indicators.description"))
                    .image(
                        Resources.trident(
                            "textures/config/blueprint_indicators.png"
                        ), 405, 316
                    ).build()
            )
            binding(handler.instance()::globalBlueprintIndicators, true)
            controller(tickBox())
        }

        rootOptions.register("craftable_indicators") {
            name(Component.translatable("config.trident.indicators.craftable_indicators.name"))
            description(
                OptionDescription.of(
                    Component.translatable("config.trident.indicators.craftable_indicators.description")
                )
            )
            binding(handler.instance()::globalCraftableIndicators, true)
            controller(tickBox())
        }

        rootOptions.register("upgrade_indicators") {
            name(Component.translatable("config.trident.indicators.upgrade_indicators.name"))
            description(
                OptionDescription.of(
                    Component.translatable("config.trident.indicators.upgrade_indicators.description")
                )
            )
            binding(handler.instance()::globalUpgradeIndicators, true)
            controller(tickBox())
        }
    }
}