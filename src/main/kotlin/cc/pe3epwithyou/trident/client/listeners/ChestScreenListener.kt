package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.questing.Quest
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.feature.questing.QuestingParser
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.questing.QuestingDialog
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.getAugmentByName
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.ItemParser
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.core.component.DataComponents

object ChestScreenListener {

    private fun parseRarity(name: String): Rarity = when {
        "Common" in name -> Rarity.COMMON
        "Uncommon" in name -> Rarity.UNCOMMON
        "Rare" in name -> Rarity.RARE
        "Epic" in name -> Rarity.EPIC
        "Legendary" in name -> Rarity.LEGENDARY
        "Mythic" in name -> Rarity.MYTHIC
        else -> Rarity.COMMON
    }

    private fun handleScreen(screen: ContainerScreen) {
        if ("FISHING SUPPLIES" in screen.title.string) {
            DelayedAction.delayTicks(2L) {
                findAugments(screen)
            }
        }
        if ("ISLAND REWARDS" in screen.title.string) {
            DelayedAction.delayTicks(2L) {
                findQuests(screen)
            }
        }
        if ("FISHING ISLANDS" in screen.title.string) {
            DelayedAction.delayTicks(2L) {
                findWayfinderData(screen)
            }
        }

        ChatUtils.debugLog("Screen title: " + screen.title.string)
    }

    fun register() {
        ScreenEvents.AFTER_INIT.register { _, screen: Screen, _, _ ->
            if (screen is ContainerScreen) handleScreen(screen)
        }
    }

    fun findQuests(screen: ContainerScreen) {
        /**
         * 37 - daily
         * 39 - weekly
         * 41 - scroll
         */
//        if (!Config.Fishing.suppliesModule) return
        if ("ISLAND REWARDS" !in screen.title.string) return
        val quests = mutableListOf<Quest>()

        val dailySlot = screen.menu.slots[37]
        val dailyQuests = QuestingParser.parseQuestSlot(dailySlot)
        quests.addAll(dailyQuests ?: emptyList())
        QuestStorage.dailyRemaining =
            QuestingParser.parseRemainingSlot(screen.menu.slots[28])

        val weeklySlot = screen.menu.slots[39]
        val weeklyQuests = QuestingParser.parseQuestSlot(weeklySlot)
        quests.addAll(weeklyQuests ?: emptyList())
        QuestStorage.weeklyRemaining =
            QuestingParser.parseRemainingSlot(screen.menu.slots[30])

        val scrollSlot = screen.menu.slots[41]
        val scrollQuests = QuestingParser.parseQuestSlot(scrollSlot)
        quests.addAll(scrollQuests ?: emptyList())

        QuestingDialog.isDesynced = false
        QuestStorage.loadQuests(quests)
    }

    fun findAugments(screen: ContainerScreen) {
        if (!Config.Fishing.suppliesModule) return
        if ("FISHING SUPPLIES" !in screen.title.string) return

        // Process bait slot (slot 19)
        val baitSlot = screen.menu.slots[19]
        val baitItemName = baitSlot.item.displayName.string
        val baitLore = baitSlot.item.getLore()

        if (!baitItemName.contains("Empty Bait Slot")) {
            val baitCount = baitLore.getOrNull(15)?.string
                ?.split(" ")
                ?.getOrNull(2)
                ?.replace(",", "")
                ?.toIntOrNull()

            TridentClient.playerState.supplies.bait.amount = baitCount
            ChatUtils.debugLog("Bait found - ${TridentClient.playerState.supplies.bait.amount}")

            val baitRarityName = baitItemName.split(" ").firstOrNull()
            TridentClient.playerState.supplies.bait.type = parseRarity(baitRarityName ?: "")
        } else {
            TridentClient.playerState.supplies.bait.type = Rarity.COMMON
            TridentClient.playerState.supplies.bait.amount = null
        }

        // Process line slot (slot 37)
        val lineSlot = screen.menu.slots[37]
        val lineItemName = lineSlot.item.displayName.string

        if (lineItemName.contains("Empty Line Slot")) {
            TridentClient.playerState.supplies.line.uses = null
            TridentClient.playerState.supplies.line.type = Rarity.COMMON
        } else {
            val lineLore = lineSlot.item.getLore()
            val lineUses = lineLore.getOrNull(15)?.string
                ?.split(" ")
                ?.getOrNull(2)
                ?.split("/")
                ?.getOrNull(0)
                ?.replace(",", "")
                ?.toIntOrNull()

            TridentClient.playerState.supplies.line.uses = lineUses

            val lineRarityName = lineItemName.split(" ").firstOrNull()
            TridentClient.playerState.supplies.line.type = parseRarity(lineRarityName ?: "")
        }

        // Process augments slots
        val augmentSlotsIndices = listOf(30, 31, 32, 33, 34, 39, 40, 41, 42, 43)
        val augmentsRaw = augmentSlotsIndices.map { screen.menu.slots[it].item.displayName.string } as MutableList

        var availableSlots = 10
        TridentClient.playerState.supplies.augments = augmentsRaw.mapNotNull { rawName ->
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
                    getAugmentByName(cleanedName)
                }
            }
        } as MutableList<Augment>
        ChatUtils.debugLog(
            """
            Augments: ${TridentClient.playerState.supplies.augments}
        """.trimIndent()
        )
        TridentClient.playerState.supplies.augmentsAvailable = availableSlots
        TridentClient.playerState.supplies.baitDesynced = false
        TridentClient.playerState.supplies.needsUpdating = false

        // Overclocks (slots 12-15)
        val hookOverclock = screen.menu.slots[12]
        TridentClient.playerState.supplies.overclocks.hook = ItemParser.getActiveOverclock(hookOverclock.item)

        val magnetOverclock = screen.menu.slots[13]
        TridentClient.playerState.supplies.overclocks.magnet = ItemParser.getActiveOverclock(magnetOverclock.item)

        val rodOverclock = screen.menu.slots[14]
        TridentClient.playerState.supplies.overclocks.rod = ItemParser.getActiveOverclock(rodOverclock.item)

        val unstableOverclock = screen.menu.slots[15]
        val unstableModel = unstableOverclock.item.components[DataComponents.ITEM_MODEL]
        if (unstableModel != null) {
            TridentClient.playerState.supplies.overclocks.unstable.state.isAvailable =
                !unstableModel.path.startsWith("island_interface/locked")
        }
        TridentClient.playerState.supplies.overclocks.unstable.texture =
            ItemParser.getUnstableOverclock(unstableOverclock.item)

        val supremeOverclock = screen.menu.slots[16]
        val supremeModel = supremeOverclock.item.components[DataComponents.ITEM_MODEL]
        if (supremeModel != null) {
            TridentClient.playerState.supplies.overclocks.supreme.state.isAvailable =
                !supremeModel.path.startsWith("island_interface/locked")
        }
        // Refresh supplies dialog if open
        DialogCollection.refreshDialog("supplies")
    }

    fun findWayfinderData(screen: ContainerScreen) {
        if ("FISHING ISLANDS" !in screen.title.string) return

        val temperateData = screen.menu.slots[24].item.getLore()[13].string.split(": ")[1].split("/")[0]
        val tropicalData = screen.menu.slots[33].item.getLore()[13].string.split(": ")[1].split("/")[0]
        val barrenData = screen.menu.slots[42].item.getLore()[13].string.split(": ")[1].split("/")[0]
        TridentClient.playerState.wayfinderData.temperate.data = temperateData.replace(",", "").toIntOrNull()!!
        TridentClient.playerState.wayfinderData.tropical.data = tropicalData.replace(",", "").toIntOrNull()!!
        TridentClient.playerState.wayfinderData.barren.data = barrenData.replace(",", "").toIntOrNull()!!
        TridentClient.playerState.wayfinderData.needsUpdating = false
    }
}