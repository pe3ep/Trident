package cc.pe3epwithyou.trident.config

import cc.pe3epwithyou.trident.dialogs.themes.TridentThemes
import cc.pe3epwithyou.trident.widgets.killfeed.Position
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
    var globalBlueprintIndicators: Boolean = true

    @SerialEntry
    var globalCurrentTheme: TridentThemes = TridentThemes.DEFAULT

    @SerialEntry
    var fishingSuppliesModule: Boolean = true

    @SerialEntry
    var fishingWayfinderModule: Boolean = true

    @SerialEntry
    var fishingFlashIfDepleted: Boolean = true

    @SerialEntry
    var debugEnableLogging: Boolean = false

    @SerialEntry
    var debugDrawSlotNumber: Boolean = false

    @SerialEntry
    var gamesAutoFocus: Boolean = false


    @SerialEntry
    var killfeedEnabled: Boolean = true

    @SerialEntry
    var killfeedHideKills: Boolean = false

    @SerialEntry
    var killfeedPositionSide: Position = Position.RIGHT

    @SerialEntry
    var killfeedRemoveKillTime: Int = 10

    @SerialEntry
    var killfeedMaxKills: Int = 5

    object Global {
        val rarityOverlay: Boolean
            get() = handler.instance().globalRarityOverlay

        val blueprintIndicators: Boolean
            get() = handler.instance().globalBlueprintIndicators

        val currentTheme: TridentThemes
            get() = handler.instance().globalCurrentTheme
    }

    object Debug {
        val enableLogging: Boolean
            get() = handler.instance().debugEnableLogging
        val drawSlotNumber: Boolean
            get() = handler.instance().debugDrawSlotNumber
    }

    object Fishing {
        val suppliesModule: Boolean
            get() = handler.instance().fishingSuppliesModule

        val wayfinderModule: Boolean
            get() = handler.instance().fishingWayfinderModule

        val flashIfDepleted: Boolean
            get() = handler.instance().fishingFlashIfDepleted
    }

    object Games {
        val autoFocus: Boolean
            get() = handler.instance().gamesAutoFocus
    }

    object KillFeed {
        val enabled: Boolean
            get() = handler.instance().killfeedEnabled
        val hideKills: Boolean
            get() = handler.instance().killfeedHideKills
        val positionSide: Position
            get() = handler.instance().killfeedPositionSide
        val removeKillTime: Int
            get() = handler.instance().killfeedRemoveKillTime
        val maxKills: Int
            get() = handler.instance().killfeedMaxKills
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

        fun getScreen(parentScreen: Screen?): Screen = YetAnotherConfigLib("trident") {
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

                    options.register<Boolean>("blueprint_indicators") {
                        name(Component.translatable("config.trident.global.blueprint_indicators.name"))
                        description(OptionDescription.createBuilder()
                            .text(Component.translatable("config.trident.global.blueprint_indicators.description"))
//                            .image(ResourceLocation.fromNamespaceAndPath("trident", "textures/config/blueprint_indicators.png"), 120, 88)
                            .build()
                        )
                        binding(handler.instance()::globalBlueprintIndicators, true)
                        controller(tickBox())
                    }

                    options.register<TridentThemes>("theme") {
                        name(Component.translatable("config.trident.global.theme.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.global.theme.description")))
                        binding(handler.instance()::globalCurrentTheme, TridentThemes.DEFAULT)
                        controller(enumSwitch<TridentThemes> { v -> v.displayName })
                    }
                }

                groups.register("games") {
                    name(Component.translatable("config.trident.games.name"))
                    description(OptionDescription.of(Component.translatable("config.trident.games.description")))

                    options.register<Boolean>("auto_focus") {
                        name(Component.translatable("config.trident.games.auto_focus.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.games.auto_focus.description")))
                        binding(handler.instance()::gamesAutoFocus, false)
                        controller(tickBox())
                    }
                }

                groups.register("killfeed") {
                    name(Component.translatable("config.trident.killfeed.name"))
                    description(OptionDescription.of(Component.translatable("config.trident.killfeed.description")))

                    options.register<Boolean>("killfeed_enabled") {
                        name(Component.translatable("config.trident.killfeed.enabled.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.enabled.description")))
                        binding(handler.instance()::killfeedEnabled, true)
                        controller(tickBox())
                    }

                    options.register<Boolean>("killfeed_hide_kills") {
                        name(Component.translatable("config.trident.killfeed.hide_kills.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.hide_kills.description")))
                        binding(handler.instance()::killfeedHideKills, false)
                        controller(tickBox())
                    }

                    options.register<Position>("killfeed_position_side") {
                        name(Component.translatable("config.trident.killfeed.position_side.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.position_side.description")))
                        binding(handler.instance()::killfeedPositionSide, Position.RIGHT)
                        controller(enumSwitch<Position> { v -> v.displayName })
                    }

                    options.register<Int>("killfeed_remove_kill_time") {
                        name(Component.translatable("config.trident.killfeed.remove_kill_time.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.remove_kill_time.description")))
                        binding(handler.instance()::killfeedRemoveKillTime, 10)
                        controller(slider(IntRange(0, 30), 1))
                    }

                    options.register<Int>("killfeed_max_kills") {
                        name(Component.translatable("config.trident.killfeed.max_kills.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.max_kills.description")))
                        binding(handler.instance()::killfeedMaxKills, 5)
                        controller(slider(IntRange(1, 10), 1))
                    }
                }

                groups.register("fishing") {
                    name(Component.translatable("config.trident.fishing.name"))
                    description(OptionDescription.of(Component.translatable("config.trident.fishing.description")))

                    options.register<Boolean>("rarity_overlay") {
                        name(Component.translatable("config.trident.fishing.supplies_module.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.fishing.supplies_module.description")))
                        binding(handler.instance()::fishingSuppliesModule, true)
                        controller(tickBox())
                    }

                    options.register<Boolean>("flash_if_depleted") {
                        name(Component.translatable("config.trident.fishing.flash_if_depleted.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.fishing.flash_if_depleted.description")))
                        binding(handler.instance()::fishingFlashIfDepleted, true)
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

                    options.register<Boolean>("draw_slot_number") {
                        name(Component.translatable("config.trident.debug.draw_slot_number.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.debug.draw_slot_number.description")))
                        binding(handler.instance()::debugDrawSlotNumber, false)
                        controller(tickBox())
                    }
                }

            }
        }.generateScreen(parentScreen)
    }
}