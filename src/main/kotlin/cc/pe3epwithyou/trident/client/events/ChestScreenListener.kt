package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.client.TridentClient.Companion.playerState
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.dialogs.DialogCollection
import cc.pe3epwithyou.trident.dialogs.questing.QuestingDialog
import cc.pe3epwithyou.trident.feature.questing.Quest
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.feature.questing.QuestingParser
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.getAugmentByName
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.ItemParser
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.core.component.DataComponents

object ChestScreenListener {

    private fun parseRarity(name: String): Rarity = when {
        "Common" in name    -> Rarity.COMMON
        "Uncommon" in name  -> Rarity.UNCOMMON
        "Rare" in name      -> Rarity.RARE
        "Epic" in name      -> Rarity.EPIC
        "Legendary" in name -> Rarity.LEGENDARY
        "Mythic" in name    -> Rarity.MYTHIC
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
        val baitLore = ItemParser.getLore(baitSlot.item)

        if (!baitItemName.contains("Empty Bait Slot")) {
            val baitCount = baitLore.getOrNull(15)?.string
                ?.split(" ")
                ?.getOrNull(2)
                ?.replace(",", "")
                ?.toIntOrNull()

            playerState.supplies.bait.amount = baitCount
            ChatUtils.debugLog("Bait found - ${playerState.supplies.bait.amount}")

            val baitRarityName = baitItemName.split(" ").firstOrNull()
            playerState.supplies.bait.type = parseRarity(baitRarityName ?: "")
        } else {
            playerState.supplies.bait.type = Rarity.COMMON
            playerState.supplies.bait.amount = null
        }

        // Process line slot (slot 37)
        val lineSlot = screen.menu.slots[37]
        val lineItemName = lineSlot.item.displayName.string

        if (lineItemName.contains("Empty Line Slot")) {
            playerState.supplies.line.uses = null
            playerState.supplies.line.type = Rarity.COMMON
        } else {
            val lineLore = ItemParser.getLore(lineSlot.item)
            val lineUses = lineLore.getOrNull(15)?.string
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
        val augmentsRaw = augmentSlotsIndices.map { screen.menu.slots[it].item.displayName.string } as MutableList

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
                    getAugmentByName(cleanedName)
                }
            }
        } as MutableList<Augment>
        ChatUtils.debugLog("""
            Augments: ${playerState.supplies.augments}
        """.trimIndent())
        playerState.supplies.augmentsAvailable = availableSlots
        playerState.supplies.baitDesynced = false
        playerState.supplies.needsUpdating = false

        // Overclocks (slots 12-15)
        val hookOverclock = screen.menu.slots[12]
        playerState.supplies.overclocks.hook = ItemParser.getActiveOverclock(hookOverclock.item)

        val magnetOverclock = screen.menu.slots[13]
        playerState.supplies.overclocks.magnet = ItemParser.getActiveOverclock(magnetOverclock.item)

        val rodOverclock = screen.menu.slots[14]
        playerState.supplies.overclocks.rod = ItemParser.getActiveOverclock(rodOverclock.item)

        val unstableOverclock = screen.menu.slots[15]
        val unstableModel = unstableOverclock.item.components[DataComponents.ITEM_MODEL]
        if (unstableModel != null) {
            playerState.supplies.overclocks.unstable.isAvailable = !unstableModel.path.startsWith("island_interface/locked")
        }
        playerState.supplies.overclocks.unstable.texture = ItemParser.getUnstableOverclock(unstableOverclock.item)

        val supremeOverclock = screen.menu.slots[16]
        val supremeModel = supremeOverclock.item.components[DataComponents.ITEM_MODEL]
        if (supremeModel != null) {
            playerState.supplies.overclocks.supreme.isAvailable = !supremeModel.path.startsWith("island_interface/locked")
        }
        // Refresh supplies dialog if open
        DialogCollection.refreshDialog("supplies")
    }
}