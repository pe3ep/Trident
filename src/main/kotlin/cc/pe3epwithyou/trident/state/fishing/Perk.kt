package cc.pe3epwithyou.trident.state.fishing

enum class Perk(
    val visualName: String, val usesInt: Boolean = false
) {
    WAYFINDER_DATA("Wayfinder Data", true),

//    Hooks
    LUCKY_HOOK("Lucky Hook"),
    GREEDY_HOOK("Lucky Hook"),
    WISE_HOOK("Lucky Hook"),
    GLIMMERING_HOOK("Lucky Hook")

    ;

    companion object {
        fun get(name: String): Perk? = Perk.entries.filter { perk -> perk.visualName == name }.getOrNull(0)
    }
}