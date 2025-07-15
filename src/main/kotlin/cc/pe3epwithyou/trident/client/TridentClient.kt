package cc.pe3epwithyou.trident.client

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.client.events.ChatEventListener
import cc.pe3epwithyou.trident.client.events.ChestScreenListener
import cc.pe3epwithyou.trident.dialogs.SettingsDialog
import cc.pe3epwithyou.trident.dialogs.SuppliesDialog
import cc.pe3epwithyou.trident.dialogs.TestDialog
import cc.pe3epwithyou.trident.state.PlayerState
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.state.fishing.AUGMENT_NAMES
import cc.pe3epwithyou.trident.utils.ItemParser
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.TimerUtil
import cc.pe3epwithyou.trident.utils.TridentFont
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.noxcrew.sheeplib.DialogContainer
import com.noxcrew.sheeplib.dialog.Dialog
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.network.chat.Component

class TridentClient : ClientModInitializer {

    private val debugDialogs = mapOf(
        "supplies" to ::SuppliesDialog,
        "settings" to ::SettingsDialog,
        "test" to ::TestDialog
    )

    companion object {
        fun onMCCIJoin() {
            ChatUtils.info("Player has joined MCC Island")
        }
        val playerState = PlayerState()
        val openedDialogs = hashMapOf<String, Dialog>()
        const val DEBUG_MODE = false
    }
    private val DEBUG_COMMANDS: LiteralArgumentBuilder<FabricClientCommandSource> = ClientCommandManager.literal("trident")
        .then(ClientCommandManager.literal("open").then(
            ClientCommandManager.argument("dialog", StringArgumentType.string())
                .suggests { _, builder ->
                    debugDialogs.keys.forEach(builder::suggest)
                    builder.buildFuture()
                }
                .executes { ctx ->
                    if (!DEBUG_MODE && !MCCIslandState.isOnIsland()) {
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
        )).then(ClientCommandManager.literal("settings").executes {
            if (!DEBUG_MODE && !MCCIslandState.isOnIsland()) {
                ChatUtils.sendMessage(Component.literal("You are not currently playing on MCC Island").withColor(TridentFont.TRIDENT_COLOR))
                return@executes 0
            }
            val dialog = SettingsDialog(10, 100)
            DialogContainer += dialog
            val screenWidth = Minecraft.getInstance().screen?.width!!
            dialog.x = (screenWidth - dialog.width) / 2
            0
        })

    override fun onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(DEBUG_COMMANDS)
        }

        ChatEventListener().register()
        ChestScreenListener().register()
        TimerUtil.register()

    }
}
