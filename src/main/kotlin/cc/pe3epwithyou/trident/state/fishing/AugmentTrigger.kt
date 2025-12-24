package cc.pe3epwithyou.trident.state.fishing

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.state.PlayerStateIO

@Suppress("unused")
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
    val augmentContainers = TridentClient.playerState.supplies.augments
    augmentContainers.filter {
        val hasTrigger = it.augment.useTrigger == trigger
        val hasRightStatus = it.status != AugmentStatus.NEEDS_REPAIRING || it.status != AugmentStatus.BROKEN
        return@filter hasTrigger && hasRightStatus
    }.forEach {
        it.durability -= 1
    }
    DialogCollection.refreshDialog("supplies")
    PlayerStateIO.save()
}