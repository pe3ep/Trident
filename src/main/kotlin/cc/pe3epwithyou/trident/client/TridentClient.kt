package cc.pe3epwithyou.trident.client

import cc.pe3epwithyou.trident.client.events.FishingEventListener
import cc.pe3epwithyou.trident.client.events.ChestScreenListener
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.dialogs.SuppliesDialog
import cc.pe3epwithyou.trident.state.PlayerState
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.NoxesiumUtils
import cc.pe3epwithyou.trident.utils.TimerUtil
import cc.pe3epwithyou.trident.utils.TridentFont
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.noxcrew.sheeplib.DialogContainer
import com.noxcrew.sheeplib.dialog.Dialog
import dev.isxander.yacl3.api.OptionDescription
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class TridentClient : ClientModInitializer {

    private val debugDialogs = mapOf(
        "supplies" to ::SuppliesDialog,
    )

    companion object {
        fun onMCCIJoin() {
            ChatUtils.info("Player has joined MCC Island")
        }
        val playerState = PlayerState()
        val openedDialogs = hashMapOf<String, Dialog>()
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
                        ChatUtils.sendMessage(Component.literal("You are not currently playing on MCC Island").withColor(TridentFont.TRIDENT_COLOR))
                        return@executes 0
                    }
                    debugDialogs[ctx.getArgument("dialog", String::class.java)]?.let {
                        val d = it(10, 10)
                        openedDialogs.putIfAbsent(ctx.getArgument("dialog", String::class.java), d)
                        DialogContainer += d
                    }
                    0
                }
        ))

    override fun onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(DEBUG_COMMANDS)
        }

        FishingEventListener.register()
        ChestScreenListener.register()
        TimerUtil.register()

    }
}
