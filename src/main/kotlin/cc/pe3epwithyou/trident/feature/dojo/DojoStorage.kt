package cc.pe3epwithyou.trident.feature.dojo

import kotlinx.serialization.Serializable

object DojoStorage {

    private val sections = linkedMapOf<String, LinkedHashSet<Int>>(
        "B1" to linkedSetOf(),
        "M1" to linkedSetOf(),
        "B2" to linkedSetOf(),
        "M2" to linkedSetOf(),
        "B3" to linkedSetOf(),
        "M3" to linkedSetOf(),
        "M4" to linkedSetOf()
    )

    private val courseBestTimesMs = linkedMapOf<String, Long>()

    fun markCourseCompleted(courseId: String) {
        val separator = courseId.lastIndexOf('-')
        if (separator < 0) return

        val section = courseId.substring(0, separator)
        val index = courseId.substring(separator + 1).toIntOrNull()
            ?: return

        if (index in 1..3) {
            sections[section]?.add(index)
        }
    }

    fun completedCount(section: String): Int =
        sections[section]?.size ?: 0

    fun bestTimeMs(courseId: String): Long? =
        courseBestTimesMs[courseId]

    fun recordBestTimeMs(courseId: String, timeMs: Long): Boolean {
        require(timeMs >= 0L)

        val previous = courseBestTimesMs[courseId]

        if (previous != null && timeMs >= previous) {
            return false
        }

        courseBestTimesMs[courseId] = timeMs
        return true
    }

    fun load(saved: SavedCourseSplits) {
        sections.values.forEach { it.clear() }

        saved.sectionTimes.forEach { (section, completed) ->
            sections[section]?.addAll(completed.filter { it in 1..3 })
        }

        courseBestTimesMs.clear()
        courseBestTimesMs.putAll(
            saved.courseBestTimesMs.filterValues { it >= 0L }
        )
    }

    fun snapshot(id: String, expiresAt: Long): SavedCourseSplits {
        return SavedCourseSplits(
            id = id,
            sectionTimes = sections.mapValues { it.value.toSet() },
            expiresAt = expiresAt,
            courseBestTimesMs = courseBestTimesMs.toMap()
        )
    }
}

@Serializable
data class SavedCourseSplits(
    val id: String,
    val sectionTimes: Map<String, Set<Int>>,
    val expiresAt: Long,
    val courseBestTimesMs: Map<String, Long> = emptyMap()
)