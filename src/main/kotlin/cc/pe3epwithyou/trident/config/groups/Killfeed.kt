package cc.pe3epwithyou.trident.config.groups

import cc.pe3epwithyou.trident.config.Config.Companion.handler
import cc.pe3epwithyou.trident.feature.killfeed.KillfeedPosition
import cc.pe3epwithyou.trident.utils.Resources
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionEventListener
import dev.isxander.yacl3.dsl.CategoryRegistrar
import dev.isxander.yacl3.dsl.available
import dev.isxander.yacl3.dsl.binding
import dev.isxander.yacl3.dsl.enumSwitch
import dev.isxander.yacl3.dsl.slider
import dev.isxander.yacl3.dsl.tickBox
import net.minecraft.network.chat.Component

fun killfeedCategory(categoryRegistrar: CategoryRegistrar) {
    categoryRegistrar.register("killfeed") {
        name(Component.translatable("config.trident.killfeed.name"))

        lateinit var killfeedHideKills: Option<Boolean>
        lateinit var killfeedShowKillStreaks: Option<Boolean>
        lateinit var killfeedClearAfterRound: Option<Boolean>
        lateinit var killfeedShowYouInKill: Option<Boolean>
        lateinit var killfeedReverseOrder: Option<Boolean>
        lateinit var killfeedPositionSide: Option<KillfeedPosition>
        lateinit var killfeedPositionY: Option<Int>
        lateinit var killfeedRemoveKillTime: Option<Int>
        lateinit var killfeedMaxKills: Option<Int>


        rootOptions.register("killfeed_enabled") {
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

        killfeedHideKills = rootOptions.register("killfeed_hide_kills") {
            name(Component.translatable("config.trident.killfeed.hide_kills.name"))
            description(OptionDescription.of(Component.translatable("config.trident.killfeed.hide_kills.description")))
            binding(handler.instance()::killfeedHideKills, false)
            controller(tickBox())
            available { handler.instance().killfeedEnabled }
        }

        killfeedShowKillStreaks = rootOptions.register("killfeed_show_kill_streaks") {
            name(Component.translatable("config.trident.killfeed.show_kill_streaks.name"))
            description(OptionDescription.of(Component.translatable("config.trident.killfeed.show_kill_streaks.description")))
            binding(handler.instance()::killfeedShowKillStreaks, true)
            controller(tickBox())
            available { handler.instance().killfeedEnabled }
        }

        killfeedClearAfterRound = rootOptions.register("killfeed_clear_after_round") {
            name(Component.translatable("config.trident.killfeed.clear_after_round.name"))
            description(OptionDescription.of(Component.translatable("config.trident.killfeed.clear_after_round.description")))
            binding(handler.instance()::killfeedClearAfterRound, true)
            controller(tickBox())
            available { handler.instance().killfeedEnabled }
        }

        killfeedShowYouInKill = rootOptions.register("killfeed_show_you_in_kill") {
            name(Component.translatable("config.trident.killfeed.show_you_in_kill.name"))
            description(OptionDescription.of(Component.translatable("config.trident.killfeed.show_you_in_kill.description")))
            binding(handler.instance()::killfeedShowYouInKill, true)
            controller(tickBox())
            available { handler.instance().killfeedEnabled }
        }

        killfeedReverseOrder = rootOptions.register("killfeed_reverse_order") {
            name(Component.translatable("config.trident.killfeed.reverse_order.name"))
            description(OptionDescription.of(Component.translatable("config.trident.killfeed.reverse_order.description")))
            binding(handler.instance()::killfeedReverseOrder, false)
            controller(tickBox())
            available { handler.instance().killfeedEnabled }
        }

        killfeedPositionSide = rootOptions.register("killfeed_position_side") {
            name(Component.translatable("config.trident.killfeed.position_side.name"))
            description(OptionDescription.of(Component.translatable("config.trident.killfeed.position_side.description")))
            binding(handler.instance()::killfeedPositionSide, KillfeedPosition.RIGHT)
            controller(enumSwitch<KillfeedPosition> { v -> v.displayName })
            available { handler.instance().killfeedEnabled }
        }

        killfeedPositionY = rootOptions.register("killfeed_position_y") {
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

        killfeedRemoveKillTime = rootOptions.register("killfeed_remove_kill_time") {
            name(Component.translatable("config.trident.killfeed.remove_kill_time.name"))
            description(OptionDescription.of(Component.translatable("config.trident.killfeed.remove_kill_time.description")))
            binding(handler.instance()::killfeedRemoveKillTime, 10)
            controller(
                slider(
                    IntRange(0, 30),
                    1
                ) { v -> Component.literal(if (v != 0) "${v}s" else "Permanent") })
            available { handler.instance().killfeedEnabled }
        }

        killfeedMaxKills = rootOptions.register("killfeed_max_kills") {
            name(Component.translatable("config.trident.killfeed.max_kills.name"))
            description(OptionDescription.of(Component.translatable("config.trident.killfeed.max_kills.description")))
            binding(handler.instance()::killfeedMaxKills, 5)
            controller(slider(IntRange(1, 10), 1))
            available { handler.instance().killfeedEnabled }
        }
    }
}