package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.OverclockTexture

data class Bait(var type: Rarity = Rarity.COMMON, var amount: Int? = null)
data class Line(var type: Rarity = Rarity.COMMON, var uses: Int? = null)
data class UnstableOverclock(
    var texture: OverclockTexture? = null,
    override var duration: Long = 60 * 5 * 20,
//    override var duration: Long = 15 * 20,
    override var timeLeft: Long = 0,
    override var cooldownLeft: Long = 0,
    override var cooldownDuration: Long = 60 * 45 * 20,
//    override var cooldownDuration: Long = 15 * 20,
    override var isActive: Boolean = false,
    override var isCooldown: Boolean = false
) : Overclock(duration, timeLeft, cooldownLeft, cooldownDuration, isActive, isCooldown)

data class SupremeOverclock(
    override var duration: Long = 60 * 10 * 20,
//    override var duration: Long = 15 * 20,
    override var timeLeft: Long = 0,
    override var cooldownLeft: Long = 0,
    override var cooldownDuration: Long = 60 * 60 * 20,
//    override var cooldownDuration: Long = 15 * 20,
    override var isActive: Boolean = false,
    override var isCooldown: Boolean = false
) : Overclock(duration, timeLeft, cooldownLeft, cooldownDuration, isActive, isCooldown)

abstract class Overclock(
    open var duration: Long,
    open var timeLeft: Long,
    open var cooldownLeft: Long,
    open var cooldownDuration: Long,
    open var isActive: Boolean,
    open var isCooldown: Boolean
)

data class Overclocks(
    var hook: Augment? = null,
    var magnet: Augment? = null,
    var rod: Augment? = null,
    var unstable: UnstableOverclock = UnstableOverclock(),
    var supreme: SupremeOverclock = SupremeOverclock()
)

data class Supplies(
    var bait: Bait = Bait(),
    var line: Line = Line(),
    var augments: MutableList<Augment> = mutableListOf(),
    var augmentsAvailable: Int = 0,
    var overclocks: Overclocks = Overclocks(),
    var baitDesynced: Boolean = true,
    var needsUpdating: Boolean = true,
)
data class PlayerState(
    var supplies: Supplies = Supplies(),
)