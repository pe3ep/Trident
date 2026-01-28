package cc.pe3epwithyou.trident.config.groups

import cc.pe3epwithyou.trident.config.Config.Companion.handler
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionEventListener
import dev.isxander.yacl3.dsl.CategoryRegistrar
import dev.isxander.yacl3.dsl.available
import dev.isxander.yacl3.dsl.binding
import dev.isxander.yacl3.dsl.tickBox
import net.minecraft.network.chat.Component

fun discordCategory(categoryRegistrar: CategoryRegistrar) {
    categoryRegistrar.register("discord") {
        name(Component.translatable("config.trident.discord"))

        lateinit var privateMode: Option<Boolean>
        lateinit var autoPrivateMode: Option<Boolean>
        lateinit var extraInfo: Option<Boolean>
        lateinit var displayParty: Option<Boolean>

        rootOptions.register("discord_enabled") {
            name(Component.translatable("config.trident.discord.enabled.name"))
            description(OptionDescription.of(Component.translatable("config.trident.discord.enabled.description")))
            binding(handler.instance()::discordEnabled, true)
            controller(tickBox())
            addListener { option, event ->
                if (event == OptionEventListener.Event.STATE_CHANGE) {
                    privateMode.setAvailable(option.pendingValue())
                    extraInfo.setAvailable(option.pendingValue() && !privateMode.pendingValue())
                    displayParty.setAvailable(option.pendingValue() && !privateMode.pendingValue())
                    autoPrivateMode.setAvailable(option.pendingValue() && !privateMode.pendingValue())
                }
            }
        }

        privateMode = rootOptions.register("discord_private_mode") {
            name(Component.translatable("config.trident.discord.private_mode.name"))
            description(OptionDescription.of(Component.translatable("config.trident.discord.private_mode.description")))
            binding(handler.instance()::discordPrivateMode, false)
            controller(tickBox())
            addListener { option, event ->
                if (event == OptionEventListener.Event.STATE_CHANGE) {
                    extraInfo.setAvailable(!option.pendingValue())
                    displayParty.setAvailable(!option.pendingValue())
                    autoPrivateMode.setAvailable(!option.pendingValue())
                }
            }
            available { handler.instance().discordEnabled }
        }

        autoPrivateMode = rootOptions.register("discord_auto_private_mode") {
            name(Component.translatable("config.trident.discord.auto_private_mode.name"))
            description(OptionDescription.of(Component.translatable("config.trident.discord.auto_private_mode.description")))
            binding(handler.instance()::discordAutoPrivateMode, false)
            controller(tickBox())
            available { handler.instance().discordEnabled && !handler.instance().discordPrivateMode }
        }

        extraInfo = rootOptions.register("discord_display_extra_info") {
            name(Component.translatable("config.trident.discord.display_extra_info.name"))
            description(OptionDescription.of(Component.translatable("config.trident.discord.display_extra_info.description")))
            binding(handler.instance()::discordDisplayExtraInfo, true)
            controller(tickBox())
            available { handler.instance().discordEnabled && !handler.instance().discordPrivateMode }
        }

        displayParty = rootOptions.register("discord_display_party") {
            name(Component.translatable("config.trident.discord.display_party.name"))
            description(OptionDescription.of(Component.translatable("config.trident.discord.display_party.description")))
            binding(handler.instance()::discordDisplayParty, true)
            controller(tickBox())
            available { handler.instance().discordEnabled && !handler.instance().discordPrivateMode }
        }
    }
}