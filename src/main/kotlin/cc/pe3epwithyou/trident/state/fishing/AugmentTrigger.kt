package cc.pe3epwithyou.trident.state.fishing

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
    SPOT("Cast Into Spot")
}