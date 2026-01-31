package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.ConfigUtil
import cc.pe3epwithyou.trident.feature.fishing.OverclockClock
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.AugmentStatus
import cc.pe3epwithyou.trident.state.fishing.OverclockTexture
import cc.pe3epwithyou.trident.utils.Logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path

@Serializable
data class Bait(var type: Rarity = Rarity.COMMON, var amount: Int? = null)

@Serializable
data class Line(var type: Rarity = Rarity.COMMON, var uses: Int? = null, var amount: Int? = null)

@Serializable
data class AugmentContainer(
    var augment: Augment,
    var status: AugmentStatus = AugmentStatus.NEW,
    var durability: Int = augment.uses,
    var paused: Boolean = false
)

@Serializable
data class UnstableOverclock(
    var texture: OverclockTexture? = null, var state: OverclockState = OverclockState(
        isAvailable = false,
        duration = 60 * 5,
        cooldownDuration = 60 * 45,
        isActive = false,
        isCooldown = false
    )
)

@Serializable
data class SupremeOverclock(
    var state: OverclockState = OverclockState(
        isAvailable = false,
        duration = 60 * 10,
        cooldownDuration = 60 * 60,
        isCooldown = false,
        isActive = false
    )
)

@Serializable
data class OverclockState(
    var isAvailable: Boolean,
    var duration: Long = 0,
    var activeUntil: Long = 0,
    var availableIn: Long = 0,
    var cooldownDuration: Long = 0,
    var isActive: Boolean,
    var isCooldown: Boolean
)

@Serializable
data class Overclocks(
    var hook: Augment? = null,
    var magnet: Augment? = null,
    var rod: Augment? = null,
    var unstable: UnstableOverclock = UnstableOverclock(),
    var supreme: SupremeOverclock = SupremeOverclock()
)

@Serializable
data class Supplies(
    var bait: Bait = Bait(),
    var line: Line = Line(),
    @Deprecated("Moved to use AugmentContainer")
    var augments: MutableList<Augment> = mutableListOf(),
    var augmentContainers: MutableList<AugmentContainer> = mutableListOf(),
    var augmentsAvailable: Int = 0,
    var overclocks: Overclocks = Overclocks(),
    var baitDesynced: Boolean = true,
    var needsUpdating: Boolean = true,
)

@Serializable
data class WayfinderStatus(
    var unlocked: Boolean = false,
    var island: String,
    var data: Int = 0,
    var hasGrotto: Boolean = false,
    var grottoStability: Int = 100,
)

@Serializable
data class WayfinderData(
    var temperate: WayfinderStatus = WayfinderStatus(island = "Temperate"),
    var tropical: WayfinderStatus = WayfinderStatus(island = "Tropical"),
    var barren: WayfinderStatus = WayfinderStatus(island = "Barren"),
    var needsUpdating: Boolean = true,
)

@Serializable
data class Research(
    var type: String,
    var tier: Int = 1,
    var progressThroughTier: Int = 0,
    var totalForTier: Int = 1000
)

@Serializable
data class FishingResearch(
    var researchTypes: MutableList<Research> = mutableListOf(),
    var needsUpdating: Boolean = true,
)

@Serializable
data class Rank(val name: String, val image: String)

@Serializable
data class ArenaData(var currentRank: Rank? = null)

@Serializable
data class PlayerState(
    var supplies: Supplies = Supplies(),
    var wayfinderData: WayfinderData = WayfinderData(),
    var research: FishingResearch = FishingResearch(),
    var hatesUpdates: Boolean = false,
    var arenaData: ArenaData = ArenaData(),
    var levelData: LevelData? = null
)

object PlayerStateIO {
    // configDir/trident/playerstate.json
    private val path: Path =
        FabricLoader.getInstance().configDir.resolve("trident").resolve("playerstate.json")

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun save() {
        val serializable = Trident.playerState
        val text = json.encodeToString(serializable)
        ConfigUtil.writeToConfig(path, text)
    }

    @Suppress("DEPRECATION")
    fun load(): PlayerState {
        Logger.info("Loading player state from $path")
        if (!Files.exists(path)) return PlayerState()
        val text = ConfigUtil.readFromConfig(path) ?: return PlayerState()
        val serializable = json.decodeFromString<PlayerState>(text)

        // Migration from 1.0.5 -> 1.0.6
        serializable.supplies.overclocks.unstable.state.duration = 60 * 5
        serializable.supplies.overclocks.supreme.state.duration = 60 * 10

        serializable.supplies.overclocks.unstable.state.cooldownDuration = 60 * 45
        serializable.supplies.overclocks.supreme.state.cooldownDuration = 60 * 60

        // Migration from 1.0.8.1 -> 1.0.9
        if (!serializable.supplies.augments.isEmpty()) {
            serializable.supplies.augmentContainers.clear()

            serializable.supplies.augments.forEach {
                serializable.supplies.augmentContainers.add(AugmentContainer(it))
            }

            serializable.supplies.needsUpdating = true
            serializable.supplies.augments.clear()
        }

        if (serializable.supplies.overclocks.unstable.state.isActive || serializable.supplies.overclocks.unstable.state.isCooldown) {
            OverclockClock.registerHandler(
                OverclockClock.ClockHandler(
                    "Unstable", serializable.supplies.overclocks.unstable.state
                )
            )
        }
        if (serializable.supplies.overclocks.supreme.state.isActive || serializable.supplies.overclocks.supreme.state.isCooldown) {
            OverclockClock.registerHandler(
                OverclockClock.ClockHandler(
                    "Supreme", serializable.supplies.overclocks.supreme.state
                )
            )
        }
        return serializable
    }
}
