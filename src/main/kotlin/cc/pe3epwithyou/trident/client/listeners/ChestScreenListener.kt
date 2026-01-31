package cc.pe3epwithyou.trident.client.listeners

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.discord.ActivityManager
import cc.pe3epwithyou.trident.feature.exchange.ExchangeHandler
import cc.pe3epwithyou.trident.feature.questing.Quest
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.feature.questing.QuestingParser
import cc.pe3epwithyou.trident.feature.questing.lock.QuestLock
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.state.AugmentContainer
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.state.Research
import cc.pe3epwithyou.trident.state.fishing.getAugmentContainer
import cc.pe3epwithyou.trident.utils.ItemParser
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.findInLore
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.safeGetLine
import cc.pe3epwithyou.trident.utils.extensions.StringExt.parseFormattedInt
import cc.pe3epwithyou.trident.utils.extensions.WindowExtensions.focusWindowIfInactive
import cc.pe3epwithyou.trident.utils.useScreen
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.Minecraft
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
        if (!MCCIState.isOnIsland()) return
        val title = screen.title.string

        useScreen(screen) {
            checkName("FISHING SUPPLIES") { await { findAugments(it) } }
            checkName("ISLAND REWARDS") { await { findQuests(it) } }
            checkName("FISHING ISLANDS") { await { findWayfinderData(it) } }
            checkName("FISHING PROGRESS") { await { findFishingResearch(it) } }
            checkName("ISLAND EXCHANGE") { await { ExchangeHandler.handleScreen(it) } }
            checkName("MATCH FOUND! (0/") { await { Minecraft.getInstance().window.focusWindowIfInactive() } }
            checkName("BATTLE BOX ARENA") { await { ActivityManager.Arena.handleScreen(it) } }
        }

        Logger.debugLog("Screen title: $title")
    }

    fun register() {
        ScreenEvents.AFTER_INIT.register { _, screen: Screen, _, _ ->
            try {
                if (screen is ContainerScreen) handleScreen(screen)
            } catch (e: Exception) {
                Logger.sendMessage(
                    Component.literal("Something went wrong when opening screen, please contact developers about this issue")
                        .withSwatch(TridentFont.ERROR)
                )

                Logger.error("Something went wrong when opening screen, ${e.message}")
            }
        }
    }

    fun findQuests(screen: ContainerScreen) {
        /**
         * 37 - daily
         * 39 - weekly
         * 41 - scroll
         */
        if (!Config.Questing.enabled) return
        if ("ISLAND REWARDS" !in screen.title.string) return
        val slotQuests = mutableListOf<Quest>()

        val dailySlot = screen.menu.slots[37]
        val dailyQuests = QuestingParser.parseQuestSlot(dailySlot) ?: emptyList()
        slotQuests.addAll(dailyQuests)
        QuestLock.questSlots[37]?.apply {
            quests = dailyQuests
            isLocked = QuestLock.shouldLock(dailyQuests)
        }
        QuestStorage.dailyRemaining = QuestingParser.parseRemainingSlot(screen.menu.slots[28])

        val weeklySlot = screen.menu.slots[39]
        val weeklyQuests = QuestingParser.parseQuestSlot(weeklySlot) ?: emptyList()
        QuestLock.questSlots[39]?.apply {
            quests = weeklyQuests
            isLocked = QuestLock.shouldLock(weeklyQuests)
        }
        slotQuests.addAll(weeklyQuests)
        QuestStorage.weeklyRemaining = QuestingParser.parseRemainingSlot(screen.menu.slots[30])

        val scrollSlot = screen.menu.slots[41]
        val scrollQuests = QuestingParser.parseQuestSlot(scrollSlot)  ?: emptyList()
        QuestLock.questSlots[41]?.apply {
            quests = scrollQuests
            isLocked = QuestLock.shouldLock(scrollQuests)
        }
        slotQuests.addAll(scrollQuests)

        QuestStorage.loadQuests(slotQuests)
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

            Trident.playerState.supplies.bait.amount = baitCount
            Logger.debugLog("Bait found - ${Trident.playerState.supplies.bait.amount}")

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
            val match =
                lineSlot.item.findInLore(Regex("""Uses Remaining: ((?:\d|,)+)/((?:\d|,)+)"""))
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
        val augmentsRaw = augmentSlotsIndices.map { screen.menu.slots[it].item } as MutableList

        var availableSlots = 10
        Trident.playerState.supplies.augmentContainers = augmentsRaw.mapNotNull { rawName ->
            when {
                rawName.hoverName.string.contains("Locked Supply Slot") -> {
                    availableSlots--
                    null
                }

                rawName.hoverName.string.contains("Empty Supply Slot") -> null

                else -> {
                    val cleanedName = rawName.hoverName.string.replace(
                        Regex("""(A\.N\.G\.L\.R\.|\[|]|Augment)"""), ""
                    ).trim()
                    getAugmentContainer(cleanedName, rawName.getLore().map { it.string })
                }
            }
        } as MutableList<AugmentContainer>
        Logger.debugLog("Augments: ${Trident.playerState.supplies.augmentContainers}")
        Trident.playerState.supplies.augmentsAvailable = availableSlots
        Trident.playerState.supplies.baitDesynced = false
        Trident.playerState.supplies.needsUpdating = false

        // Overclocks (slots 12-15)
        val hookOverclock = screen.menu.slots[12]
        Trident.playerState.supplies.overclocks.hook =
            ItemParser.getActiveOverclock(hookOverclock.item)

        val magnetOverclock = screen.menu.slots[13]
        Trident.playerState.supplies.overclocks.magnet =
            ItemParser.getActiveOverclock(magnetOverclock.item)

        val rodOverclock = screen.menu.slots[14]
        Trident.playerState.supplies.overclocks.rod =
            ItemParser.getActiveOverclock(rodOverclock.item)

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
        DialogCollection.refreshDialog("supplies")
    }

    private fun handleWfdItemLine(item: ItemStack) {
        val itemName = item.hoverName.string
        val wayfinderStatus = when {
            "Sunken Swamp" in itemName -> Trident.playerState.wayfinderData.temperate
            "Mirrored Oasis" in itemName -> Trident.playerState.wayfinderData.tropical
            "Volcanic Springs" in itemName -> Trident.playerState.wayfinderData.barren
            else -> return
        }

        item.findInLore(Regex("""Wayfinder Data: (\d{1,3}(?:,\d{3})*)\/(\d{1,3}(?:,\d{3})*)."""))
            ?.let {
                val temperateData = it.groups[1]?.value?.parseFormattedInt() ?: return@let
                wayfinderStatus.data = temperateData
                wayfinderStatus.unlocked = true
                wayfinderStatus.hasGrotto = false
                if (temperateData >= 2000) wayfinderStatus.hasGrotto = true
                Logger.debugLog("Island: ${item.displayName.string}, Current Data: $temperateData, Grotto: false")
                return
            }

        item.findInLore(Regex("""Remaining Stability: (\d+)%"""))?.let {
            wayfinderStatus.hasGrotto = true
            wayfinderStatus.unlocked = true
            wayfinderStatus.grottoStability = it.groups[1]?.value?.parseFormattedInt() ?: return@let
            Logger.debugLog("Island: ${item.displayName.string}, Current Data: 2000+, Grotto: true")
        }
    }

    // TODO: Use safer methods to get data from a slot
    fun findWayfinderData(screen: ContainerScreen) {
        if ("FISHING ISLANDS" !in screen.title.string) return

        handleWfdItemLine(screen.menu.slots[24].item) // temperate
        handleWfdItemLine(screen.menu.slots[33].item) // tropical
        handleWfdItemLine(screen.menu.slots[42].item) // barren

        Trident.playerState.wayfinderData.needsUpdating = false
        DialogCollection.refreshDialog("wayfinder")
    }

    fun findFishingResearch(screen: ContainerScreen) {
        if ("FISHING PROGRESS" !in screen.title.string) return

        // empty the list
        Trident.playerState.research.researchTypes = mutableListOf()

        val researchSlots = listOf(12, 13, 14, 15, 16)
        val researchTypes =
            mapOf(12 to "Strong", 13 to "Wise", 14 to "Glimmering", 15 to "Greedy", 16 to "Lucky")
        for (slot in researchSlots) {
            val tierLine =
                screen.menu.slots[slot].item.safeGetLine(0)?.string?.split("(")?.getOrNull(1)
                    ?: continue
            val tierLineNoBrackets = tierLine.dropLast(2)
            val tier = tierLineNoBrackets.split("/").getOrNull(0)?.parseFormattedInt() ?: continue

            val progressLine = screen.menu.slots[slot].item.safeGetLine(4)?.string ?: continue
            if ("Progress: " in progressLine) {
                val progressValues = progressLine.split(": ").getOrNull(1) ?: continue

                val amount = progressValues.split("/").getOrNull(0)?.parseFormattedInt() ?: continue
                val total = progressValues.split("/").getOrNull(1)?.parseFormattedInt() ?: continue

                Trident.playerState.research.researchTypes.add(
                    researchSlots.indexOf(slot), Research(
                        researchTypes[slot] ?: "Strong",
                        tier = tier,
                        progressThroughTier = amount,
                        totalForTier = total
                    )
                )
            }
        }

        Trident.playerState.research.needsUpdating = false
        DialogCollection.refreshDialog("research")
    }
}