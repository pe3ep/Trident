package cc.pe3epwithyou.trident.feature.fishing.listeners

import cc.pe3epwithyou.trident.events.container.ContainerContext
import cc.pe3epwithyou.trident.events.container.ContainerEvents
import cc.pe3epwithyou.trident.feature.fishing.FishingType
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.state.Research
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.findInLore
import cc.pe3epwithyou.trident.utils.parseFormattedInt
import cc.pe3epwithyou.trident.utils.playerState
import net.minecraft.world.item.ItemStack

object ResearchListeners {
    fun register() {
        ContainerEvents.onOpen(::find)
        ContainerEvents.onClose(::find)
    }

    fun find(ctx: ContainerContext) = with(ctx) {
        titleHas("FISHING PROGRESS")

        val types = mutableListOf<Research>()
        var index = 12
        FishingType.entries.forEach {
            processAndMutateResearch(item(index) ?: return@forEach, types, it)
            index++
        }

        playerState().research.apply {
            researchTypes = types
            needsUpdating = false
        }

        DialogCollection.refreshDialog("research")
    }

    private fun processAndMutateResearch(item: ItemStack, researchTypes: MutableList<Research>, type: FishingType) {
        val research = Research(type)
        item.findInLore(Regex("""\w+ Research \((\d+)/100\)"""))?.let {
            research.tier = it.groupValues.getOrNull(1)?.parseFormattedInt() ?: 1
        }

        item.findInLore(Regex("""Progress: (.+)/(.+)"""))?.let {
            research.apply {
                progressThroughTier = it.groupValues.getOrNull(1)?.parseFormattedInt() ?: 0
                totalForTier = it.groupValues.getOrNull(2)?.parseFormattedInt() ?: 1
            }
        }

        researchTypes.add(research)
    }
}