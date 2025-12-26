package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.exchange.ExchangeHandler
import cc.pe3epwithyou.trident.feature.questing.Quest
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.feature.questing.QuestingParser
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.questing.QuestingDialog
import cc.pe3epwithyou.trident.state.AugmentContainer
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.state.Research
import cc.pe3epwithyou.trident.state.fishing.getAugmentContainer
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.ItemParser
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.findInLore
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.safeGetLine
import cc.pe3epwithyou.trident.utils.extensions.StringExt.parseFormattedInt
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack


// TODO: Refactor this whole object
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
            val baitCount =
                baitSlot.item.safeGetLine(15)?.string?.split(" ")?.getOrNull(2)?.parseFormattedInt()

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
//            val lineUses =
//                lineLore.getOrNull(15)?.string?.split(" ")?.getOrNull(2)?.split("/")?.getOrNull(0)?.replace(",", "")
//                    ?.toIntOrNull()
            val match =
                lineSlot.item.findInLore(Regex("""Uses Remaining: ((?:\d|,)+)/((?:\d|,)+)"""))
            var lineUses: Int? = null
            var lineAmount: Int? = null
            match?.groups?.let {
                lineUses = it[1]?.value?.parseFormattedInt()
                lineAmount = it[2]?.value?.parseFormattedInt()
            }

            TridentClient.playerState.supplies.line.uses = lineUses
            TridentClient.playerState.supplies.line.amount = lineAmount

            val lineRarityName = lineItemName.split(" ").firstOrNull()
            TridentClient.playerState.supplies.line.type = parseRarity(lineRarityName ?: "")
        }

        // Process augments slots
        val augmentSlotsIndices = listOf(30, 31, 32, 33, 34, 39, 40, 41, 42, 43)
        val augmentsRaw =
            augmentSlotsIndices.map { screen.menu.slots[it].item } as MutableList

        var availableSlots = 10
        TridentClient.playerState.supplies.augments = augmentsRaw.mapNotNull { rawName ->
            when {
                rawName.hoverName.string.contains("Locked Supply Slot") -> {
                    availableSlots--
                    null
                }

                rawName.hoverName.string.contains("Empty Supply Slot") -> null

                else -> {
                    val cleanedName =
                        rawName.hoverName.string.replace(Regex("""(A\.N\.G\.L\.R\.|\[|]|Augment)"""), "").trim()
                    getAugmentContainer(cleanedName, rawName.getLore().map { it.string })
                }
            }
        } as MutableList<AugmentContainer>
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
        TridentClient.playerState.supplies.overclocks.hook =
            ItemParser.getActiveOverclock(hookOverclock.item)

        val magnetOverclock = screen.menu.slots[13]
        TridentClient.playerState.supplies.overclocks.magnet =
            ItemParser.getActiveOverclock(magnetOverclock.item)

        val rodOverclock = screen.menu.slots[14]
        TridentClient.playerState.supplies.overclocks.rod =
            ItemParser.getActiveOverclock(rodOverclock.item)

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

    private fun handleWfdItemLine(item: ItemStack) {
        val line = item.safeGetLine(13)?.string
        if (line != null && line.contains("Wayfinder Data: ")) {
            val temperateData =
                line.split(": ")[1].split("/")[0].replace(",", "").toIntOrNull()!!
            TridentClient.playerState.wayfinderData.temperate.data = temperateData
            TridentClient.playerState.wayfinderData.temperate.unlocked = true
            if (temperateData >= 2000) TridentClient.playerState.wayfinderData.temperate.hasGrotto =
                true
        } else {
            TridentClient.playerState.wayfinderData.temperate.hasGrotto = true
        }
    }

    // TODO: Use safer methods to get data from a slot
    fun findWayfinderData(screen: ContainerScreen) {
        if ("FISHING ISLANDS" !in screen.title.string) return

        // temperate
        handleWfdItemLine(screen.menu.slots[24].item)
        // tropical
        handleWfdItemLine(screen.menu.slots[33].item)
        // barren
        handleWfdItemLine(screen.menu.slots[42].item)

        TridentClient.playerState.wayfinderData.needsUpdating = false
        DialogCollection.refreshDialog("wayfinder")
    }

    fun findFishingResearch(screen: ContainerScreen) {
        if ("FISHING PROGRESS" !in screen.title.string) return

        // empty the list
        TridentClient.playerState.research.researchTypes = mutableListOf()

        val researchSlots = listOf(12, 13, 14, 15, 16)
        val researchTypes =
            mapOf(12 to "Strong", 13 to "Wise", 14 to "Glimmering", 15 to "Greedy", 16 to "Lucky")
        for (slot in researchSlots) {
            val tierLine =
                screen.menu.slots[slot].item.safeGetLine(0)?.string?.split("(")?.getOrNull(1)
                    ?: continue
            val tierLineNoBrackets = tierLine.dropLast(2)
            val tier = tierLineNoBrackets.split("/")[0].replace(",", "").toIntOrNull()!!

            val progress = screen.menu.slots[slot].item.safeGetLine(4)?.string ?: continue
            if (progress.contains("Progress: ")) {
                val amount = progress.split(": ")[1].split("/")[0].replace(",", "").toIntOrNull()!!
                val total = progress.split(": ")[1].split("/")[1].replace(",", "").toIntOrNull()!!

                TridentClient.playerState.research.researchTypes.add(
                    researchSlots.indexOf(slot), Research(
                        researchTypes[slot] ?: "Strong",
                        tier = tier,
                        progressThroughTier = amount,
                        totalForTier = total
                    )
                )
            }
        }

        TridentClient.playerState.research.needsUpdating = false
        DialogCollection.refreshDialog("research")
    }
}