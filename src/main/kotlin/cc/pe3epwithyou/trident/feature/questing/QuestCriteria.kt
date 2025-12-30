package cc.pe3epwithyou.trident.feature.questing

enum class QuestCriteria(
    val shortName: String, val regexPattern: Regex, val statisticKeys: List<String>
) {
    HOLE_IN_THE_WALL_SURVIVED_TWO_MINUTE(
        "Survive 2m",
        Regex("Survive at least 2m in (\\d+) games of HITW"),
        listOf("hole_in_the_wall_survived_two_minute")
    ),
    HOLE_IN_THE_WALL_SURVIVED_MINUTE(
        "Survive 1m", Regex("Survive at least 60s in (\\d+) games of HITW"), listOf("hole_in_the_wall_survived_minute")
    ),
    HOLE_IN_THE_WALL_WALLS_DODGED(
        "Dodge walls", Regex("Survive (\\d+) walls in HITW"), listOf("hole_in_the_wall_walls_dodged")
    ),
    HOLE_IN_THE_WALL_TOP_EIGHT(
        "Top 8", Regex("Place Top 8 in (\\d+) games of HITW"), listOf("hole_in_the_wall_top_eight")
    ),
    HOLE_IN_THE_WALL_TOP_FIVE(
        "Top 5", Regex("Place Top 5 in (\\d+) games of HITW"), listOf("hole_in_the_wall_top_five")
    ),
    HOLE_IN_THE_WALL_TOP_THREE(
        "Top 3", Regex("Place Top 3 in (\\d+) games of HITW"), listOf("hole_in_the_wall_top_three")
    ),

    TGTTOS_CHICKENS_PUNCHED(
        "Punch chickens", Regex("Punch (\\d+) chickens in TGTTOS"), listOf("tgttos_chickens_punched")
    ),
    TGTTOS_TOP_EIGHT(
        "Top 8", Regex("Place Top 8 in (\\d+) games of TGTTOS"), listOf("tgttos_top_eight")
    ),
    TGTTOS_TOP_FIVE(
        "Top 5", Regex("Place Top 5 in (\\d+) games of TGTTOS"), listOf("tgttos_top_five")
    ),
    TGTTOS_TOP_THREE(
        "Top 3", Regex("Place Top 3 in (\\d+) games of TGTTOS"), listOf("tgttos_top_three")
    ),
    TGTTOS_ROUND_TOP_EIGHT(
        "Round top 8", Regex("Place Top 8 in (\\d+) rounds of TGTTOS"), listOf("tgttos_round_top_eight")
    ),
    TGTTOS_ROUND_TOP_FIVE(
        "Round top 5", Regex("Place Top 5 in (\\d+) rounds of TGTTOS"), listOf("tgttos_round_top_five")
    ),
    TGTTOS_ROUND_TOP_THREE(
        "Round top 3", Regex("Place Top 3 in (\\d+) rounds of TGTTOS"), listOf("tgttos_round_top_three")
    ),

    BATTLE_BOX_QUADS_GAMES_PLAYED(
        "Play Games",
        Regex("Complete (\\d+) games of Battle Box"),
        listOf("battle_box_quads_games_played", "battle_box_arena_games_played")
    ),
    BATTLE_BOX_QUADS_TEAM_ROUNDS_WON(
        "Win Rounds",
        Regex("Win (\\d+) rounds of Battle Box"),
        listOf("battle_box_arena_team_rounds_won", "battle_box_quads_team_rounds_won")
    ),
    BATTLE_BOX_QUADS_ROUNDS_PLAYED(
        "Play Rounds",
        Regex("Play (\\d+) rounds of Battle Box"),
        listOf("battle_box_arena_rounds_played", "battle_box_quads_rounds_played")
    ),
    BATTLE_BOX_QUADS_TEAM_FIRST_PLACE(
        "Team 1st",
        Regex("Place 1st as a team in (\\d+) games of Battle Box"),
        listOf("battle_box_quads_team_first_place", "battle_box_arena_team_first_place")
    ),
    BATTLE_BOX_QUADS_TEAM_SECOND_PLACE(
        "Team 2nd",
        Regex("Place 2nd or higher as a team in (\\d+) games of Battle Box"),
        listOf("battle_box_quads_team_second_place", "battle_box_arena_team_second_place")

    ),
    BATTLE_BOX_QUADS_PLAYERS_KILLED(
        "Kill players", Regex("Eliminate (\\d+) players in Battle Box"), listOf(
            "battle_box_quads_players_eliminated",
            "battle_box_quads_players_killed",
            "battle_box_quads_ranged_kills",
            "battle_box_quads_melee_kills",
            "battle_box_quads_explosive_kills",
            "battle_box_arena_players_eliminated",
            "battle_box_arena_players_killed",
            "battle_box_arena_ranged_kills",
            "battle_box_arena_melee_kills",
            "battle_box_arena_explosive_kills",
        )
    ),
    BATTLE_BOX_QUADS_PLAYERS_KILLED_OR_ASSISTED(
        "Kills or Assists",
        Regex("Eliminate or assist in eliminating (\\d+) players in Battle Box"),
        listOf(
            "battle_box_quads_players_eliminated",
            "battle_box_quads_players_killed",
            "battle_box_quads_ranged_kills",
            "battle_box_quads_melee_kills",
            "battle_box_quads_explosive_kills",
            "battle_box_quads_player_kills_assisted",
            "battle_box_arena_players_eliminated",
            "battle_box_arena_players_killed",
            "battle_box_arena_ranged_kills",
            "battle_box_arena_melee_kills",
            "battle_box_arena_explosive_kills",
            "battle_box_arena_player_kills_assisted",
        )
    ),
    BATTLE_BOX_QUADS_RANGED_KILLS(
        "Ranged kills",
        Regex("Eliminate (\\d+) players in Battle Box using a ranged weapon"),
        listOf("battle_box_arena_ranged_kills", "battle_box_quads_ranged_kills")
    ),

    SKY_BATTLE_QUADS_SURVIVED_TWO_MINUTE(
        "Survive 2m",
        Regex("Survive at least 2m in (\\d+) games of Sky Battle"),
        listOf("sky_battle_quads_survived_two_minute")
    ),
    SKY_BATTLE_QUADS_SURVIVED_MINUTE(
        "Survive 1m",
        Regex("Survive at least 60s in (\\d+) games of Sky Battle"),
        listOf("sky_battle_quads_survived_minute")
    ),
    SKY_BATTLE_QUADS_SURVIVAL_TOP_TEN(
        "Survive Top 10",
        Regex("Reach a Survival Placement of 10 or higher in (\\d+) games of Sky Battle"),
        listOf("sky_battle_quads_survival_top_ten")
    ),
    SKY_BATTLE_QUADS_SURVIVAL_TOP_FIVE(
        "Survive Top 5",
        Regex("Reach a Survival Placement of 5 or higher in (\\d+) games of Sky Battle"),
        listOf("sky_battle_quads_survival_top_five")
    ),
    SKY_BATTLE_QUADS_SURVIVAL_TOP_THREE(
        "Survive Top 3",
        Regex("Reach a Survival Placement of 3 or higher in (\\d+) games of Sky Battle"),
        listOf("sky_battle_quads_survival_top_three")
    ),
    SKY_BATTLE_QUADS_PLAYERS_KILLED(
        "Kill players", Regex("Eliminate (\\d+) players in Sky Battle"), listOf(
            "sky_battle_quads_players_killed",
            "sky_battle_quads_players_eliminated",
            "sky_battle_quads_melee_kills",
            "sky_battle_quads_ranged_kills",
            "sky_battle_quads_explosive_kills",
        )
    ),

    PW_SURVIVAL_OBSTACLES_COMPLETED(
        "Obstacles",
        Regex("Complete (\\d+) obstacles in Parkour Warrior Survivor"),
        listOf("pw_survival_obstacles_completed")
    ),
    PW_SURVIVAL_PLAYERS_ELIMINATED(
        "Outlive players",
        Regex("Outlive (\\d+) players in Parkour Warrior Survivor"),
        listOf("pw_survival_players_eliminated")
    ),
    PW_SURVIVAL_LEAP_6_COMPLETION(
        "Complete leap 6",
        Regex("Complete Leap 6 in (\\d+) games of Parkour Warrior Survivor"),
        listOf("pw_survival_leap_6_completion")
    ),
    PW_SURVIVAL_LEAP_4_COMPLETION(
        "Complete leap 4",
        Regex("Complete Leap 4 in (\\d+) games of Parkour Warrior Survivor"),
        listOf("pw_survival_leap_4_completion")
    ),
    PW_SURVIVAL_LEAP_2_COMPLETION(
        "Complete leap 2",
        Regex("Complete Leap 2 in (\\d+) games of Parkour Warrior Survivor"),
        listOf("pw_survival_leap_2_completion")
    ),

    PW_SOLO_TOTAL_MEDALS_BANKED(
        "Earn medals",
        Regex("Earn (\\d+) Medals from Parkour Warrior Dojo course completions"),
        listOf("pw_solo_total_medals_banked")
    ),
    PW_SOLO_STANDARD_CMPL_BELOW_FIVE_MIN(
        "Standard <5m",
        Regex("Perform (\\d+) Standard Completions each under 5m in Parkour Warrior Dojo"),
        listOf("pw_solo_standard_cmpl_below_five_min")
    ),
    PW_SOLO_STANDARD_CMPL_BELOW_THREE_MIN(
        "Standard <4m",
        Regex("Perform (\\d+) Standard Completions each under 4m in Parkour Warrior Dojo"),
        listOf("pw_solo_standard_cmpl_below_three_min")
    ),
    PW_SOLO_STANDARD_CMPL_BELOW_TWO_MIN(
        "Standard <2m",
        Regex("Perform (\\d+) Standard Completions each under 2m in Parkour Warrior Dojo"),
        listOf("pw_solo_standard_cmpl_below_two_min")
    ),
    PW_SOLO_ADVANCED_CMPL_BELOW_FIVE_MIN(
        "Advanced <5m",
        Regex("Perform (\\d+) Advanced Completions each under 5m in Parkour Warrior Dojo"),
        listOf("pw_solo_advanced_cmpl_below_five_min")
    ),
    PW_SOLO_ADVANCED_CMPL_BELOW_FOUR_MIN(
        "Advanced <4m",
        Regex("Perform (\\d+) Advanced Completions each under 4m in Parkour Warrior Dojo"),
        listOf("pw_solo_advanced_cmpl_below_four_min")
    ),
    PW_SOLO_TOTAL_ADVANCED_CMPLS(
        "Total advanced",
        Regex("Perform (\\d+) Advanced Completions \\(or higher\\) in Parkour Warrior Dojo courses"),
        listOf("pw_solo_total_advanced_cmpls")
    ),
    PW_SOLO_TOTAL_STANDARD_CMPLS(
        "Total standard",
        Regex("Perform (\\d+) Standard Completions \\(or higher\\) in Parkour Warrior Dojo courses"),
        listOf("pw_solo_total_standard_cmpls")
    ),

    DYNABALL_SURVIVE_1M(
        "Survive 1m", Regex("Survive at least 1m or win in (\\d+) games of Dynaball"), listOf("dynaball_survive_1m")
    ),
    DYNABALL_SURVIVE_2M(
        "Survive 2m", Regex("Survive at least 2m or win in (\\d+) games of Dynaball"), listOf("dynaball_survive_2m")
    ),
    DYNABALL_SURVIVE_4M(
        "Survive 4m", Regex("Survive at least 4m or win in (\\d+) games of Dynaball"), listOf("dynaball_survive_4m")
    ),
    DYNABALL_WINS(
        "Wins", Regex("Win (\\d+) games of Dynaball while surviving until the end"), listOf("dynaball_wins")
    ),
    DYNABALL_PLAYERS_ELIMINATED(
        "Kill players",
        Regex("Eliminate (\\d+) players during games of Dynaball"),
        listOf("dynaball_players_eliminated")
    ),
    DYNABALL_PLAYERS_STUCK(
        "Stuck players",
        Regex("Stick (\\d+) players with TNT during games of Dynaball"),
        listOf("dynaball_players_stuck")
    ),
    DYNABALL_BLOCKS_DESTROYED(
        "Destroy blocks", Regex("Destroy (\\d+) blocks during games of Dynaball"), listOf("dynaball_blocks_destroyed")
    ),
    DYNABALL_BLOCKS_PLACED(
        "Place blocks", Regex("Place (\\d+) repair blocks during games of Dynaball"), listOf("dynaball_blocks_placed")
    ),

    ROCKET_SPLEEF_PLAYERS_OUTLIVED(
        "Outlive players",
        Regex("Outlive (\\d+) players during games of Rocket Spleef Rush"),
        listOf("rocket_spleef_players_outlived")
    ),
    ROCKET_SPLEEF_TOP_FIVE(
        "Top 5", Regex("Place 5th or higher in (\\d+) games of Rocket Spleef Rush"), listOf("rocket_spleef_top_five")
    ),
    ROCKET_SPLEEF_TOP_EIGHT(
        "Top 8", Regex("Place 8th or higher in (\\d+) games of Rocket Spleef Rush"), listOf("rocket_spleef_top_eight")

    ),
    ROCKET_SPLEEF_TOP_THREE(
        "Top 3", Regex("Place 3rd or higher in (\\d+) games of Rocket Spleef Rush"), listOf("rocket_spleef_top_three")

    ),
    ROCKET_SPLEEF_SURVIVE_60S(
        "Survive 1m",
        Regex("Survive for at least 60 seconds in (\\d+) games of Rocket Spleef Rush"),
        listOf("rocket_spleef_survive_60s")

    ),
    ROCKET_SPLEEF_DIRECT_HITS(
        "Direct hits",
        Regex("Land (\\d+) direct rocket hits on players during games of Rocket Spleef Rush"),
        listOf("rocket_spleef_direct_hits")
    )
}