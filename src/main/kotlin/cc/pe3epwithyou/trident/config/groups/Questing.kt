package cc.pe3epwithyou.trident.config.groups

import cc.pe3epwithyou.trident.config.Config.Companion.handler
import cc.pe3epwithyou.trident.utils.Resources
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionEventListener
import dev.isxander.yacl3.dsl.CategoryRegistrar
import dev.isxander.yacl3.dsl.available
import dev.isxander.yacl3.dsl.binding
import dev.isxander.yacl3.dsl.tickBox
import net.minecraft.network.chat.Component

fun questingCategory(categoryRegistrar: CategoryRegistrar) {
    categoryRegistrar.register("questing") {
        name(Component.translatable("config.trident.questing.name"))

        lateinit var questingRarityColorName: Option<Boolean>
        lateinit var questingShowInLobby: Option<Boolean>
        lateinit var questingShowLeft: Option<Boolean>
        lateinit var questingHideIfNoQuests: Option<Boolean>

        rootOptions.register("questing_enabled") {
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

        questingRarityColorName = rootOptions.register("questing_rarity_color_name") {
            name(Component.translatable("config.trident.questing.rarity_color_name.name"))
            description(OptionDescription.of(Component.translatable("config.trident.questing.rarity_color_name.description")))
            binding(handler.instance()::questingRarityColorName, true)
            controller(tickBox())
            available { handler.instance().questingEnabled }
        }

        questingShowInLobby = rootOptions.register("questing_show_in_lobby") {
            name(Component.translatable("config.trident.questing.show_in_lobby.name"))
            description(OptionDescription.of(Component.translatable("config.trident.questing.show_in_lobby.description")))
            binding(handler.instance()::questingShowInLobby, true)
            controller(tickBox())
            available { handler.instance().questingEnabled }
        }

        questingShowLeft = rootOptions.register("questing_show_left") {
            name(Component.translatable("config.trident.questing.show_left.name"))
            description(OptionDescription.of(Component.translatable("config.trident.questing.show_left.description")))
            binding(handler.instance()::questingShowLeft, true)
            controller(tickBox())
            available { handler.instance().questingEnabled }
        }

        questingHideIfNoQuests = rootOptions.register("questing_hide_if_no_quests") {
            name(Component.translatable("config.trident.questing.hide_if_no_quests.name"))
            description(OptionDescription.of(Component.translatable("config.trident.questing.hide_if_no_quests.description")))
            binding(handler.instance()::questingHideIfNoQuests, false)
            controller(tickBox())
            available { handler.instance().questingEnabled }
        }
    }
}