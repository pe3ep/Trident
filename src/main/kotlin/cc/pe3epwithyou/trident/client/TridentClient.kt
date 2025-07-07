package cc.pe3epwithyou.trident.client

import cc.pe3epwithyou.trident.dialogs.SettingsDialog
import cc.pe3epwithyou.trident.dialogs.SuppliesDialog
import cc.pe3epwithyou.trident.dialogs.TestDialog
import cc.pe3epwithyou.trident.state.PlayerState
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.state.fishing.AUGMENT_NAMES
import cc.pe3epwithyou.trident.utils.ItemParser
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.utils.TridentFont
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.noxcrew.sheeplib.DialogContainer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.Slot

class TridentClient : ClientModInitializer {
    companion object { val playerState = PlayerState() }

    private val debugDialogs = mapOf(
        "supplies" to ::SuppliesDialog,
        "settings" to ::SettingsDialog,
        "test" to ::TestDialog
    )

    companion object {
        fun onMCCIJoin() {
            Minecraft.getInstance().gui.chat.addMessage(Component.literal("You've joined MCC Island!"))
        }
    }

    private val DEBUG_COMMANDS: LiteralArgumentBuilder<FabricClientCommandSource> = ClientCommandManager.literal("trident")
        .then(ClientCommandManager.literal("open").then(
            ClientCommandManager.argument("dialog", StringArgumentType.string())
                .suggests { _, builder ->
                    debugDialogs.keys.forEach(builder::suggest)
                    builder.buildFuture()
                }
                .executes {
                    if (!MCCIslandState.isOnIsland()) {
                        Minecraft.getInstance().gui.chat.addMessage(TridentFont.tridentPrefix().append(
                            Component.literal("You are not currently playing MCC Island").withStyle(ChatFormatting.RED)
                        ))
                        return@executes 0
                    }
                    debugDialogs[it.getArgument("dialog", String::class.java)]?.let {
                        DialogContainer += it(10, 10)
                    }
                    0
                }
        )).then(ClientCommandManager.literal("settings").executes {
            if (!MCCIslandState.isOnIsland()) {
                Minecraft.getInstance().gui.chat.addMessage(TridentFont.tridentPrefix().append(
                    Component.literal("You are not currently playing MCC Island").withStyle(ChatFormatting.RED)
                ))
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
    }

        // SCREEN EVENT
        ScreenEvents.AFTER_INIT.register { client, screen: Screen, _, _ ->
            // check the screen is a chest
            if (screen is ContainerScreen) {
                // check it's the supplies menu
                if (screen.title.string.contains("FISHING SUPPLIES")) {
                    // get supplies info and add to state
                    client.execute {
                        Minecraft.getInstance().gui.chat.addMessage(Component.literal("Opened menu: ${screen.title.string}"))

                        // supplies
                        val bait = screen.menu.slots[19]
                        val baitLore = ItemParser().getLore(bait.item)
                        if (!bait.item.displayName.string.contains("Empty Bait Slot")) {
                            val baitCount = baitLore?.get(15)?.string?.split(" ")[2]?.replace(",", "")?.toInt()
                            playerState.supplies.bait.amount = baitCount
                            when(bait.item.displayName.string.split(" ")[0] ) {
                                "Common" -> { playerState.supplies.bait.type = Rarity.COMMON }
                                "Uncommon" -> { playerState.supplies.bait.type = Rarity.UNCOMMON }
                                "Rare" -> { playerState.supplies.bait.type = Rarity.RARE }
                                "Epic" -> { playerState.supplies.bait.type = Rarity.EPIC }
                                "Legendary" -> { playerState.supplies.bait.type = Rarity.LEGENDARY }
                                "Mythic" -> { playerState.supplies.bait.type = Rarity.MYTHIC }
                            }
                        } else {
                            playerState.supplies.bait.type = null
                            playerState.supplies.bait.amount = null
                        }

                        val line = screen.menu.slots[37]
                        val lineLore = ItemParser().getLore(line.item)
                        val lineUses = lineLore?.get(15)?.string?.split(" ")[2]?.split("/")[0]?.replace(",", "")?.toInt()
                        playerState.supplies.line.uses = lineUses
                        when(bait.item.displayName.string.split(" ")[0] ) {
                            "Common" -> { playerState.supplies.line.type = Rarity.COMMON }
                            "Uncommon" -> { playerState.supplies.line.type = Rarity.UNCOMMON }
                            "Rare" -> { playerState.supplies.line.type = Rarity.RARE }
                            "Epic" -> { playerState.supplies.line.type = Rarity.EPIC }
                            "Legendary" -> { playerState.supplies.line.type = Rarity.LEGENDARY }
                            "Mythic" -> { playerState.supplies.line.type = Rarity.MYTHIC }
                        }

                        // augments
                        val augments = listOf<String>(
                            screen.menu.slots[30].item.displayName.string,
                            screen.menu.slots[31].item.displayName.string,
                            screen.menu.slots[32].item.displayName.string,
                            screen.menu.slots[33].item.displayName.string,
                            screen.menu.slots[34].item.displayName.string,
                            screen.menu.slots[39].item.displayName.string,
                            screen.menu.slots[40].item.displayName.string,
                            screen.menu.slots[41].item.displayName.string,
                            screen.menu.slots[42].item.displayName.string,
                            screen.menu.slots[43].item.displayName.string,
                        )
                        playerState.supplies.augments = augments.map { AUGMENT_NAMES.getValue(it
                            .replace("A.N.G.L.R. ", "")
                            .replace("[", "")
                            .replace("]", "")
                        ) }

                        // overclocks
                        val hookOverclock = screen.menu.slots[12]
                        val hookLore = ItemParser().getActiveOverclock(hookOverclock.item)


                        val magnetkOverclock = screen.menu.slots[13]
                        val rodOverclock = screen.menu.slots[14]
                        val unstableOverclock = screen.menu.slots[15]
                    }
                }
            }
        }
    }
}
