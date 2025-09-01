package cc.pe3epwithyou.trident.client

import cc.pe3epwithyou.trident.client.events.QuestingEvents
import cc.pe3epwithyou.trident.client.listeners.ChatEventListener
import cc.pe3epwithyou.trident.client.listeners.ChestScreenListener
import cc.pe3epwithyou.trident.client.listeners.SpotEntityListener
import cc.pe3epwithyou.trident.client.listeners.KillChatListener
import cc.pe3epwithyou.trident.client.listeners.TideWindBossbarListener
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.fishing.DepletedDisplay
import cc.pe3epwithyou.trident.feature.fishing.SuppliesModuleTimer
import cc.pe3epwithyou.trident.feature.questing.QuestListener
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.fishing.SuppliesDialog
import cc.pe3epwithyou.trident.interfaces.questing.QuestingDialog
import cc.pe3epwithyou.trident.interfaces.fishing.UpgradesDialog
import cc.pe3epwithyou.trident.interfaces.fishing.ChanceDialog
import cc.pe3epwithyou.trident.interfaces.fishing.HookChanceDialog
import cc.pe3epwithyou.trident.interfaces.fishing.MagnetChanceDialog
import cc.pe3epwithyou.trident.interfaces.fishing.RodChanceDialog
import cc.pe3epwithyou.trident.interfaces.fishing.PotChanceDialog
import cc.pe3epwithyou.trident.interfaces.fishing.ChancePerksDialog
import cc.pe3epwithyou.trident.interfaces.fishing.SpotDialog
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.state.PlayerState
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.TridentFont
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.ChatFormatting
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW


class TridentClient : ClientModInitializer {

    private val debugDialogs = mapOf(
        "supplies" to ::SuppliesDialog,
        "questing" to ::QuestingDialog,
        "upgrades" to ::UpgradesDialog,
        "chances" to ::ChanceDialog,
        "hookchances" to ::HookChanceDialog,
        "magnetchances" to ::MagnetChanceDialog,
        "rodchances" to ::RodChanceDialog,
        "potchances" to ::PotChanceDialog,
        "chanceperks" to ::ChancePerksDialog,
        "spot" to ::SpotDialog,
    )

    companion object {
        val playerState = PlayerState()
        lateinit var settingsKeymapping: KeyMapping
        var jokeCooldown: Boolean = false
    }

    private val debugCommands: LiteralArgumentBuilder<FabricClientCommandSource> =
        ClientCommandManager.literal("trident")
            .then(
                ClientCommandManager.literal("open").then(
                    ClientCommandManager.argument("dialog", StringArgumentType.string())
                        .suggests { _, builder ->
                            debugDialogs.keys.forEach(builder::suggest)
                            builder.buildFuture()
                        }
                        .executes { ctx ->
                            if (!Config.Debug.enableLogging && !MCCIState.isOnIsland()) {
                                ChatUtils.sendMessage(
                                    Component.translatable("trident.not_island").withColor(TridentFont.TRIDENT_COLOR)
                                )
                                return@executes 0
                            }
                            debugDialogs[ctx.getArgument("dialog", String::class.java)]?.let {
                                val key = ctx.getArgument("dialog", String::class.java)
                                DialogCollection.open(key, it(10, 10, key))
                            }
                            0
                        }
                )).then(
                ClientCommandManager.literal("close").then(
                    ClientCommandManager.argument("dialog", StringArgumentType.string())
                        .suggests { _, builder ->
                            debugDialogs.keys.forEach(builder::suggest)
                            builder.buildFuture()
                        }
                        .executes { ctx ->
                            if (!Config.Debug.enableLogging && !MCCIState.isOnIsland()) {
                                ChatUtils.sendMessage(
                                    Component.translatable("trident.not_island").withColor(TridentFont.TRIDENT_COLOR)
                                )
                                return@executes 0
                            }
                            DialogCollection.close(ctx.getArgument("dialog", String::class.java))
                            0
                        }
                )).then(
                ClientCommandManager.literal("resetDialogPositions")
                    .executes { _ ->
                        DialogCollection.resetDialogPositions()
                        val c = Component.literal("Saved dialog positions have been ")
                            .withColor(TridentFont.TRIDENT_COLOR)
                            .append(
                                Component.literal("successfully reset")
                                    .withColor(TridentFont.TRIDENT_ACCENT)
                            )
                        ChatUtils.sendMessage(c, true)
                        0
                    }
            ).then(
                ClientCommandManager.literal("autofish")
                    .executes { _ ->
                        if (jokeCooldown) return@executes 0
                        jokeCooldown = true
                        ChatUtils.sendMessage("Requesting autofish.jar...")
                        DelayedAction.delayTicks(60L) {
                            ChatUtils.sendMessage("Received a response from the server")
                        }
                        DelayedAction.delayTicks(100L) {
                            ChatUtils.sendMessage("It states the following:")
                        }
                        DelayedAction.delayTicks(120L) {
                            ChatUtils.sendMessage(
                                Component.literal("Did you really just try to enable autofishing?")
                                    .withStyle(ChatFormatting.AQUA)
                            )
                        }
                        DelayedAction.delayTicks(180L) {
                            ChatUtils.sendMessage(
                                Component.literal("Are we serious right meow bro?")
                                    .withStyle(ChatFormatting.AQUA)
                            )
                        }
                        DelayedAction.delayTicks(240L) {
                            ChatUtils.sendMessage(
                                Component.literal("This incident will be reported.")
                                    .withStyle(ChatFormatting.DARK_RED)
                                    .withStyle(ChatFormatting.BOLD)
                            )
                            jokeCooldown = false
                        }
                        0
                    }
            )

    override fun onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(debugCommands)
        }

        settingsKeymapping = KeyBindingHelper.registerKeyBinding(
            KeyMapping(
                "key.trident.config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                "category.trident.keys"
            )
        )

        /* Convert deprecated config entries to their new counterpart */
        Config.convertDeprecated()

        ChatEventListener.register()
        ChestScreenListener.register()
        SpotEntityListener.register()
        DepletedDisplay.DepletedTimer.register()
        TideWindBossbarListener.register()
        SuppliesModuleTimer.register()
        KillChatListener.register()
        DelayedAction.init()
        QuestListener.register()

//        Register keybinding
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: Minecraft ->
            if (!MCCIState.isOnIsland()) return@EndTick
            if (!settingsKeymapping.consumeClick() || client.player == null) return@EndTick
            client.setScreen(Config.getScreen(client.screen))
        })

//        Register Questing events
        QuestingEvents.INCREMENT_ACTIVE.register { ctx ->
            QuestStorage.applyIncrement(ctx)
        }

        DialogCollection.loadAllDialogs()
    }
}
