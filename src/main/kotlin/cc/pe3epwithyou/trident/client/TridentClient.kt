package cc.pe3epwithyou.trident.client

import cc.pe3epwithyou.trident.dialogs.SettingsDialog
import cc.pe3epwithyou.trident.dialogs.SuppliesDialog
import cc.pe3epwithyou.trident.dialogs.TestDialog
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.noxcrew.sheeplib.DialogContainer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.Minecraft

class TridentClient : ClientModInitializer {
    private val debugDialogs = mapOf(
        "supplies" to ::SuppliesDialog,
        "settings" to ::SettingsDialog,
        "test" to ::TestDialog
    )

    private val DEBUG_COMMANDS: LiteralArgumentBuilder<FabricClientCommandSource> = ClientCommandManager.literal("trident")
        .then(ClientCommandManager.literal("open").then(
            ClientCommandManager.argument("dialog", StringArgumentType.string())
                .suggests { _, builder ->
                    debugDialogs.keys.forEach(builder::suggest)
                    builder.buildFuture()
                }
                .executes {
                    debugDialogs[it.getArgument("dialog", String::class.java)]?.let {
                        DialogContainer += it(10, 10)
                    }
                    0
                }
        )).then(ClientCommandManager.literal("settings").executes {
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
    }
}
