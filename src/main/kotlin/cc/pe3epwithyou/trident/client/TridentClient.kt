package cc.pe3epwithyou.trident.client

import cc.pe3epwithyou.trident.client.events.FishingEventListener
import cc.pe3epwithyou.trident.client.events.ChestScreenListener
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.dialogs.DebugDialog
import cc.pe3epwithyou.trident.dialogs.DialogCollection
import cc.pe3epwithyou.trident.dialogs.fishing.SuppliesDialog
import cc.pe3epwithyou.trident.feature.DepletedDisplay
import cc.pe3epwithyou.trident.state.PlayerState
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.TimerUtil
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.WindowExtensions.focusWindowIfInactive
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

class TridentClient : ClientModInitializer {

    private val debugDialogs = mapOf(
        "supplies" to ::SuppliesDialog,
    )

    companion object {
        val playerState = PlayerState()
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
        )).then(ClientCommandManager.literal("focusTest").executes {
            TimerUtil.INSTANCE.setTimer(60L) {
                Minecraft.getInstance().window.focusWindowIfInactive()
            }
            0
        }).then(ClientCommandManager.literal("dialogTest").then(
            ClientCommandManager.argument("index", StringArgumentType.string())
                .executes { ctx ->
                    val prefix = ctx.getArgument("index", String::class.java)
                    val key = "${prefix}_testdialog"
                    DialogCollection.open(key, DebugDialog(10, 10, key))
                    0
                })
        )

    override fun onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(DEBUG_COMMANDS)
        }

        FishingEventListener.register()
        ChestScreenListener.register()
        TimerUtil.register()
        DepletedDisplay.DepletedTimer.register()

    }
}
