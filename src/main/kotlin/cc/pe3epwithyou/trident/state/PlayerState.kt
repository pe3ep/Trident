package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.state.fishing.Augment

data class Bait(var type: Rarity = Rarity.COMMON, var amount: Int? = null)
data class Line(var type: Rarity = Rarity.COMMON, var uses: Int? = null)
data class UnstableOverclock(var type: Augment? = null, var timeLeft: Int = 60 * 5, var cooldown: Int = 60 * 45)
data class Overclocks(
    var hook: Augment? = null,
    var magnet: Augment? = null,
    var rod: Augment? = null,
    var unstable: UnstableOverclock? = UnstableOverclock(),
)

data class Supplies(
    var bait: Bait = Bait(),
    var line: Line = Line(),
    var augments: List<Augment> = listOf(),
    var augmentsAvailable: Int = 0,
    var overclocks: Overclocks = Overclocks(),
    var updateRequired: Boolean = true,
)
data class PlayerState(
    var supplies: Supplies = Supplies(),
)