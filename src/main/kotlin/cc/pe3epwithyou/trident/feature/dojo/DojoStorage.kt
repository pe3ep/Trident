package cc.pe3epwithyou.trident.feature.dojo

import kotlinx.serialization.Serializable

object DojoStorage {

    /**
     * Stores fastest course times in ticks for each section (3 courses in one section)
     */
    private val sectionTimes = linkedMapOf<String, MutableList<Long>>(
        "B1" to mutableListOf(),
        "M1" to mutableListOf(),
        "B2" to mutableListOf(),
        "M2" to mutableListOf(),
        "B3" to mutableListOf(),
        "M3" to mutableListOf(),
        "M4" to mutableListOf()
    )

    fun completedCourseCount(section: String): Int =
        sectionTimes[section]?.count { it > 0L } ?: 0

    fun coursePersonalBest(courseId: String): Long? {
        val (section, index) = parseCourseId(courseId) ?: return null
        return sectionTimes[section]?.getOrNull(index)?.takeIf { it > 0L }
    }

    fun recordCoursePersonalBest(courseId: String, ticks: Long) {
        require(ticks > 0L)

        val (section, index) = parseCourseId(courseId) ?: return
        val times = sectionTimes[section] ?: return

        while (times.size <= index) {
            times.add(0L)
        }

        if (times[index] == 0L || ticks < times[index]) {
            times[index] = ticks
        }
    }

    fun formattedTime(ticks: Long): String {
        require(ticks >= 0L)

        val totalSeconds = ticks / 20L
        val milliseconds = (ticks % 20L) * 50L
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L

        val time = if (minutes > 0L) {
            "$minutes:${seconds.toString().padStart(2, '0')}"
        } else {
            totalSeconds.toString()
        }

        return "$time.${milliseconds.toString().dropLast(1).padStart(2, '0')}"
    }

    // TODO: Add the ability to save/load splits to a file
    fun load(saved: SavedCourseSplits) {
        sectionTimes.forEach { (section, times) ->
            times.clear()
            times.addAll(
                saved.sectionTimes[section]
                    .orEmpty()
                    .take(3)
                    .map { it.coerceAtLeast(0L) }
            )
        }
    }

    // TODO: Detect what course is being played and it's expiration date
    fun snapshot(id: String, expiresAt: Long) = SavedCourseSplits(
        id = id,
        sectionTimes = sectionTimes.mapValues { it.value.toList() },
        expiresAt = expiresAt
    )

    /** Extracts a section and zero-based course index from an ID such as B1-2. */
    private fun parseCourseId(courseId: String): Pair<String, Int>? {
        val split = courseId.split('-')
        if (split.size != 2) return null

        val section = split[0]
        val course = split[1].toIntOrNull() ?: return null

        if (section !in sectionTimes || course !in 1..3) return null

        return section to (course - 1)
    }

    /**
     * Clears all stored course best times.
     */
    fun clear() {
        sectionTimes.values.forEach { it.clear() }
    }
}

/** Serializable snapshot of all section best times, measured in ticks. */
@Serializable
data class SavedCourseSplits(
    val id: String,
    val sectionTimes: Map<String, List<Long>>,
    val expiresAt: Long
)

enum class CompletionType {
    NONE,
    STANDARD,
    ADVANCED,
    EXPERT;
}