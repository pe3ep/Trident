package cc.pe3epwithyou.trident.client

import cc.pe3epwithyou.trident.client.events.ChatEventListener
import cc.pe3epwithyou.trident.client.events.ChestScreenListener
import cc.pe3epwithyou.trident.client.events.KillChatListener
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.dialogs.DebugDialog
import cc.pe3epwithyou.trident.dialogs.DialogCollection
import cc.pe3epwithyou.trident.dialogs.fishing.SuppliesDialog
import cc.pe3epwithyou.trident.dialogs.killfeed.KillFeedDialog
import cc.pe3epwithyou.trident.dialogs.killfeed.KillFeedSetup
import cc.pe3epwithyou.trident.dialogs.questing.QuestingDialog
import cc.pe3epwithyou.trident.feature.DepletedDisplay
import cc.pe3epwithyou.trident.feature.SupplyWidgetTimer
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.state.PlayerState
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.TimerUtil
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.WindowExtensions.focusWindowIfInactive
import cc.pe3epwithyou.trident.widgets.killfeed.KillMethod
import cc.pe3epwithyou.trident.widgets.killfeed.KillWidget
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.noxcrew.sheeplib.util.opacity
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW


class TridentClient : ClientModInitializer {

    private val debugDialogs = mapOf(
        "supplies" to ::SuppliesDialog,
        "questing" to ::QuestingDialog,
        "killfeed" to ::KillFeedDialog,
        "killsetup" to ::KillFeedSetup
    )

    companion object {
        val playerState = PlayerState()
        lateinit var settingsKeymapping: KeyMapping
    }
    private val DEBUG_COMMANDS: LiteralArgumentBuilder<FabricClientCommandSource> = ClientCommandManager.literal("trident")
        .then(ClientCommandManager.literal("open").then(
            ClientCommandManager.argument("dialog", StringArgumentType.string())
                .suggests { _, builder ->
                    debugDialogs.keys.forEach(builder::suggest)
                    builder.buildFuture()
                }
                .executes { ctx ->
                    if (!Config.Debug.enableLogging && !MCCIslandState.isOnIsland()) {
                        ChatUtils.sendMessage(Component.translatable("trident.not_island").withColor(TridentFont.TRIDENT_COLOR))
                        return@executes 0
                    }
                    debugDialogs[ctx.getArgument("dialog", String::class.java)]?.let {
                        val key = ctx.getArgument("dialog", String::class.java)
                        DialogCollection.open(key, it(10, 10, key))
                    }
                    0
                }
        )).then(ClientCommandManager.literal("close").then(
            ClientCommandManager.argument("dialog", StringArgumentType.string())
                .suggests { _, builder ->
                    debugDialogs.keys.forEach(builder::suggest)
                    builder.buildFuture()
                }
                .executes { ctx ->
                    if (!Config.Debug.enableLogging && !MCCIslandState.isOnIsland()) {
                        ChatUtils.sendMessage(Component.translatable("trident.not_island").withColor(TridentFont.TRIDENT_COLOR))
                        return@executes 0
                    }
                    DialogCollection.close(ctx.getArgument("dialog", String::class.java))
                    0
                }
        )).then(ClientCommandManager.literal("dialogTest").then(
            ClientCommandManager.argument("index", StringArgumentType.string())
                .executes { ctx ->
                    val prefix = ctx.getArgument("index", String::class.java)
                    val key = "${prefix}_testdialog"
                    DialogCollection.open(key, DebugDialog(10, 10, key))
                    0
                })
        ).then(ClientCommandManager.literal("fakeUnstableOverclock")
            .executes { _ ->
                SupplyWidgetTimer.INSTANCE.startUnstableOverclock()
                0
            }
        ).then(ClientCommandManager.literal("fakeSupremeOverclock")
            .executes { _ ->
                SupplyWidgetTimer.INSTANCE.startSupremeOverclock()
                0
            }
        ).then(ClientCommandManager.literal("resetDialogPositions")
            .executes { _ ->
                DialogCollection.resetDialogPositions()
                val c = Component.literal("Saved dialog positions have been ")
                    .withColor(TridentFont.TRIDENT_COLOR)
                    .append(Component.literal("successfully reset")
                        .withColor(TridentFont.TRIDENT_ACCENT)
                    )
                ChatUtils.sendMessage(c, true)
                0
            }
        ).then(ClientCommandManager.literal("addTestKill")
            .executes { _ ->
                val player = Minecraft.getInstance().player!!
                val w = KillWidget(
                    player.name.string,
                    KillMethod.MELEE,
                    killColors = Pair(0x606060 opacity 192, 0x808080 opacity 192)
                )
                KillFeedDialog.addKill(w)
                ChatUtils.sendMessage(Component.literal("Added fake kill"), true)
                0
            }
        )

    override fun onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(DEBUG_COMMANDS)
        }

        settingsKeymapping = KeyBindingHelper.registerKeyBinding(
            KeyMapping(
                "key.trident.config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                "category.trident.keys"
            )
        )

        ChatEventListener.register()
        ChestScreenListener.register()
        TimerUtil.register()
        DepletedDisplay.DepletedTimer.register()
        SupplyWidgetTimer.register()
        KillChatListener.register()
        DelayedAction.init()

//        Register keybinding
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: Minecraft ->
            if (!MCCIslandState.isOnIsland()) return@EndTick
            if (!settingsKeymapping.consumeClick() || client.player == null) return@EndTick
            client.setScreen(Config.getScreen(client.screen))
        })
    }
}
