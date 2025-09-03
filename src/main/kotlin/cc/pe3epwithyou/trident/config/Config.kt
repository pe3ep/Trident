package cc.pe3epwithyou.trident.config

import cc.pe3epwithyou.trident.feature.killfeed.Position
import cc.pe3epwithyou.trident.feature.rarityslot.DisplayType
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemes
import cc.pe3epwithyou.trident.utils.ChatUtils
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
    @Deprecated("This option has been moved to a separate group")
    @SerialEntry
    var globalRarityOverlay: Boolean? = null

    @SerialEntry
    var globalBlueprintIndicators: Boolean = true

    @SerialEntry
    var globalCurrentTheme: TridentThemes = TridentThemes.DEFAULT

    @SerialEntry
    var raritySlotEnabled: Boolean = false

    @SerialEntry
    var raritySlotDisplayType: DisplayType = DisplayType.OUTLINE

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
    var debugLogForScrapers: Boolean = false

    @SerialEntry
    var debugBypassOnIsland: Boolean = false

    @SerialEntry
    var gamesAutoFocus: Boolean = false


    @SerialEntry
    var killfeedEnabled: Boolean = true

    @SerialEntry
    var killfeedHideKills: Boolean = false

    @SerialEntry
    var killfeedClearAfterRound: Boolean = true

    @SerialEntry
    var killfeedShowYouInKill: Boolean = true

    @SerialEntry
    var killfeedReverseOrder: Boolean = false

    @SerialEntry
    var killfeedPositionSide: Position = Position.RIGHT

    @SerialEntry
    var killfeedRemoveKillTime: Int = 10

    @SerialEntry
    var killfeedMaxKills: Int = 5

    @SerialEntry
    var killfeedShowKillStreaks: Boolean = true

    @SerialEntry
    var questingEnabled: Boolean = true

    @SerialEntry
    var questingRarityColorName: Boolean = false

    @SerialEntry
    var questingShowInLobby: Boolean = true

    @SerialEntry
    var questingShowLeft: Boolean = true

    @SerialEntry
    var questingHideIfNoQuests: Boolean = false

    object Global {
        val blueprintIndicators: Boolean
            get() = handler.instance().globalBlueprintIndicators
        val currentTheme: TridentThemes
            get() = handler.instance().globalCurrentTheme
    }

    object RaritySlot {
        val enabled: Boolean
            get() = handler.instance().raritySlotEnabled
        val displayType: DisplayType
            get() = handler.instance().raritySlotDisplayType
    }

    object Debug {
        val enableLogging: Boolean
            get() = handler.instance().debugEnableLogging
        val drawSlotNumber: Boolean
            get() = handler.instance().debugDrawSlotNumber
        val logForScrapers: Boolean
            get() = handler.instance().debugLogForScrapers
        val bypassOnIsland: Boolean
            get() = handler.instance().debugBypassOnIsland
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
        val clearAfterRound: Boolean
            get() = handler.instance().killfeedClearAfterRound
        val showYouInKill: Boolean
            get() = handler.instance().killfeedShowYouInKill
        val reverseOrder: Boolean
            get() = handler.instance().killfeedReverseOrder
        val positionSide: Position
            get() = handler.instance().killfeedPositionSide
        val removeKillTime: Int
            get() = handler.instance().killfeedRemoveKillTime
        val maxKills: Int
            get() = handler.instance().killfeedMaxKills
        val showKillstreaks: Boolean
            get() = handler.instance().killfeedShowKillStreaks
    }

    object Questing {
        val enabled: Boolean
            get() = handler.instance().questingEnabled
        val rarityColorName: Boolean
            get() = handler.instance().questingRarityColorName
        val showInLobby: Boolean
            get() = handler.instance().questingShowInLobby
        val showLeft: Boolean
            get() = handler.instance().questingShowLeft
        val hideIfNoQuests: Boolean
            get() = handler.instance().questingHideIfNoQuests
    }

    companion object {
        val handler: ConfigClassHandler<Config> by lazy {
            ConfigClassHandler.createBuilder(Config::class.java)
                .id(ResourceLocation.fromNamespaceAndPath("trident", "config")).serializer { config ->
                    GsonConfigSerializerBuilder.create(config)
                        .setPath(FabricLoader.getInstance().configDir.resolve("trident.json")).build()
                }.build()
        }

        @Suppress("DEPRECATION")
        fun convertDeprecated() {
            val rarityOverlayPrev = handler.instance().globalRarityOverlay
            if (rarityOverlayPrev != null) {
                ChatUtils.warn("Detected a deprecated config value for rarity overlay, converting it")

                handler.instance().raritySlotEnabled = rarityOverlayPrev
                /* Reset the old value to null */
                handler.instance().globalRarityOverlay = null
            }

            handler.save()
        }

        fun init() {
            handler.load()
        }

        fun getScreen(parentScreen: Screen?): Screen = YetAnotherConfigLib("trident") {
            title(Component.translatable("config.trident"))
            save {
                handler.save()
                DialogCollection.refreshOpenedDialogs()
            }

            categories.register("trident") {
                name(Component.translatable("config.trident"))

                groups.register("global") {
                    name(Component.translatable("config.trident.global.name"))
                    description(OptionDescription.of(Component.translatable("config.trident.global.description")))

                    options.register<Boolean>("blueprint_indicators") {
                        name(Component.translatable("config.trident.global.blueprint_indicators.name"))
                        description(
                            OptionDescription.createBuilder()
                                .text(Component.translatable("config.trident.global.blueprint_indicators.description"))
                                .image(
                                    ResourceLocation.fromNamespaceAndPath(
                                        "trident", "textures/config/blueprint_indicators.png"
                                    ), 405, 316
                                ).build()
                        )
                        binding(handler.instance()::globalBlueprintIndicators, true)
                        controller(tickBox())
                    }

                    options.register<TridentThemes>("theme") {
                        name(Component.translatable("config.trident.global.theme.name"))
                        description(
                            OptionDescription.createBuilder()
                                .text(Component.translatable("config.trident.global.theme.description")).image(
                                    ResourceLocation.fromNamespaceAndPath("trident", "textures/config/theme.png"),
                                    497,
                                    329
                                ).build()
                        )
                        binding(handler.instance()::globalCurrentTheme, TridentThemes.DEFAULT)
                        controller(enumSwitch<TridentThemes> { v -> v.displayName })
                    }
                }

                groups.register("rarity_slot") {
                    name(Component.translatable("config.trident.rarity_slot.name"))
                    description(
                        OptionDescription.of(
                            Component.translatable("config.trident.rarity_slot.description")
                        )
                    )

                    options.register<Boolean>("rarity_slot_enabled") {
                        name(Component.translatable("config.trident.rarity_slot.name"))
                        description(
                            OptionDescription.createBuilder()
                                .text(Component.translatable("config.trident.rarity_slot.description")).image(
                                    ResourceLocation.fromNamespaceAndPath(
                                        "trident", "textures/config/rarity_overlay.png"
                                    ), 120, 88
                                ).build()
                        )
                        binding(handler.instance()::raritySlotEnabled, false)
                        controller(tickBox())
                    }

                    options.register<DisplayType>("rarity_slot_display_type") {
                        name(Component.translatable("config.trident.rarity_slot.display_type.name"))
                        description(
                            OptionDescription.of(
                                Component.translatable("config.trident.rarity_slot.display_type.description")
                            )
                        )
                        binding(handler.instance()::raritySlotDisplayType, DisplayType.OUTLINE)
                        controller(enumSwitch<DisplayType> { v -> v.displayName })
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
                        description(
                            OptionDescription.createBuilder()
                                .text(Component.translatable("config.trident.killfeed.enabled.description")).image(
                                    ResourceLocation.fromNamespaceAndPath("trident", "textures/config/killfeed.png"),
                                    618,
                                    332
                                ).build()
                        )
                        binding(handler.instance()::killfeedEnabled, true)
                        controller(tickBox())
                    }

                    options.register<Boolean>("killfeed_hide_kills") {
                        name(Component.translatable("config.trident.killfeed.hide_kills.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.hide_kills.description")))
                        binding(handler.instance()::killfeedHideKills, false)
                        controller(tickBox())
                    }

                    options.register<Boolean>("killfeed_show_kill_streaks") {
                        name(Component.translatable("config.trident.killfeed.show_kill_streaks.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.show_kill_streaks.description")))
                        binding(handler.instance()::killfeedShowKillStreaks, true)
                        controller(tickBox())
                    }

                    options.register<Boolean>("killfeed_clear_after_round") {
                        name(Component.translatable("config.trident.killfeed.clear_after_round.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.clear_after_round.description")))
                        binding(handler.instance()::killfeedClearAfterRound, true)
                        controller(tickBox())
                    }

                    options.register<Boolean>("killfeed_show_you_in_kill") {
                        name(Component.translatable("config.trident.killfeed.show_you_in_kill.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.show_you_in_kill.description")))
                        binding(handler.instance()::killfeedShowYouInKill, true)
                        controller(tickBox())
                    }

                    options.register<Boolean>("killfeed_reverse_order") {
                        name(Component.translatable("config.trident.killfeed.reverse_order.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.reverse_order.description")))
                        binding(handler.instance()::killfeedReverseOrder, false)
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

                groups.register("questing") {
                    name(Component.translatable("config.trident.questing.name"))
                    description(OptionDescription.of(Component.translatable("config.trident.questing.description")))

                    options.register<Boolean>("questing_enabled") {
                        name(Component.translatable("config.trident.questing.enabled.name"))
                        description(
                            OptionDescription.createBuilder()
                                .text(Component.translatable("config.trident.questing.enabled.description")).image(
                                    ResourceLocation.fromNamespaceAndPath("trident", "textures/config/questing.png"),
                                    414,
                                    338
                                ).build()
                        )
                        binding(handler.instance()::questingEnabled, true)
                        controller(tickBox())
                    }

                    options.register<Boolean>("questing_rarity_color_name") {
                        name(Component.translatable("config.trident.questing.rarity_color_name.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.questing.rarity_color_name.description")))
                        binding(handler.instance()::questingRarityColorName, false)
                        controller(tickBox())
                    }

                    options.register<Boolean>("questing_show_in_lobby") {
                        name(Component.translatable("config.trident.questing.show_in_lobby.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.questing.show_in_lobby.description")))
                        binding(handler.instance()::questingShowInLobby, true)
                        controller(tickBox())
                    }

                    options.register<Boolean>("questing_show_left") {
                        name(Component.translatable("config.trident.questing.show_left.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.questing.show_left.description")))
                        binding(handler.instance()::questingShowLeft, true)
                        controller(tickBox())
                    }

                    options.register<Boolean>("questing_hide_if_no_quests") {
                        name(Component.translatable("config.trident.questing.hide_if_no_quests.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.questing.hide_if_no_quests.description")))
                        binding(handler.instance()::questingHideIfNoQuests, false)
                        controller(tickBox())
                    }
                }

                groups.register("fishing") {
                    name(Component.translatable("config.trident.fishing.name"))
                    description(OptionDescription.of(Component.translatable("config.trident.fishing.description")))

                    options.register<Boolean>("supplies_module") {
                        name(Component.translatable("config.trident.fishing.supplies_module.name"))
                        description(
                            OptionDescription.createBuilder()
                                .text(Component.translatable("config.trident.fishing.supplies_module.description"))
                                .image(
                                    ResourceLocation.fromNamespaceAndPath("trident", "textures/config/supplies.png"),
                                    507,
                                    333
                                ).build()
                        )
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

                    options.register<Boolean>("log_for_scrapers") {
                        name(Component.translatable("config.trident.debug.log_for_scrapers.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.debug.log_for_scrapers.description")))
                        binding(handler.instance()::debugLogForScrapers, false)
                        controller(tickBox())
                    }

                    options.register<Boolean>("bypass_on_island") {
                        name(Component.translatable("config.trident.debug.bypass_on_island.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.debug.bypass_on_island.description")))
                        binding(handler.instance()::debugBypassOnIsland, false)
                        controller(tickBox())
                    }
                }

            }
        }.generateScreen(parentScreen)
    }
}