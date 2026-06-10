package cc.pe3epwithyou.trident.feature.questing

@ConsistentCopyVisibility
data class QuestHolder private constructor(
    val quests: List<Quest>
) {
    companion object {
        fun create(list: List<Quest>): QuestHolder {
            val holder = QuestHolder(list)
            holder.quests.forEach { it.questHolder = holder }
            return holder
        }
    }

    fun totalProgress(): Float =
        quests.sumOf {
            if (it.totalProgress <= 0) 0.0
            else it.progress.toDouble() / it.totalProgress.toDouble() * 100.0
        }.toFloat().coerceAtMost(100f)
}
