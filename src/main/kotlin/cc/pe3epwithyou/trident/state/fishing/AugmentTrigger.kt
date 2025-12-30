package cc.pe3epwithyou.trident.state.fishing

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.interfaces.DialogCollection
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
    augmentContainers.filter {
        val hasTrigger = it.augment.useTrigger == trigger
        val hasRightStatus = it.status != AugmentStatus.NEEDS_REPAIRING || it.status != AugmentStatus.BROKEN || it.status != AugmentStatus.PAUSED
        return@filter hasTrigger && hasRightStatus
    }.forEach {
        it.durability -= 1
        if (it.durability == 0 && it.status == AugmentStatus.NEW) it.status = AugmentStatus.NEEDS_REPAIRING
        if (it.durability == 0 && it.status == AugmentStatus.REPAIRED) it.status = AugmentStatus.BROKEN
    }
    DialogCollection.refreshDialog("supplies")
    PlayerStateIO.save()
}