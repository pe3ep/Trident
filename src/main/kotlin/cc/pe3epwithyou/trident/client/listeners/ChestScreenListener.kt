package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.exchange.ExchangeHandler
import cc.pe3epwithyou.trident.feature.questing.Quest
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.feature.questing.QuestingParser
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.questing.QuestingDialog
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.state.Research
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.getAugmentByName
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.ItemParser
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.findInLore
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.safeGetLine
import cc.pe3epwithyou.trident.utils.extensions.StringExt.parseFormattedInt
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component

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
            DelayedAction.delayTicks(1L) {
                findAugments(screen)
            }
        }
        if ("ISLAND REWARDS" in screen.title.string) {
            DelayedAction.delayTicks(1L) {
                findQuests(screen)
            }
        }
        if ("FISHING ISLANDS" in screen.title.string) {
            DelayedAction.delayTicks(1L) {
                findWayfinderData(screen)
            }
        }
        if ("FISHING PROGRESS" in screen.title.string) {
            DelayedAction.delayTicks(1L) {
                findFishingResearch(screen)
            }
        }

        if ("ISLAND EXCHANGE" in screen.title.string) {
            DelayedAction.delayTicks(1L) {
                ExchangeHandler.handleScreen(screen)
            }
        }

        ChatUtils.debugLog("Screen title: " + screen.title.string)
    }

    fun register() {
        ScreenEvents.AFTER_INIT.register { _, screen: Screen, _, _ ->
            try {
                if (screen is ContainerScreen) handleScreen(screen)
            } catch (e: Exception) {
                ChatUtils.sendMessage(
                    Component.literal("Something went wrong when opening screen, please contact developers about this issue")
                        .withSwatch(TridentFont.ERROR)
                )

                ChatUtils.error("Something went wrong when opening screen, ${e.message}")
            }
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
        QuestStorage.dailyRemaining = QuestingParser.parseRemainingSlot(screen.menu.slots[28])

        val weeklySlot = screen.menu.slots[39]
        val weeklyQuests = QuestingParser.parseQuestSlot(weeklySlot)
        quests.addAll(weeklyQuests ?: emptyList())
        QuestStorage.weeklyRemaining = QuestingParser.parseRemainingSlot(screen.menu.slots[30])

        val scrollSlot = screen.menu.slots[41]
        val scrollQuests = QuestingParser.parseQuestSlot(scrollSlot)
        quests.addAll(scrollQuests ?: emptyList())

        QuestingDialog.dialogState = QuestingDialog.QuestingDialogState.NORMAL
        QuestStorage.loadQuests(quests)
    }

    fun findAugments(screen: ContainerScreen) {
        if (!Config.Fishing.suppliesModule) return
        if ("FISHING SUPPLIES" !in screen.title.string) return

        // Process bait slot (slot 19)
        val baitSlot = screen.menu.slots[19]
        val baitItemName = baitSlot.item.displayName.string

        if (!baitItemName.contains("Empty Bait Slot")) {
            val baitCount = baitSlot.item.safeGetLine(15)?.string?.split(" ")?.getOrNull(2)?.parseFormattedInt()

            Trident.playerState.supplies.bait.amount = baitCount
            ChatUtils.debugLog("Bait found - ${Trident.playerState.supplies.bait.amount}")

            val baitRarityName = baitItemName.split(" ").firstOrNull()
            Trident.playerState.supplies.bait.type = parseRarity(baitRarityName ?: "")
        } else {
            Trident.playerState.supplies.bait.type = Rarity.COMMON
            Trident.playerState.supplies.bait.amount = null
        }

        // Process line slot (slot 37)
        val lineSlot = screen.menu.slots[37]
        val lineItemName = lineSlot.item.displayName.string

        if (lineItemName.contains("Empty Line Slot")) {
            Trident.playerState.supplies.line.uses = null
            Trident.playerState.supplies.line.type = Rarity.COMMON
        } else {
//            val lineUses =
//                lineLore.getOrNull(15)?.string?.split(" ")?.getOrNull(2)?.split("/")?.getOrNull(0)?.replace(",", "")
//                    ?.toIntOrNull()
            val match = lineSlot.item.findInLore(Regex("""Uses Remaining: ((?:\d|,)+)/((?:\d|,)+)"""))
            var lineUses: Int? = null
            var lineAmount: Int? = null
            match?.groups?.let {
                lineUses = it[1]?.value?.parseFormattedInt()
                lineAmount = it[2]?.value?.parseFormattedInt()
            }

            Trident.playerState.supplies.line.uses = lineUses
            Trident.playerState.supplies.line.amount = lineAmount

            val lineRarityName = lineItemName.split(" ").firstOrNull()
            Trident.playerState.supplies.line.type = parseRarity(lineRarityName ?: "")
        }

        // Process augments slots
        val augmentSlotsIndices = listOf(30, 31, 32, 33, 34, 39, 40, 41, 42, 43)
        val augmentsRaw = augmentSlotsIndices.map { screen.menu.slots[it].item.displayName.string } as MutableList

        var availableSlots = 10
        Trident.playerState.supplies.augments = augmentsRaw.mapNotNull { rawName ->
            when {
                rawName.contains("Locked Supply Slot") -> {
                    availableSlots--
                    null
                }

                rawName.contains("Empty Supply Slot") -> null
                else -> {
                    val cleanedName = rawName.replace(Regex("""(A\.N\.G\.L\.R\.|\[|]|Augment)"""), "").trim()
                    getAugmentByName(cleanedName)
                }
            }
        } as MutableList<Augment>
        ChatUtils.debugLog(
            """
            Augments: ${Trident.playerState.supplies.augments}
        """.trimIndent()
        )
        Trident.playerState.supplies.augmentsAvailable = availableSlots
        Trident.playerState.supplies.baitDesynced = false
        Trident.playerState.supplies.needsUpdating = false

        // Overclocks (slots 12-15)
        val hookOverclock = screen.menu.slots[12]
        Trident.playerState.supplies.overclocks.hook = ItemParser.getActiveOverclock(hookOverclock.item)

        val magnetOverclock = screen.menu.slots[13]
        Trident.playerState.supplies.overclocks.magnet = ItemParser.getActiveOverclock(magnetOverclock.item)

        val rodOverclock = screen.menu.slots[14]
        Trident.playerState.supplies.overclocks.rod = ItemParser.getActiveOverclock(rodOverclock.item)

        val unstableOverclock = screen.menu.slots[15]
        val unstableModel = unstableOverclock.item.components[DataComponents.ITEM_MODEL]
        if (unstableModel != null) {
            Trident.playerState.supplies.overclocks.unstable.state.isAvailable =
                !unstableModel.path.startsWith("island_interface/locked")
        }
        Trident.playerState.supplies.overclocks.unstable.texture =
            ItemParser.getUnstableOverclock(unstableOverclock.item)

        val supremeOverclock = screen.menu.slots[16]
        val supremeModel = supremeOverclock.item.components[DataComponents.ITEM_MODEL]
        if (supremeModel != null) {
            Trident.playerState.supplies.overclocks.supreme.state.isAvailable =
                !supremeModel.path.startsWith("island_interface/locked")
        }
        // Refresh supplies dialog if open
        DialogCollection.refreshDialog("supplies")
    }

    // TODO: Use safer methods to get data from a slot
    fun findWayfinderData(screen: ContainerScreen) {
        if ("FISHING ISLANDS" !in screen.title.string) return

        // temperate
        val temperateDataLine = screen.menu.slots[24].item.safeGetLine(13)?.string
        if (temperateDataLine != null && temperateDataLine.contains("Wayfinder Data: ")) {
            val temperateData = temperateDataLine.split(": ")[1].split("/")[0].replace(",", "").toIntOrNull()!!
            Trident.playerState.wayfinderData.temperate.data = temperateData
            Trident.playerState.wayfinderData.temperate.unlocked = true
            if (temperateData >= 2000) Trident.playerState.wayfinderData.temperate.hasGrotto = true
        } else {
            Trident.playerState.wayfinderData.temperate.hasGrotto = true
        }

        // tropical
        val tropicalDataLine = screen.menu.slots[33].item.safeGetLine(13)?.string
        if (tropicalDataLine != null && tropicalDataLine.contains("Wayfinder Data: ")) {
            val tropicalData = tropicalDataLine.split(": ")[1].split("/")[0].replace(",", "").toIntOrNull()!!
            Trident.playerState.wayfinderData.tropical.data = tropicalData
            Trident.playerState.wayfinderData.tropical.unlocked = true
            if (tropicalData >= 2000) Trident.playerState.wayfinderData.tropical.hasGrotto = true
        } else {
            Trident.playerState.wayfinderData.tropical.hasGrotto = true
        }

        // barren
        val barrenDataLine = screen.menu.slots[42].item.safeGetLine(13)?.string
        if (barrenDataLine != null && barrenDataLine.contains("Wayfinder Data: ")) {
            val barrenData = barrenDataLine.split(": ")[1].split("/")[0].replace(",", "").toIntOrNull()!!
            Trident.playerState.wayfinderData.barren.data = barrenData
            Trident.playerState.wayfinderData.barren.unlocked = true
            if (barrenData >= 2000) Trident.playerState.wayfinderData.barren.hasGrotto = true
        } else {
            Trident.playerState.wayfinderData.barren.hasGrotto = true
        }

        Trident.playerState.wayfinderData.needsUpdating = false
        DialogCollection.refreshDialog("wayfinder")
    }

    fun findFishingResearch(screen: ContainerScreen) {
        if ("FISHING PROGRESS" !in screen.title.string) return

        // empty the list
        Trident.playerState.research.researchTypes = mutableListOf()

        val researchSlots = listOf(12, 13, 14, 15, 16)
        val researchTypes = mapOf(12 to "Strong", 13 to "Wise", 14 to "Glimmering", 15 to "Greedy", 16 to "Lucky")
        for (slot in researchSlots) {
            val tierLine = screen.menu.slots[slot].item.safeGetLine(0)?.string?.split("(")?.getOrNull(1) ?: continue
            val tierLineNoBrackets = tierLine.dropLast(2)
            val tier = tierLineNoBrackets.split("/")[0].replace(",", "").toIntOrNull()!!

            val progress = screen.menu.slots[slot].item.safeGetLine(4)?.string ?: continue
            if (progress.contains("Progress: ")) {
                val amount = progress.split(": ")[1].split("/")[0].replace(",", "").toIntOrNull()!!
                val total = progress.split(": ")[1].split("/")[1].replace(",", "").toIntOrNull()!!

                Trident.playerState.research.researchTypes.add(
                    researchSlots.indexOf(slot), Research(
                        researchTypes[slot] ?: "Strong", tier = tier, progressThroughTier = amount, totalForTier = total
                    )
                )
            }
        }

        Trident.playerState.research.needsUpdating = false
        DialogCollection.refreshDialog("research")
    }
}