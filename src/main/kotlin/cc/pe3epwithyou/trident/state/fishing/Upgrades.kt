package cc.pe3epwithyou.trident.state.fishing

enum class UpgradeLine(
    val hookLabel: String,
    val magnetLabel: String,
    val rodLabel: String,
    val potLabel: String?,
    val chanceLabel: String?
) {
    STRONG(
        hookLabel = "Strong Hook",
        magnetLabel = "XP Magnet",
        rodLabel = "Boosted Rod",
        potLabel = "Strong Pot",
        chanceLabel = "Elusive"
    ),
    WISE(
        hookLabel = "Wise Hook",
        magnetLabel = "Fish Magnet",
        rodLabel = "Speedy Rod",
        potLabel = "Wise Pot",
        chanceLabel = "Wayfinder"
    ),
    GLIMMERING(
        hookLabel = "Glimmering Hook",
        magnetLabel = "Pearl Magnet",
        rodLabel = "Graceful Rod",
        potLabel = "Glimmering Pot",
        chanceLabel = "Pearl Chance"
    ),
    GREEDY(
        hookLabel = "Greedy Hook",
        magnetLabel = "Treasure Magnet",
        rodLabel = "Glitched Rod",
        potLabel = "Greedy Pot",
        chanceLabel = "Treasure Chance"
    ),
    LUCKY(
        hookLabel = "Lucky Hook",
        magnetLabel = "Spirit Magnet",
        rodLabel = "Stable Rod",
        potLabel = "Lucky Pot",
        chanceLabel = "Spirit Chance"
    );

    fun allLabels(): List<String> = listOfNotNull(hookLabel, magnetLabel, rodLabel, potLabel, chanceLabel)
}

enum class UpgradeType(val maxLevel: Int) {
    HOOK(20),
    MAGNET(20),
    ROD(20),
    POT(20),
    CHANCE(10)
}

data class PlayerUpgrades(
    val levels: MutableMap<UpgradeLine, MutableMap<UpgradeType, Int>> = mutableMapOf()
) {
    init {
        // initialize default levels to 0 for all combinations
        for (line in UpgradeLine.entries) {
            val byType = levels.getOrPut(line) { mutableMapOf() }
            for (t in UpgradeType.entries) {
                byType.putIfAbsent(t, 0)
            }
        }
    }

    fun getLevel(line: UpgradeLine, type: UpgradeType): Int =
        levels[line]?.get(type) ?: 0

    fun setLevel(line: UpgradeLine, type: UpgradeType, level: Int) {
        val clamped = level.coerceIn(0, type.maxLevel)
        val byType = levels.getOrPut(line) { mutableMapOf() }
        byType[type] = clamped
    }

    fun increment(line: UpgradeLine, type: UpgradeType, delta: Int = 1) {
        val current = getLevel(line, type)
        setLevel(line, type, current + delta)
    }
}


