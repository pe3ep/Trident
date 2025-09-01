package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.OverclockTexture
import cc.pe3epwithyou.trident.state.fishing.UseCondition
import cc.pe3epwithyou.trident.state.fishing.PerkState
import cc.pe3epwithyou.trident.state.fishing.PerkStateCalculator
import cc.pe3epwithyou.trident.state.fishing.PlayerUpgrades
import cc.pe3epwithyou.trident.state.fishing.SpotState
import cc.pe3epwithyou.trident.state.fishing.UpgradeLine

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
    override var isCooldown: Boolean = false,
    var level: Int? = null
) : Overclock(false, duration, timeLeft, cooldownLeft, cooldownDuration, isActive, isCooldown)

data class SupremeOverclock(
    override var duration: Long = 60 * 10 * 20,
//    override var duration: Long = 15 * 20,
    override var timeLeft: Long = 0,
    override var cooldownLeft: Long = 0,
    override var cooldownDuration: Long = 60 * 60 * 20,
//    override var cooldownDuration: Long = 15 * 20,
    override var isActive: Boolean = false,
    override var isCooldown: Boolean = false,
    var level: Int? = null
) : Overclock(false, duration, timeLeft, cooldownLeft, cooldownDuration, isActive, isCooldown)

abstract class Overclock(
    open var isAvailable: Boolean,
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
    var supreme: SupremeOverclock = SupremeOverclock(),
    var stableLevels: StableOverclockLevels = StableOverclockLevels()
)

data class StableOverclockLevels(
    var hook: Int? = null,
    var magnet: Int? = null,
    var rod: Int? = null,
)

data class Supplies(
    var bait: Bait = Bait(),
    var line: Line = Line(),
    var augments: MutableList<MutableAugment> = mutableListOf(),
    var augmentsAvailable: Int = 0,
    var overclocks: Overclocks = Overclocks(),
    var baitDesynced: Boolean = true,
    var needsUpdating: Boolean = true,
)
data class PlayerState(
    var supplies: Supplies = Supplies(),
    var upgrades: PlayerUpgrades = PlayerUpgrades(),
    var perkState: PerkState = PerkState(),
    var spot: SpotState = SpotState(),
    var inGrotto: Boolean = false,
    var tideLines: MutableSet<UpgradeLine> = mutableSetOf(),
    var windLines: MutableSet<UpgradeLine> = mutableSetOf(),
    var magnetPylonBonus: Int = 0,
)

data class MutableAugment(
    val augment: Augment,
    var usesCurrent: Int? = null,
    var usesMax: Int? = null,
    var useCondition: UseCondition? = null,
    var paused: Boolean = false,
    var bannedInGrotto: Boolean = false,
)