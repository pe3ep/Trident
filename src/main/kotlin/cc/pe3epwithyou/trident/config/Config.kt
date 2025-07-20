package cc.pe3epwithyou.trident.config

import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.dsl.*
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class Config {
    @SerialEntry
    var globalRarityOverlay: Boolean = false

    @SerialEntry
    var fishingSuppliesModule: Boolean = true

    @SerialEntry
    var fishingWayfinderModule: Boolean = true

    @SerialEntry
    var debugEnableLogging: Boolean = false

    object Global {
        val rarityOverlay: Boolean
            get() = handler.instance().globalRarityOverlay
    }

    object Debug {
        val enableLogging: Boolean
            get() = handler.instance().debugEnableLogging
    }

    object Fishing {
        val suppliesModule: Boolean
            get() = handler.instance().fishingSuppliesModule

        val wayfinderModule: Boolean
            get() = handler.instance().fishingWayfinderModule
    }

    companion object {
        val handler: ConfigClassHandler<Config> by lazy {
            ConfigClassHandler.createBuilder(Config::class.java)
                .id(ResourceLocation.fromNamespaceAndPath("trident", "config"))
                .serializer { config ->
                    GsonConfigSerializerBuilder.create(config)
                        .setPath(FabricLoader.getInstance().configDir.resolve("trident.json"))
                        .build()
                }
                .build()
        }

        fun init() {
            handler.load()
        }

        fun getScreen(parentScreen: Screen): Screen = YetAnotherConfigLib("trident") {
            title(Component.translatable("config.trident"))
            save(handler::save)

            categories.register("trident") {
                name(Component.translatable("config.trident"))

                groups.register("global") {
                    name(Component.translatable("config.trident.global.name"))
                    description(OptionDescription.of(Component.translatable("config.trident.global.description")))

                    options.register<Boolean>("rarity_overlay") {
                        name(Component.translatable("config.trident.global.rarity_overlay.name"))
                        description(OptionDescription.createBuilder()
                            .text(Component.translatable("config.trident.global.rarity_overlay.description"))
                            .image(ResourceLocation.fromNamespaceAndPath("trident", "textures/config/rarity_overlay.png"), 120, 88)
                            .build()
                        )
                        binding(handler.instance()::globalRarityOverlay, false)
                        controller(tickBox())
                    }
                }

                groups.register("fishing") {
                    name(Component.translatable("config.trident.fishing.name"))
                    description(OptionDescription.of(Component.translatable("config.trident.fishing.description")))

                    options.register<Boolean>("rarity_overlay") {
                        name(Component.translatable("config.trident.fishing.supplies_module.name"))
                        description(OptionDescription.createBuilder()
                            .text(Component.translatable("config.trident.fishing.supplies_module.description"))
                            .build()
                        )
                        binding(handler.instance()::fishingSuppliesModule, true)
                        controller(tickBox())
                    }
//                    options.register<Boolean>("wayfinder_module") {
//                        name(Component.translatable("config.trident.fishing.wayfinder_module.name"))
//                        description(OptionDescription.createBuilder()
//                            .text(Component.translatable("config.trident.fishing.wayfinder_module.description"))
//                            .build()
//                        )
//                        binding(handler.instance()::fishingWayfinderModule, true)
//                        controller(tickBox())
//                    }
                }
            }

            categories.register("debug") {
                name(Component.translatable("config.trident.debug"))

                groups.register("debug") {
                    name(Component.translatable("config.trident.debug"))
                    description(OptionDescription.of(Component.translatable("config.trident.debug.description")))

                    options.register<Boolean>("logging") {
                        name(Component.translatable("config.trident.debug.enable_logging.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.debug.enable_logging.description")))
                        binding(handler.instance()::debugEnableLogging, false)
                        controller(tickBox())
                    }
                }

            }
        }.generateScreen(parentScreen)
    }
}