package cc.pe3epwithyou.trident.state.fishing

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.state.PlayerStateIO

enum class AugmentTrigger(
    val lore: String
) {
    ANYTHING("Caught Anything"),
    ANYTHING_GROTTO("Caught in Grotto"),
    FISH("Caught Fish"),
    ELUSIVE("Caught Elusive Fish"),
    TREASURE("Caught Fish"),
    SPIRIT("Caught Spirit"),
    PEARL("Caught Pearl"),
    SPOT("Cast Into Spot"),
    NONE("madeyoulook"),
}

fun updateDurability(trigger: AugmentTrigger) {
    val augmentContainers = Trident.playerState.supplies.augmentContainers
    augmentContainers.filter { it.augment.useTrigger == trigger }.forEach {
        if (it.paused) return@forEach
        if (it.status == AugmentStatus.NEEDS_REPAIRING || it.status == AugmentStatus.BROKEN) return@forEach
        if (!it.augment.worksInGrotto && MCCIState.fishingState.isGrotto) return@forEach

        it.durability -= 1
        it.durability = it.durability.coerceIn(0, it.augment.uses)

        if (it.durability == 0 && it.status == AugmentStatus.NEW)
            it.status = AugmentStatus.NEEDS_REPAIRING
        if (it.durability == 0 && it.status == AugmentStatus.REPAIRED)
            it.status = AugmentStatus.BROKEN
    }
    DialogCollection.refreshDialog("supplies")
    PlayerStateIO.save()
}