package cc.pe3epwithyou.trident.feature.questing

import cc.pe3epwithyou.trident.feature.questing.QuestCriteria.*
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.utils.Resources
import net.minecraft.resources.ResourceLocation

class Quest(
    val game: Game,
    val type: QuestType,
    val subtype: QuestSubtype,
    val rarity: Rarity,
    val criteria: QuestCriteria,
    var progress: Int,
    val totalProgress: Int,
) {
    val sprite: ResourceLocation
        get() {
            val directory = type.directoryPath
            val raritySuffix = rarity.name.lowercase()
            return Resources.mcc("textures/$directory$raritySuffix.png")
        }

    val display_name: String = criteria.shortName

    val isCompleted: Boolean
        get() = progress >= totalProgress

    /**
     * Increment progress by amount, clamp to totalProgress and return true if
     * this increment caused the quest to complete.
     */
    fun increment(amount: Int): Boolean {
        if (isCompleted) return false
        progress = (progress + amount).coerceAtMost(totalProgress)
        return isCompleted
    }
}

enum class QuestSubtype {
    DAILY,
    WEEKLY,
    SCROLL;
}

enum class QuestType(
    val directoryPath: String
) {
    DEFAULT(
        "island_interface/quest_log/daily/"
    ),
    SCROLL(
        "island_items/infinibag/quest_scroll/"
    ),
    BOOSTED(
        "island_interface/quest_log/boosted/"
    ),
    ARCANE(
        "island_interface/quest_log/arcane/"
    );
}

enum class GameQuests(
    val list: List<QuestCriteria>
) {
    HITW(
        listOf(
            HOLE_IN_THE_WALL_SURVIVED_TWO_MINUTE,
            HOLE_IN_THE_WALL_SURVIVED_MINUTE,
            HOLE_IN_THE_WALL_WALLS_DODGED,
            HOLE_IN_THE_WALL_TOP_EIGHT,
            HOLE_IN_THE_WALL_TOP_FIVE,
            HOLE_IN_THE_WALL_TOP_THREE
        )
    ),
    TGTTOS(
        listOf(
            TGTTOS_CHICKENS_PUNCHED,
            TGTTOS_TOP_EIGHT,
            TGTTOS_TOP_FIVE,
            TGTTOS_TOP_THREE,
            TGTTOS_ROUND_TOP_EIGHT,
            TGTTOS_ROUND_TOP_FIVE,
            TGTTOS_ROUND_TOP_THREE
        )
    ),
    BATTLE_BOX(
        listOf(
            BATTLE_BOX_QUADS_GAMES_PLAYED,
            BATTLE_BOX_QUADS_TEAM_ROUNDS_WON,
            BATTLE_BOX_QUADS_TEAM_FIRST_PLACE,
            BATTLE_BOX_QUADS_TEAM_SECOND_PLACE,
            BATTLE_BOX_QUADS_PLAYERS_KILLED,
            BATTLE_BOX_QUADS_RANGED_KILLS
        )
    ),
    SKY_BATTLE(
        listOf(
            SKY_BATTLE_QUADS_SURVIVED_TWO_MINUTE,
            SKY_BATTLE_QUADS_SURVIVED_MINUTE,
            SKY_BATTLE_QUADS_SURVIVAL_TOP_TEN,
            SKY_BATTLE_QUADS_SURVIVAL_TOP_FIVE,
            SKY_BATTLE_QUADS_SURVIVAL_TOP_THREE,
            SKY_BATTLE_QUADS_PLAYERS_KILLED
        )
    ),
    PARKOUR_WARRIOR_SURVIVOR(
        listOf(
            PW_SURVIVAL_OBSTACLES_COMPLETED,
            PW_SURVIVAL_PLAYERS_ELIMINATED,
            PW_SURVIVAL_LEAP_6_COMPLETION,
            PW_SURVIVAL_LEAP_4_COMPLETION,
            PW_SURVIVAL_LEAP_2_COMPLETION
        )
    ),
    PARKOUR_WARRIOR_DOJO(
        listOf(
            PW_SOLO_TOTAL_MEDALS_BANKED,
            PW_SOLO_STANDARD_CMPL_BELOW_FIVE_MIN,
            PW_SOLO_STANDARD_CMPL_BELOW_THREE_MIN,
            PW_SOLO_STANDARD_CMPL_BELOW_TWO_MIN,
            PW_SOLO_ADVANCED_CMPL_BELOW_FIVE_MIN,
            PW_SOLO_ADVANCED_CMPL_BELOW_FOUR_MIN,
            PW_SOLO_TOTAL_ADVANCED_CMPLS,
            PW_SOLO_TOTAL_STANDARD_CMPLS
        )
    ),
    DYNABALL(
        listOf(
            DYNABALL_SURVIVE_1M,
            DYNABALL_SURVIVE_2M,
            DYNABALL_SURVIVE_4M,
            DYNABALL_WINS,
            DYNABALL_PLAYERS_ELIMINATED,
            DYNABALL_PLAYERS_STUCK,
            DYNABALL_BLOCKS_DESTROYED,
            DYNABALL_BLOCKS_PLACED
        )
    ),
    ROCKET_SPLEEF_RUSH(
        listOf(
            ROCKET_SPLEEF_PLAYERS_OUTLIVED,
            ROCKET_SPLEEF_TOP_FIVE,
            ROCKET_SPLEEF_TOP_EIGHT,
            ROCKET_SPLEEF_TOP_THREE,
            ROCKET_SPLEEF_SURVIVE_60S,
            ROCKET_SPLEEF_DIRECT_HITS
        )
    )
}