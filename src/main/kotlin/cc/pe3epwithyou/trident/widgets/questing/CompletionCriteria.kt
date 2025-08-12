package cc.pe3epwithyou.trident.widgets.questing

enum class CompletionCriteria(val shortName: String, val regexPattern: Regex, val isTracked: Boolean = true) {
    HOLE_IN_THE_WALL_SURVIVED_TWO_MINUTE(
        "Survive 2m",
        Regex("Survive at least 2m in (\\d+) games of HITW")
    ),
    HOLE_IN_THE_WALL_SURVIVED_MINUTE(
        "Survive 1m",
        Regex("Survive at least 60s in (\\d+) games of HITW")
    ),
    HOLE_IN_THE_WALL_WALLS_DODGED(
        "Dodge walls",
        Regex("Survive (\\d+) walls in HITW"),
        false
    ),
    HOLE_IN_THE_WALL_TOP_EIGHT(
        "Top 8",
        Regex("Place top 8 in (\\d+) games of HITW")
    ),
    HOLE_IN_THE_WALL_TOP_FIVE(
        "Top 5",
        Regex("Place top 5 in (\\d+) games of HITW")
    ),
    HOLE_IN_THE_WALL_TOP_THREE(
        "Top 3",
        Regex("Place top 3 in (\\d+) games of HITW")
    ),

    TGTTOS_CHICKENS_PUNCHED(
        "Punch chickens",
        Regex("Punch (\\d+) chickens in TGTTOS")
    ),
    TGTTOS_TOP_EIGHT(
        "Top 8",
        Regex("Place top 8 in (\\d+) games of TGTTOS")
    ),
    TGTTOS_TOP_FIVE(
        "Top 5",
        Regex("Place top 5 in (\\d+) games of TGTTOS")
    ),
    TGTTOS_TOP_THREE(
        "Top 3",
        Regex("Place top 3 in (\\d+) games of TGTTOS")
    ),
    TGTTOS_ROUND_TOP_EIGHT(
        "Round top 8",
        Regex("Place top 8 in (\\d+) rounds of TGTTOS")
    ),
    TGTTOS_ROUND_TOP_FIVE(
        "Round top 5",
        Regex("Place top 5 in (\\d+) rounds of TGTTOS")
    ),
    TGTTOS_ROUND_TOP_THREE(
        "Round top 3",
        Regex("Place top 3 in (\\d+) rounds of TGTTOS")
    ),

    BATTLE_BOX_QUADS_GAMES_PLAYED(
        "Games",
        Regex("Complete (\\d+) games of Battle Box")
    ),
    BATTLE_BOX_QUADS_TEAM_ROUNDS_WON(
        "Rounds",
        Regex("Win (\\d+) rounds of Battle Box")
    ),
    BATTLE_BOX_QUADS_TEAM_FIRST_PLACE(
        "Team 1st",
        Regex("Place 1st as a team in (\\d+) games of Battle Box")
    ),
    BATTLE_BOX_QUADS_TEAM_SECOND_PLACE(
        "Team 2nd",
        Regex("Place 2nd or higher as a team in (\\d+) games of Battle Box")
    ),
    BATTLE_BOX_QUADS_PLAYERS_KILLED(
        "Kill players",
        Regex("Eliminate (\\d+) players in Battle Box")
    ),
    BATTLE_BOX_QUADS_RANGED_KILLS(
        "Ranged kills",
        Regex("Eliminate (\\d+) players in Battle Box using a ranged weapon")
    ),

    SKY_BATTLE_QUADS_SURVIVED_TWO_MINUTE(
        "Survive 2m",
        Regex("Survive at least 2m in (\\d+) games of Sky Battle")
    ),
    SKY_BATTLE_QUADS_SURVIVED_MINUTE(
        "Survive 1m",
        Regex("Survive at least 60s in (\\d+) games of Sky Battle")
    ),
    SKY_BATTLE_QUADS_SURVIVAL_TOP_TEN(
        "Survive Top 10",
        Regex("Reach a Survival Placement of 10 or higher in (\\d+) games of Sky Battle")
    ),
    SKY_BATTLE_QUADS_SURVIVAL_TOP_FIVE(
        "Survive Top 5",
        Regex("Reach a Survival Placement of 5 or higher in (\\d+) games of Sky Battle")
    ),
    SKY_BATTLE_QUADS_SURVIVAL_TOP_THREE(
        "Survive Top 3",
        Regex("Reach a Survival Placement of 3 or higher in (\\d+) games of Sky Battle")
    ),
    SKY_BATTLE_QUADS_PLAYERS_KILLED(
        "Kill players",
        Regex("Eliminate (\\d+) players in Sky Battle")
    ),

    PW_SURVIVAL_OBSTACLES_COMPLETED(
        "Obstacles",
        Regex("Complete (\\d+) obstacles in Parkour Warrior Survivor")
    ),
    PW_SURVIVAL_PLAYERS_ELIMINATED(
        "Outlive players",
        Regex("Outlive (\\d+) players in Parkour Warrior Survivor")
    ),
    PW_SURVIVAL_LEAP_6_COMPLETION(
        "Complete leap 6",
        Regex("Complete leap 6 in (\\d+) games of Parkour Warrior Survivor")
    ),
    PW_SURVIVAL_LEAP_4_COMPLETION(
        "Complete leap 4",
        Regex("Complete leap 4 in (\\d+) games of Parkour Warrior Survivor")
    ),
    PW_SURVIVAL_LEAP_2_COMPLETION(
        "Complete leap 2",
        Regex("Complete leap 2 in (\\d+) games of Parkour Warrior Survivor")
    ),

    PW_SOLO_TOTAL_MEDALS_BANKED(
        "Earn medals",
        Regex("Earn (\\d+) Medals from Parkour Warrior Dojo course completions")
    ),
    PW_SOLO_STANDARD_CMPL_BELOW_FIVE_MIN(
        "Standard <5m",
        Regex("Perform (\\d+) Standard Completions each under 5m in Parkour Warrior Dojo")
    ),
    PW_SOLO_STANDARD_CMPL_BELOW_THREE_MIN(
        "Standard <3m",
        Regex("Perform (\\d+) Standard Completions each under 4m in Parkour Warrior Dojo")
    ),
    PW_SOLO_STANDARD_CMPL_BELOW_TWO_MIN(
        "Standard <2m",
        Regex("Perform (\\d+) Standard Completions each under 2m in Parkour Warrior Dojo")
    ),
    PW_SOLO_ADVANCED_CMPL_BELOW_FIVE_MIN(
        "Advanced <5m",
        Regex("Perform (\\d+) Advanced Completions each under 5m in Parkour Warrior Dojo")
    ),
    PW_SOLO_ADVANCED_CMPL_BELOW_FOUR_MIN(
        "Advanced <4m",
        Regex("Perform (\\d+) Advanced Completions each under 4m in Parkour Warrior Dojo")
    ),
    PW_SOLO_TOTAL_ADVANCED_CMPLS(
        "Total advanced",
        Regex("Perform (\\d+) Advanced Completions (or higher) in Parkour Warrior Dojo")
    ),
    PW_SOLO_TOTAL_STANDARD_CMPLS(
        "Total standard",
        Regex("Perform (\\d+) Standard Completions (or higher) in Parkour Warrior Dojo")
    ),

    DYNABALL_SURVIVE_1M(
        "Survive 1m",
        Regex("Survive at least 1m or win in (\\d+) games of Dynaball")
    ),
    DYNABALL_SURVIVE_2M(
        "Survive 2m",
        Regex("Survive at least 2m or win in (\\d+) games of Dynaball")
    ),
    DYNABALL_SURVIVE_4M(
        "Survive 4m",
        Regex("Survive at least 4m or win in (\\d+) games of Dynaball")
    ),
    DYNABALL_WINS(
        "Wins",
        Regex("Win (\\d+) games of Dynaball while surviving until the end")
    ),
    DYNABALL_PLAYERS_ELIMINATED(
        "Kill players",
        Regex("Eliminate (\\d+) players during games of Dynaball")
    ),
    DYNABALL_PLAYERS_STUCK(
        "Stuck players",
        Regex("Stick (\\d+) players with TNT during games of Dynaball"),
        false
    ),
    DYNABALL_BLOCKS_DESTROYED(
        "Destroy blocks",
        Regex("Destroy (\\d+) blocks during games of Dynaball"),
        false
    ),
    DYNABALL_BLOCKS_PLACED(
        "Place blocks",
        Regex("Place (\\d+) repair blocks during games of Dynaball"),
        false
    ),

    ROCKET_SPLEEF_PLAYERS_OUTLIVED(
        "Outlive players",
        Regex("Outlive (\\d+) players during games of Rocket Spleef Rush")
    ),
    ROCKET_SPLEEF_TOP_FIVE(
        "Top 5",
        Regex("Place 5th or higher in (\\d+) games of Rocket Spleef Rush")
    ),
    ROCKET_SPLEEF_TOP_EIGHT(
        "Top 8",
        Regex("Place 8th or higher in (\\d+) games of Rocket Spleef Rush")
    ),
    ROCKET_SPLEEF_TOP_THREE(
        "Top 3",
        Regex("Place 3rd or higher in (\\d+) games of Rocket Spleef Rush")
    ),
    ROCKET_SPLEEF_SURVIVE_60S(
        "Survive 1m",
        Regex("Surive for at least 60s in (\\d+) games of Rocket Spleef Rush")
    ),
    ROCKET_SPLEEF_DIRECT_HITS(
        "Direct hits",
        Regex("Land (\\d) direct rocket hits on players during games of Rocket Spleef Rush"),
        false
    )
}