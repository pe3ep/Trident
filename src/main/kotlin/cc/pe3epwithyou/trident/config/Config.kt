package cc.pe3epwithyou.trident.config

import cc.pe3epwithyou.trident.config.screen.RaritySlotPreview
import cc.pe3epwithyou.trident.feature.ChatSwitcherButtons
import cc.pe3epwithyou.trident.feature.api.ApiProvider
import cc.pe3epwithyou.trident.feature.killfeed.KillfeedPosition
import cc.pe3epwithyou.trident.feature.rarityslot.DisplayType
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.fishing.WayfinderModuleDisplay
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemes
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.Resources
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionEventListener
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.dsl.*
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class Config {
    @SerialEntry
    var globalCallToHome: Boolean = true

    @Deprecated("This option has been moved to a separate group")
    @SerialEntry
    var globalRarityOverlay: Boolean? = null

    @SerialEntry
    var globalApiProvider: ApiProvider = ApiProvider.TRIDENT

    @SerialEntry
    var globalBlueprintIndicators: Boolean = true

    @SerialEntry
    var globalCraftableIndicators: Boolean = true

    @SerialEntry
    var globalExchangeImprovements: Boolean = true

    @SerialEntry
    var globalChatChannelButtons: Boolean = false

    @SerialEntry
    var globalCurrentTheme: TridentThemes = TridentThemes.DEFAULT

    @SerialEntry
    var raritySlotEnabled: Boolean = false

    @SerialEntry
    var raritySlotDisplayType: DisplayType = DisplayType.OUTLINE

    @SerialEntry
    var fishingSuppliesModule: Boolean = true

    @SerialEntry
    var fishingSuppliesModuleShowAugmentDurability: Boolean = false

    @SerialEntry
    var fishingShowAugmentStatusInInterface: Boolean = true

    @SerialEntry
    var fishingWayfinderModule: Boolean = true

    @SerialEntry
    var fishingWayfinderModuleDisplay: WayfinderModuleDisplay = WayfinderModuleDisplay.FULL

    @SerialEntry
    var fishingFlashIfDepleted: Boolean = true

    @SerialEntry
    var fishingIslandIndicators: Boolean = true

    @SerialEntry
    var debugEnableLogging: Boolean = false

    @SerialEntry
    var debugDrawSlotNumber: Boolean = false

    @Deprecated("Option is no longer used")
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
    var killfeedPositionSide: KillfeedPosition = KillfeedPosition.RIGHT

    @SerialEntry
    var killfeedPositionY: Int = 20

    @SerialEntry
    var killfeedRemoveKillTime: Int = 10

    @SerialEntry
    var killfeedMaxKills: Int = 5

    @SerialEntry
    var killfeedShowKillStreaks: Boolean = true

    @SerialEntry
    var questingEnabled: Boolean = true

    @SerialEntry
    var questingRarityColorName: Boolean = true

    @SerialEntry
    var questingShowInLobby: Boolean = true

    @SerialEntry
    var questingShowLeft: Boolean = true

    @SerialEntry
    var questingHideIfNoQuests: Boolean = false

    @SerialEntry
    var apiKey: String = ""

    @SerialEntry
    var sawIntroduction: Boolean = false

    object Global {
        val callToHome: Boolean
            get() = handler.instance().globalCallToHome
        val apiProvider: ApiProvider
            get() = handler.instance().globalApiProvider
        val blueprintIndicators: Boolean
            get() = handler.instance().globalBlueprintIndicators
        val chatChannelButtons: Boolean
            get() = handler.instance().globalChatChannelButtons
        val currentTheme: TridentThemes
            get() = handler.instance().globalCurrentTheme
        val craftableIndicators: Boolean
            get() = handler.instance().globalCraftableIndicators
        val exchangeImprovements: Boolean
            get() = handler.instance().globalExchangeImprovements
    }

    object RaritySlot {
        val enabled: Boolean
            get() = handler.instance().raritySlotEnabled
        val displayType: DisplayType
            get() = handler.instance().raritySlotDisplayType
    }

    object Debug {
        val developerMode: Boolean
            get() = handler.instance().debugEnableLogging
        val drawSlotNumber: Boolean
            get() = handler.instance().debugDrawSlotNumber
        val bypassOnIsland: Boolean
            get() = handler.instance().debugBypassOnIsland
    }

    object Fishing {
        val suppliesModule: Boolean
            get() = handler.instance().fishingSuppliesModule
        val suppliesModuleShowAugmentDurability: Boolean
            get() = handler.instance().fishingSuppliesModuleShowAugmentDurability
        val showAugmentStatusInInterface: Boolean
            get() = handler.instance().fishingShowAugmentStatusInInterface
        val flashIfDepleted: Boolean
            get() = handler.instance().fishingFlashIfDepleted
        val islandIndicators: Boolean
            get() = handler.instance().fishingIslandIndicators
        val wayfinderModule: Boolean
            get() = handler.instance().fishingWayfinderModule
        val wayfinderModuleDisplay: WayfinderModuleDisplay
            get() = handler.instance().fishingWayfinderModuleDisplay
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
        val positionSide: KillfeedPosition
            get() = handler.instance().killfeedPositionSide
        val positionY: Int
            get() = handler.instance().killfeedPositionY
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

    object Api {
        val key: String
            get() = handler.instance().apiKey
    }

    companion object {
        val handler: ConfigClassHandler<Config> by lazy {
            ConfigClassHandler.createBuilder(Config::class.java).id(Resources.trident("config"))
                .serializer { config ->
                    GsonConfigSerializerBuilder.create(config)
                        .setPath(FabricLoader.getInstance().configDir.resolve("trident.json"))
                        .build()
                }.build()
        }

        @Suppress("DEPRECATION")
        fun convertDeprecated() {
            val rarityOverlayPrev = handler.instance().globalRarityOverlay
            if (rarityOverlayPrev != null) {
                Logger.warn("Detected a deprecated config value for rarity overlay, converting it")

                handler.instance().raritySlotEnabled =
                    rarityOverlayPrev /* Reset the old value to null */
                handler.instance().globalRarityOverlay = null
            }

            // Check Island Utils compatibility
            if (handler.instance().globalChatChannelButtons && !ChatSwitcherButtons.checkCompatibility()) {
                handler.instance().globalChatChannelButtons = false
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

                    lateinit var apiOption: Option<ApiProvider>

                    options.register("call_to_home") {
                        name(Component.translatable("config.trident.global.call_to_home.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.global.call_to_home.description")))
                        binding(handler.instance()::globalCallToHome, true)
                        controller(tickBox())
                        addListener { option, event ->
                            if (event == OptionEventListener.Event.STATE_CHANGE) {
                                if (!option.pendingValue()) {
                                    handler.instance().globalApiProvider = ApiProvider.SELF_TOKEN
                                    apiOption.setAvailable(false)
                                } else {
                                    apiOption.setAvailable(true)
                                    apiOption.requestSet(ApiProvider.TRIDENT)
                                }
                                handler.save()
                            }
                        }
                    }

                    apiOption = options.register("api_provider") {
                        name(Component.translatable("config.trident.global.api_provider.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.global.api_provider.description")))
                        binding(handler.instance()::globalApiProvider, ApiProvider.TRIDENT)
                        controller(enumSwitch<ApiProvider> { v -> v.displayName })
                        available { handler.instance().globalCallToHome }
                    }


                    options.register("theme") {
                        name(Component.translatable("config.trident.global.theme.name"))
                        description(
                            OptionDescription.createBuilder()
                                .text(Component.translatable("config.trident.global.theme.description"))
                                .image(
                                    Resources.trident("textures/config/theme.png"), 497, 329
                                ).build()
                        )
                        binding(handler.instance()::globalCurrentTheme, TridentThemes.DEFAULT)
                        controller(enumSwitch<TridentThemes> { v -> v.displayName })
                    }

                    options.register("exchange_improvements") {
                        name(Component.translatable("config.trident.global.exchange_improvements.name"))
                        description(
                            OptionDescription.createBuilder()
                                .text(Component.translatable("config.trident.global.exchange_improvements.description"))
                                .image(
                                    Resources.trident(
                                        "textures/config/exchange_improvements.png"
                                    ), 185, 194
                                ).build()
                        )
                        binding(handler.instance()::globalExchangeImprovements, true)
                        controller(tickBox())
                    }

                    options.register("chat_channel_buttons") {
                        name(Component.translatable("config.trident.global.chat_channel_buttons.name"))
                        description(
                            OptionDescription.of(Component.translatable("config.trident.global.chat_channel_buttons.description"))
                        )
                        binding(handler.instance()::globalChatChannelButtons, false)
                        controller(tickBox())
                        available(ChatSwitcherButtons.checkCompatibility())
                    }
                }

                groups.register("indicators") {
                    name(Component.translatable("config.trident.indicators.name"))
                    description(OptionDescription.of(Component.translatable("config.trident.indicators.description")))

                    options.register("blueprint_indicators") {
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

                    options.register("craftable_indicators") {
                        name(Component.translatable("config.trident.indicators.craftable_indicators.name"))
                        description(
                            OptionDescription.of(
                                Component.translatable("config.trident.indicators.craftable_indicators.description")
                            )
                        )
                        binding(handler.instance()::globalCraftableIndicators, true)
                        controller(tickBox())
                    }
                }

                groups.register("rarity_slot") {
                    name(Component.translatable("config.trident.rarity_slot.name"))
                    description(
                        OptionDescription.of(
                            Component.translatable("config.trident.rarity_slot.description")
                        )
                    )

                    lateinit var raritySlotDisplayType: Option<DisplayType>

                    options.register("rarity_slot_enabled") {
                        name(Component.translatable("config.trident.rarity_slot.name"))
                        description(
                            OptionDescription.createBuilder()
                                .text(Component.translatable("config.trident.rarity_slot.description"))
                                .image(
                                    Resources.trident(
                                        "textures/config/rarity_overlay.png"
                                    ), 120, 88
                                ).build()
                        )
                        binding(handler.instance()::raritySlotEnabled, false)
                        controller(tickBox())
                        addListener { option, event ->
                            if (event == OptionEventListener.Event.STATE_CHANGE) {
                                raritySlotDisplayType.setAvailable(option.pendingValue())
                            }
                        }
                    }

                    raritySlotDisplayType = options.register("rarity_slot_display_type") {
                        name(Component.translatable("config.trident.rarity_slot.display_type.name"))
                        description(
                            OptionDescription.createBuilder()
                                .text(Component.translatable("config.trident.rarity_slot.display_type.description"))
                                .customImage(RaritySlotPreview())
                                .build()
                        )
                        binding(handler.instance()::raritySlotDisplayType, DisplayType.OUTLINE)
                        controller(enumSwitch<DisplayType> { v -> v.displayName })
                        addListener { option, event ->
                            if (event != OptionEventListener.Event.STATE_CHANGE) return@addListener
                            RaritySlotPreview.RARITY_DISPLAY_TYPE = option.pendingValue()
                        }
                        available { handler.instance().raritySlotEnabled }
                    }
                }

                groups.register("games") {
                    name(Component.translatable("config.trident.games.name"))
                    description(OptionDescription.of(Component.translatable("config.trident.games.description")))

                    options.register("auto_focus") {
                        name(Component.translatable("config.trident.games.auto_focus.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.games.auto_focus.description")))
                        binding(handler.instance()::gamesAutoFocus, false)
                        controller(tickBox())
                    }
                }

                groups.register("killfeed") {
                    name(Component.translatable("config.trident.killfeed.name"))
                    description(OptionDescription.of(Component.translatable("config.trident.killfeed.description")))

                    lateinit var killfeedHideKills: Option<Boolean>
                    lateinit var killfeedShowKillStreaks: Option<Boolean>
                    lateinit var killfeedClearAfterRound: Option<Boolean>
                    lateinit var killfeedShowYouInKill: Option<Boolean>
                    lateinit var killfeedReverseOrder: Option<Boolean>
                    lateinit var killfeedPositionSide: Option<KillfeedPosition>
                    lateinit var killfeedPositionY: Option<Int>
                    lateinit var killfeedRemoveKillTime: Option<Int>
                    lateinit var killfeedMaxKills: Option<Int>


                    options.register("killfeed_enabled") {
                        name(Component.translatable("config.trident.killfeed.enabled.name"))
                        description(
                            OptionDescription.createBuilder()
                                .text(Component.translatable("config.trident.killfeed.enabled.description"))
                                .image(
                                    Resources.trident("textures/config/killfeed.png"), 618, 332
                                ).build()
                        )
                        binding(handler.instance()::killfeedEnabled, true)
                        controller(tickBox())
                        addListener { option, event ->
                            if (event == OptionEventListener.Event.STATE_CHANGE) {
                                killfeedHideKills.setAvailable(option.pendingValue())
                                killfeedShowKillStreaks.setAvailable(option.pendingValue())
                                killfeedClearAfterRound.setAvailable(option.pendingValue())
                                killfeedShowYouInKill.setAvailable(option.pendingValue())
                                killfeedReverseOrder.setAvailable(option.pendingValue())
                                killfeedPositionSide.setAvailable(option.pendingValue())
                                killfeedPositionY.setAvailable(option.pendingValue())
                                killfeedRemoveKillTime.setAvailable(option.pendingValue())
                                killfeedMaxKills.setAvailable(option.pendingValue())
                            }
                        }
                    }

                    killfeedHideKills = options.register("killfeed_hide_kills") {
                        name(Component.translatable("config.trident.killfeed.hide_kills.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.hide_kills.description")))
                        binding(handler.instance()::killfeedHideKills, false)
                        controller(tickBox())
                        available { handler.instance().killfeedEnabled }
                    }

                    killfeedShowKillStreaks = options.register("killfeed_show_kill_streaks") {
                        name(Component.translatable("config.trident.killfeed.show_kill_streaks.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.show_kill_streaks.description")))
                        binding(handler.instance()::killfeedShowKillStreaks, true)
                        controller(tickBox())
                        available { handler.instance().killfeedEnabled }
                    }

                    killfeedClearAfterRound = options.register("killfeed_clear_after_round") {
                        name(Component.translatable("config.trident.killfeed.clear_after_round.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.clear_after_round.description")))
                        binding(handler.instance()::killfeedClearAfterRound, true)
                        controller(tickBox())
                        available { handler.instance().killfeedEnabled }
                    }

                    killfeedShowYouInKill = options.register("killfeed_show_you_in_kill") {
                        name(Component.translatable("config.trident.killfeed.show_you_in_kill.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.show_you_in_kill.description")))
                        binding(handler.instance()::killfeedShowYouInKill, true)
                        controller(tickBox())
                        available { handler.instance().killfeedEnabled }
                    }

                    killfeedReverseOrder = options.register("killfeed_reverse_order") {
                        name(Component.translatable("config.trident.killfeed.reverse_order.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.reverse_order.description")))
                        binding(handler.instance()::killfeedReverseOrder, false)
                        controller(tickBox())
                        available { handler.instance().killfeedEnabled }
                    }

                    killfeedPositionSide = options.register("killfeed_position_side") {
                        name(Component.translatable("config.trident.killfeed.position_side.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.position_side.description")))
                        binding(handler.instance()::killfeedPositionSide, KillfeedPosition.RIGHT)
                        controller(enumSwitch<KillfeedPosition> { v -> v.displayName })
                        available { handler.instance().killfeedEnabled }
                    }

                    killfeedPositionY = options.register("killfeed_position_y") {
                        name(Component.translatable("config.trident.killfeed.position_y.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.position_y.description")))
                        binding(handler.instance()::killfeedPositionY, 20)
                        controller(
                            slider(
                                IntRange(0, 60),
                                1
                            ) { v -> Component.literal(v.toString() + "px") }
                        )
                        available { handler.instance().killfeedEnabled }
                    }

                    killfeedRemoveKillTime = options.register("killfeed_remove_kill_time") {
                        name(Component.translatable("config.trident.killfeed.remove_kill_time.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.remove_kill_time.description")))
                        binding(handler.instance()::killfeedRemoveKillTime, 10)
                        controller(
                            slider(
                                IntRange(0, 30),
                                1
                            ) { v -> Component.literal(if (v != 0) v.toString() + "s" else "Permanent") })
                        available { handler.instance().killfeedEnabled }
                    }

                    killfeedMaxKills = options.register("killfeed_max_kills") {
                        name(Component.translatable("config.trident.killfeed.max_kills.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.killfeed.max_kills.description")))
                        binding(handler.instance()::killfeedMaxKills, 5)
                        controller(slider(IntRange(1, 10), 1))
                        available { handler.instance().killfeedEnabled }
                    }
                }

                groups.register("questing") {
                    name(Component.translatable("config.trident.questing.name"))
                    description(OptionDescription.of(Component.translatable("config.trident.questing.description")))

                    lateinit var questingRarityColorName: Option<Boolean>
                    lateinit var questingShowInLobby: Option<Boolean>
                    lateinit var questingShowLeft: Option<Boolean>
                    lateinit var questingHideIfNoQuests: Option<Boolean>

                    options.register("questing_enabled") {
                        name(Component.translatable("config.trident.questing.enabled.name"))
                        description(
                            OptionDescription.createBuilder()
                                .text(Component.translatable("config.trident.questing.enabled.description"))
                                .image(
                                    Resources.trident("textures/config/questing.png"), 414, 338
                                ).build()
                        )
                        binding(handler.instance()::questingEnabled, true)
                        controller(tickBox())
                        addListener { option, event ->
                            if (event == OptionEventListener.Event.STATE_CHANGE) {
                                questingRarityColorName.setAvailable(option.pendingValue())
                                questingShowInLobby.setAvailable(option.pendingValue())
                                questingShowLeft.setAvailable(option.pendingValue())
                                questingHideIfNoQuests.setAvailable(option.pendingValue())
                            }
                        }
                    }

                    questingRarityColorName = options.register("questing_rarity_color_name") {
                        name(Component.translatable("config.trident.questing.rarity_color_name.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.questing.rarity_color_name.description")))
                        binding(handler.instance()::questingRarityColorName, true)
                        controller(tickBox())
                        available { handler.instance().questingEnabled }
                    }

                    questingShowInLobby = options.register("questing_show_in_lobby") {
                        name(Component.translatable("config.trident.questing.show_in_lobby.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.questing.show_in_lobby.description")))
                        binding(handler.instance()::questingShowInLobby, true)
                        controller(tickBox())
                        available { handler.instance().questingEnabled }
                    }

                    questingShowLeft = options.register("questing_show_left") {
                        name(Component.translatable("config.trident.questing.show_left.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.questing.show_left.description")))
                        binding(handler.instance()::questingShowLeft, true)
                        controller(tickBox())
                        available { handler.instance().questingEnabled }
                    }

                    questingHideIfNoQuests = options.register("questing_hide_if_no_quests") {
                        name(Component.translatable("config.trident.questing.hide_if_no_quests.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.questing.hide_if_no_quests.description")))
                        binding(handler.instance()::questingHideIfNoQuests, false)
                        controller(tickBox())
                        available { handler.instance().questingEnabled }
                    }
                }

                groups.register("fishing") {
                    name(Component.translatable("config.trident.fishing.name"))
                    description(OptionDescription.of(Component.translatable("config.trident.fishing.description")))

                    lateinit var suppliesModuleDurability: Option<Boolean>

                    options.register("supplies_module") {
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

                    suppliesModuleDurability = options.register("supplies_module_durability") {
                        name(Component.translatable("config.trident.fishing.supplies_module.durability.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.fishing.supplies_module.durability.description")))
                        binding(
                            handler.instance()::fishingSuppliesModuleShowAugmentDurability,
                            false
                        )
                        controller(tickBox())
                        available { handler.instance().fishingSuppliesModule }

                    }

                    options.register("show_augment_status_in_interface") {
                        name(Component.translatable("config.trident.fishing.show_augment_status_in_interface.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.fishing.show_augment_status_in_interface.description")))
                        binding(
                            handler.instance()::fishingShowAugmentStatusInInterface,
                            true
                        )
                        controller(tickBox())
                    }

                    options.register("flash_if_depleted") {
                        name(Component.translatable("config.trident.fishing.flash_if_depleted.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.fishing.flash_if_depleted.description")))
                        binding(handler.instance()::fishingFlashIfDepleted, true)
                        controller(tickBox())
                    }

                    options.register("island_indicators") {
                        name(Component.translatable("config.trident.fishing.island_indicators.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.fishing.island_indicators.description")))
                        binding(handler.instance()::fishingIslandIndicators, true)
                        controller(tickBox())
                    }

                    lateinit var wayfinderModuleDisplayOption: Option<WayfinderModuleDisplay>

                    options.register("wayfinder_module") {
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

                    wayfinderModuleDisplayOption = options.register("wayfinder_module_display") {
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


            categories.register("debug") {
                name(Component.translatable("config.trident.debug"))

                groups.register("debug") {
                    name(Component.translatable("config.trident.debug"))
                    description(OptionDescription.of(Component.translatable("config.trident.debug.description")))

                    options.register("logging") {
                        name(Component.translatable("config.trident.debug.enable_logging.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.debug.enable_logging.description")))
                        binding(handler.instance()::debugEnableLogging, false)
                        controller(tickBox())
                    }

                    options.register("draw_slot_number") {
                        name(Component.translatable("config.trident.debug.draw_slot_number.name"))
                        description(OptionDescription.of(Component.translatable("config.trident.debug.draw_slot_number.description")))
                        binding(handler.instance()::debugDrawSlotNumber, false)
                        controller(tickBox())
                    }

                    options.register("bypass_on_island") {
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