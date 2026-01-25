package cc.pe3epwithyou.trident.config.groups

import cc.pe3epwithyou.trident.config.Config.Companion.handler
import cc.pe3epwithyou.trident.config.screen.RaritySlotPreview
import cc.pe3epwithyou.trident.feature.ChatSwitcherButtons
import cc.pe3epwithyou.trident.feature.api.ApiProvider
import cc.pe3epwithyou.trident.feature.rarityslot.DisplayType
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemes
import cc.pe3epwithyou.trident.utils.Resources
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionEventListener
import dev.isxander.yacl3.dsl.*
import net.minecraft.network.chat.Component

fun generalCategory(categoriesRegistrar: CategoryRegistrar) {
    categoriesRegistrar.register("general") {
        name(Component.translatable("config.trident.global.name"))

        lateinit var apiOption: Option<ApiProvider>

        rootOptions.register("call_to_home") {
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

        apiOption = rootOptions.register("api_provider") {
            name(Component.translatable("config.trident.global.api_provider.name"))
            description(OptionDescription.of(Component.translatable("config.trident.global.api_provider.description")))
            binding(handler.instance()::globalApiProvider, ApiProvider.TRIDENT)
            controller(enumSwitch<ApiProvider> { v -> v.displayName })
            available { handler.instance().globalCallToHome }
        }

        rootOptions.register("theme") {
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

        rootOptions.register("exchange_improvements") {
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

        rootOptions.register("chat_channel_buttons") {
            name(Component.translatable("config.trident.global.chat_channel_buttons.name"))
            description(
                OptionDescription.of(Component.translatable("config.trident.global.chat_channel_buttons.description"))
            )
            binding(handler.instance()::globalChatChannelButtons, false)
            controller(tickBox())
            available(ChatSwitcherButtons.checkCompatibility())
        }

        rootOptions.register("auto_focus") {
            name(Component.translatable("config.trident.games.auto_focus.name"))
            description(OptionDescription.of(Component.translatable("config.trident.games.auto_focus.description")))
            binding(handler.instance()::gamesAutoFocus, false)
            controller(tickBox())
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
    }
}