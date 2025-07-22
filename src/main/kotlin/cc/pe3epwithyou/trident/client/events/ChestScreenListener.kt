package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.client.TridentClient.Companion.playerState
import cc.pe3epwithyou.trident.dialogs.DialogCollection
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

    private fun parseRarity(name: String): Rarity? = when (name) {
        "Common" -> Rarity.COMMON
        "Uncommon" -> Rarity.UNCOMMON
        "Rare" -> Rarity.RARE
        "Epic" -> Rarity.EPIC
        "Legendary" -> Rarity.LEGENDARY
        "Mythic" -> Rarity.MYTHIC
        else -> null
    }

    private fun handleScreen(client: Minecraft, screen: ContainerScreen) {
        findAugments(screen)
    }

    fun register() {
        ScreenEvents.AFTER_INIT.register { client, screen: Screen, _, _ ->
            if (screen is ContainerScreen) handleScreen(client, screen)
        }
    }

    fun findAugments(screen: ContainerScreen) {
        if (!screen.title.string.contains("FISHING SUPPLIES")) return

        TimerUtil.INSTANCE.setTimer(3) {
            ChatUtils.info("Opened menu: ${screen.title.string}")

            // Process bait slot (slot 19)
            val baitSlot = screen.menu.slots[19]
            val baitItemName = baitSlot.item.displayName.string
            val baitLore = ItemParser.getLore(baitSlot.item)

            if (!baitItemName.contains("Empty Bait Slot")) {
                val baitCount = baitLore?.getOrNull(15)?.string
                    ?.split(" ")
                    ?.getOrNull(2)
                    ?.replace(",", "")
                    ?.toIntOrNull()

                playerState.supplies.bait.amount = baitCount
                ChatUtils.info("Bait found - ${playerState.supplies.bait.amount}")

                val baitRarityName = baitItemName.split(" ").firstOrNull()
                playerState.supplies.bait.type = parseRarity(baitRarityName ?: "")
            } else {
                playerState.supplies.bait.type = null
                playerState.supplies.bait.amount = null
            }

            // Process line slot (slot 37)
            val lineSlot = screen.menu.slots[37]
            val lineItemName = lineSlot.item.displayName.string

            if (lineItemName.contains("Empty Line Slot")) {
                playerState.supplies.line.uses = null
                playerState.supplies.line.type = null
            } else {
                val lineLore = ItemParser.getLore(lineSlot.item)
                val lineUses = lineLore?.getOrNull(15)?.string
                    ?.split(" ")
                    ?.getOrNull(2)
                    ?.split("/")
                    ?.getOrNull(0)
                    ?.replace(",", "")
                    ?.toIntOrNull()

                playerState.supplies.line.uses = lineUses

                val lineRarityName = lineItemName.split(" ").firstOrNull()
                playerState.supplies.line.type = parseRarity(lineRarityName ?: "")
            }

            // Process augments slots
            val augmentSlotsIndices = listOf(30, 31, 32, 33, 34, 39, 40, 41, 42, 43)
            val augmentsRaw = augmentSlotsIndices.map { screen.menu.slots[it].item.displayName.string }

            var availableSlots = 10
            playerState.supplies.augments = augmentsRaw.mapNotNull { rawName ->
                when {
                    rawName.contains("Locked Supply Slot") -> {
                        availableSlots--
                        null
                    }
                    rawName.contains("Empty Supply Slot") -> null
                    else -> {
                        val cleanedName = rawName
                            .replace("A.N.G.L.R. ", "")
                            .replace("[", "")
                            .replace("]", "")
                            .replace(" Augment", "")
                        AUGMENT_NAMES[cleanedName]
                    }
                }
            }
            playerState.supplies.augmentsAvailable = availableSlots
            playerState.supplies.updateRequired = false

            // Refresh supplies dialog if open
            DialogCollection.refreshDialog("supplies")

            // Overclocks (slots 12-15)
            val hookOverclock = screen.menu.slots[12]
            val hookLore = ItemParser.getActiveOverclock(hookOverclock.item)

            val magnetOverclock = screen.menu.slots[13]
            val rodOverclock = screen.menu.slots[14]
            val unstableOverclock = screen.menu.slots[15]

            // TODO: Process overclocks if needed
        }
    }
}