package cc.pe3epwithyou.trident.feature.fishing.listeners

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.events.click.ClickEvents
import cc.pe3epwithyou.trident.events.container.ContainerContext
import cc.pe3epwithyou.trident.events.container.ContainerEvents
import cc.pe3epwithyou.trident.feature.fishing.OverclockHandlers
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.state.AugmentContainer
import cc.pe3epwithyou.trident.state.OverclockState
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.state.Supplies
import cc.pe3epwithyou.trident.state.fishing.getAugmentContainer
import cc.pe3epwithyou.trident.utils.ItemParser
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.findInLore
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.safeGetLine
import cc.pe3epwithyou.trident.utils.parseFormattedInt
import cc.pe3epwithyou.trident.utils.playerState
import net.minecraft.core.component.DataComponents

object SuppliesListeners {
    fun register() {
        ContainerEvents.onOpen(::find)
        ContainerEvents.onClose(::find)

        ClickEvents.onClick {
            requireTitle("FISHING SUPPLIES")
            if (!Config.Fishing.suppliesModule) return@onClick
            val item = clickedItem() ?: return@onClick
            if (item.isEmpty) return@onClick

            val name = item.hoverName.string
            val overclocks = playerState().supplies.overclocks
            if (name.contains("Unstable Overclock", ignoreCase = true) && shift && left) {
                val state = overclocks.unstable.state
                if (!state.isAvailable) return@onClick
                startOverclock("Unstable", state)
            }

            if (name.contains("Supreme Overclock", ignoreCase = true) && left) {
                val state = overclocks.supreme.state
                if (!state.isAvailable) return@onClick
                startOverclock("Supreme", state)
            }
        }
    }

    fun find(ctx: ContainerContext) = with(ctx) {
        requireTitle("FISHING SUPPLIES")
        if (!Config.Fishing.suppliesModule) return@with
        val supplies = playerState().supplies

        processBait(supplies)
        processLine(supplies)
        processAugments(supplies)
        processOverclocks(supplies)

        supplies.needsUpdating = false

        DialogCollection.refreshDialog("supplies")
    }

    private fun ContainerContext.processBait(supplies: Supplies) {
        val bait = item(19) ?: return

        supplies.bait.apply {
            val itemName = bait.hoverName.string
            if (itemName.contains("Empty Bait Slot", ignoreCase = true)) {
                type = Rarity.COMMON
                amount = null
                return
            }

            amount =
                bait.safeGetLine(15)?.string?.split(" ")?.getOrNull(2)?.parseFormattedInt() ?: 0
            type = Rarity.fromString(itemName.split(" ").firstOrNull() ?: "")
        }
        supplies.baitDesynced = false

        Logger.debugLog("Bait: ${supplies.bait}")
    }

    private fun ContainerContext.processLine(supplies: Supplies) {
        val line = item(37) ?: return
        val itemName = line.hoverName.string

        supplies.line.apply {
            if (itemName.contains("Empty Line Slot", ignoreCase = true)) {
                uses = null
                type = Rarity.COMMON
                return
            }

            line.findInLore(Regex("""Uses Remaining: ((?:\d|,)+)/((?:\d|,)+)"""))?.let {
                uses = it.groupValues.getOrNull(1)?.parseFormattedInt()
                amount = it.groupValues.getOrNull(2)?.parseFormattedInt()
            }

            type = Rarity.fromString(itemName.split(" ").firstOrNull() ?: "")
        }

        Logger.debugLog("Line: ${supplies.line}")
    }

    private fun ContainerContext.processAugments(supplies: Supplies) {
        val slots = listOf(30, 31, 32, 33, 34, 39, 40, 41, 42, 43)
        var available = slots.size
        val containers = mutableListOf<AugmentContainer>()

        slots.mapNotNull(::item).forEach { item ->
            val name = item.hoverName.string
            when {
                name.contains("Locked Supply slot", ignoreCase = true) -> available--
                name.contains("Empty Supply slot", ignoreCase = true) -> Unit

                else -> {
                    val cleanedName = name.replace(
                        Regex("""(A\.N\.G\.L\.R\.|\[|]|Augment)"""), ""
                    ).trim()
                    val lore = item.getLore().map { it.string }
                    getAugmentContainer(
                        cleanedName, lore
                    )?.let(containers::add)
                }
            }
        }

        supplies.apply {
            augmentsAvailable = available
            augmentContainers = containers
        }

        Logger.debugLog("Augments: $containers")
    }

    private fun ContainerContext.processOverclocks(supplies: Supplies) {
        supplies.overclocks.apply {
            hook = item(12)?.let(ItemParser::getActiveOverclock) ?: return
            magnet = item(13)?.let(ItemParser::getActiveOverclock) ?: return
            rod = item(14)?.let(ItemParser::getActiveOverclock) ?: return

            val unstableItem = item(15) ?: return
            val unstableModel = unstableItem.components[DataComponents.ITEM_MODEL] ?: return
            unstable.apply {
                state.isAvailable = !unstableModel.path.startsWith("island_interface/locked")
                texture = ItemParser.getUnstableOverclock(unstableItem)
            }

            val supremeItem = item(16) ?: return
            val supremeModel = supremeItem.components[DataComponents.ITEM_MODEL] ?: return
            supreme.state.isAvailable = !supremeModel.path.startsWith("island_interface/locked")
        }

        Logger.debugLog("Overclocks: ${supplies.overclocks}")
    }

    private fun startOverclock(name: String, overclock: OverclockState) = with(overclock) {
        if (isActive || isCooldown) return@with
        OverclockHandlers.startTimedOverclock(name, this)
    }
}