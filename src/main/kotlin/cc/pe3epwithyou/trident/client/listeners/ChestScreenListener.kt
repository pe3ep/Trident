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
import cc.pe3epwithyou.trident.state.MutableAugment
import cc.pe3epwithyou.trident.state.fishing.getAugmentByName
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.ItemParser
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import cc.pe3epwithyou.trident.state.fishing.PerkStateCalculator
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
        if ("FISHING PERKS" in screen.title.string) {
            DelayedAction.delayTicks(2L) {
                findFishingPerks(screen)
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
        val augmentItems = augmentSlotsIndices.map { screen.menu.slots[it].item }
        val augmentsRaw = augmentItems.map { it.displayName.string } as MutableList

        var availableSlots = 10
        val parsedAugments: MutableList<Augment?> = augmentsRaw.mapNotNull { rawName ->
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
        } as MutableList<Augment?>
        ChatUtils.debugLog("""
            Augments: ${TridentClient.playerState.supplies.augments}
        """.trimIndent())
        TridentClient.playerState.supplies.augmentsAvailable = availableSlots
        // Parse uses for each augment; keep length aligned with augments list, skip locked/empty
        val mutableAugments = mutableListOf<MutableAugment>()
        augmentItems.forEachIndexed { idx, item ->
            val name = item.displayName.string
            if (name.contains("Locked Supply Slot") || name.contains("Empty Supply Slot")) {
                // skip placeholders in new structure
            } else {
                val parsed = ItemParser.getAugmentUses(item)
                val augment = parsedAugments.getOrNull(idx)
                if (augment != null) {
                    val meta = ItemParser.getAugmentUseCondition(item)
                    val paused = ItemParser.isAugmentPaused(item)
                    val mutable = MutableAugment(
                        augment = augment,
                        usesCurrent = parsed?.first,
                        usesMax = parsed?.second,
                        useCondition = meta.condition,
                        paused = paused,
                        bannedInGrotto = meta.bannedInGrotto
                    )
                    mutableAugments.add(mutable)
                }
            }
        }
        TridentClient.playerState.supplies.augments = mutableAugments
        TridentClient.playerState.supplies.baitDesynced = false
        TridentClient.playerState.supplies.needsUpdating = false

        // Overclocks (slots 12-15)
        val hookOverclock = screen.menu.slots[12]
        TridentClient.playerState.supplies.overclocks.hook = ItemParser.getActiveOverclock(hookOverclock.item)
        TridentClient.playerState.supplies.overclocks.stableLevels.hook = ItemParser.getOverclockLevel(hookOverclock.item)

        val magnetOverclock = screen.menu.slots[13]
        TridentClient.playerState.supplies.overclocks.magnet = ItemParser.getActiveOverclock(magnetOverclock.item)
        TridentClient.playerState.supplies.overclocks.stableLevels.magnet = ItemParser.getOverclockLevel(magnetOverclock.item)

        val rodOverclock = screen.menu.slots[14]
        TridentClient.playerState.supplies.overclocks.rod = ItemParser.getActiveOverclock(rodOverclock.item)
        TridentClient.playerState.supplies.overclocks.stableLevels.rod = ItemParser.getOverclockLevel(rodOverclock.item)

        val unstableOverclock = screen.menu.slots[15]
        val unstableModel = unstableOverclock.item.components[DataComponents.ITEM_MODEL]
        if (unstableModel != null) {
            TridentClient.playerState.supplies.overclocks.unstable.isAvailable = !unstableModel.path.startsWith("island_interface/locked")
        }
        TridentClient.playerState.supplies.overclocks.unstable.texture = ItemParser.getUnstableOverclock(unstableOverclock.item)
        TridentClient.playerState.supplies.overclocks.unstable.level = ItemParser.getOverclockLevel(unstableOverclock.item)

        val supremeOverclock = screen.menu.slots[16]
        val supremeModel = supremeOverclock.item.components[DataComponents.ITEM_MODEL]
        if (supremeModel != null) {
            TridentClient.playerState.supplies.overclocks.supreme.isAvailable = !supremeModel.path.startsWith("island_interface/locked")
        }
        TridentClient.playerState.supplies.overclocks.stableLevels.hook = ItemParser.getOverclockLevel(hookOverclock.item)
        TridentClient.playerState.supplies.overclocks.stableLevels.magnet = ItemParser.getOverclockLevel(magnetOverclock.item)
        TridentClient.playerState.supplies.overclocks.stableLevels.rod = ItemParser.getOverclockLevel(rodOverclock.item)
        // Recompute and refresh dialogs
        TridentClient.playerState.perkState = PerkStateCalculator.recompute(TridentClient.playerState)
        // Refresh supplies dialog if open
        DialogCollection.refreshDialog("supplies")
        DialogCollection.refreshDialog("upgrades")
        DialogCollection.refreshDialog("chances")
        DialogCollection.refreshDialog("hookchances")
        DialogCollection.refreshDialog("magnetchances")
        DialogCollection.refreshDialog("rodchances")
        DialogCollection.refreshDialog("potchances")
        DialogCollection.refreshDialog("chanceperks")
    }

    fun findFishingPerks(screen: ContainerScreen) {
        // This menu lists permanent upgrades. We'll scan visible slots for names like
        // "Strong Hook (8/20)" and set corresponding levels.
        val upgrades = TridentClient.playerState.upgrades
        screen.menu.slots.forEach { slot ->
            val name = slot.item.displayName.string
            val line = ItemParser.parseUpgradeLine(name) ?: return@forEach
            val type = ItemParser.parseUpgradeType(name) ?: return@forEach
            val level = ItemParser.parseUpgradeLevelFromName(name) ?: return@forEach
            upgrades.setLevel(line, type, level)
        }
        // Refresh dialog to reflect newly parsed values immediately
        DialogCollection.refreshDialog("upgrades")
        DialogCollection.refreshDialog("chances")
        DialogCollection.refreshDialog("hookchances")
        DialogCollection.refreshDialog("magnetchances")
        DialogCollection.refreshDialog("rodchances")
        DialogCollection.refreshDialog("potchances")
        DialogCollection.refreshDialog("chanceperks")
        TridentClient.playerState.perkState = PerkStateCalculator.recompute(TridentClient.playerState)
    }
}