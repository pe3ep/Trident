package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.client.TridentClient.Companion.playerState
import cc.pe3epwithyou.trident.dialogs.SuppliesDialog
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.state.fishing.AUGMENT_NAMES
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.ItemParser
import cc.pe3epwithyou.trident.utils.TimerUtil
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen

object ChestScreenListener {
    private fun handleScreen(client: Minecraft, screen: ContainerScreen) {
        findAugments(screen)
    }

    fun register() {
        ScreenEvents.AFTER_INIT.register { client, screen: Screen, _, _ ->
            if (screen is ContainerScreen) handleScreen(client, screen)
        }
    }

    fun findAugments(screen: ContainerScreen) {
        if (screen.title.string.contains("FISHING SUPPLIES")) {
            // get supplies info and add to state
            TimerUtil.INSTANCE.setTimer(3) {
                ChatUtils.info("Opened menu: ${screen.title.string}")
                // supplies
                val bait = screen.menu.slots[19]
                val baitLore = ItemParser().getLore(bait.item)
                if (!bait.item.displayName.string.contains("Empty Bait Slot")) {
                    val baitCount = baitLore?.get(15)?.string?.split(" ")?.get(2)?.replace(",", "")?.toInt()
                    playerState.supplies.bait.amount = baitCount
                    ChatUtils.info("Bait found - ${playerState.supplies.bait.amount}")

                    when (bait.item.displayName.string.split(" ")[0]) {
                        "Common" -> {
                            playerState.supplies.bait.type = Rarity.COMMON
                        }

                        "Uncommon" -> {
                            playerState.supplies.bait.type = Rarity.UNCOMMON
                        }

                        "Rare" -> {
                            playerState.supplies.bait.type = Rarity.RARE
                        }

                        "Epic" -> {
                            playerState.supplies.bait.type = Rarity.EPIC
                        }

                        "Legendary" -> {
                            playerState.supplies.bait.type = Rarity.LEGENDARY
                        }

                        "Mythic" -> {
                            playerState.supplies.bait.type = Rarity.MYTHIC
                        }
                    }
                } else {
                    playerState.supplies.bait.type = null
                    playerState.supplies.bait.amount = null
                }

                val line = screen.menu.slots[37]

                if (line.item.displayName.string.contains("Empty Line Slot")) {
                    playerState.supplies.line.uses = null
                    playerState.supplies.line.type = null
                } else {
                    val lineLore = ItemParser().getLore(line.item)
                    val lineUses =
                        lineLore?.get(15)?.string?.split(" ")?.get(2)?.split("/")?.get(0)?.replace(",", "")?.toInt()
                    playerState.supplies.line.uses = lineUses
                    when (bait.item.displayName.string.split(" ")[0]) {
                        "Common" -> {
                            playerState.supplies.line.type = Rarity.COMMON
                        }

                        "Uncommon" -> {
                            playerState.supplies.line.type = Rarity.UNCOMMON
                        }

                        "Rare" -> {
                            playerState.supplies.line.type = Rarity.RARE
                        }

                        "Epic" -> {
                            playerState.supplies.line.type = Rarity.EPIC
                        }

                        "Legendary" -> {
                            playerState.supplies.line.type = Rarity.LEGENDARY
                        }

                        "Mythic" -> {
                            playerState.supplies.line.type = Rarity.MYTHIC
                        }
                    }

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
                var availableSlots = 10
                playerState.supplies.augments = augments.mapNotNull {
                    if (it.contains("Locked Supply Slot")) {
                        availableSlots--
                        return@mapNotNull null
                    }
                    if (it.contains("Empty Supply Slot")) {
                        return@mapNotNull null
                    }
                    AUGMENT_NAMES.getValue(
                        it
                            .replace("A.N.G.L.R. ", "")
                            .replace("[", "")
                            .replace("]", "")
                            .replace(" Augment", "")
                    )
                }
                playerState.supplies.augmentsAvailable = availableSlots
                playerState.supplies.updateRequired = false
                // Update supplies menu if it's opened
                (TridentClient.openedDialogs["supplies"] as SuppliesDialog?)?.refresh()

                // overclocks
                val hookOverclock = screen.menu.slots[12]
                val hookLore = ItemParser().getActiveOverclock(hookOverclock.item)


                val magnetOverclock = screen.menu.slots[13]
                val rodOverclock = screen.menu.slots[14]
                val unstableOverclock = screen.menu.slots[15]
            }
        }
    }
}