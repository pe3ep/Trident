package cc.pe3epwithyou.trident.feature.dojo

import cc.pe3epwithyou.trident.config.ConfigUtil
import cc.pe3epwithyou.trident.feature.dojo.hud.states.CourseState
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.main
import cc.pe3epwithyou.trident.utils.nonCriticalIO
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.TextColor
import java.nio.file.Files
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

object DojoStorage {
    private val savedSplitsPath =
        FabricLoader.getInstance().configDir.resolve("trident").resolve("saved_dojo_splits.json")

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private var currentRun: Run? = null

    private var bestSplits: SavedCourseSplits? = null

    fun getCurrentRun(): Run? = currentRun

    fun startRun(type: DojoCourseType, courseName: String) {
        val now = Instant.now()
        val expiresAt = calculateExpiryEpoch(type, now)
        val id =
            UUID.nameUUIDFromBytes("${courseName.lowercase()}:$expiresAt".toByteArray(Charsets.UTF_8))
                .toString()

        currentRun = Run(
            id = id,
            type = type,
            expiresAt = expiresAt,
            collectedMedals = 0,
            sectionTimes = emptySectionTimes(),
            courseState = null
        )

        bestSplits = null

        nonCriticalIO().launch {
            val saved = readSavedSplits().savedCourseSplits.firstOrNull {
                it.id == id && it.type == type && it.expiresAt > now.epochSecond
            }

            main {
                if (currentRun?.id == id) {
                    bestSplits = saved
                }
            }
        }
    }

    fun setCourseState(state: CourseState?) {
        currentRun = currentRun?.copy(courseState = state)
    }

    fun incrementCollectedMedals() {
        currentRun = currentRun?.let { run ->
            run.copy(collectedMedals = run.collectedMedals + 1)
        }
    }

    fun completedCourseCount(section: String): Int =
        currentRun?.sectionTimes?.get(section)?.count { it > 0L } ?: 0

    fun coursePersonalBest(courseId: String): Long? {
        val (section, index) = parseCourseId(courseId) ?: return null

        return bestSplits?.sectionTimes?.get(section)?.getOrNull(index)?.takeIf { it > 0L }
    }

    // Records a split in the current run only.
    fun recordCoursePersonalBest(courseId: String, ticks: Long) {
        require(ticks > 0L)

        val (section, index) = parseCourseId(courseId) ?: return
        val times = currentRun?.sectionTimes?.get(section) ?: return

        while (times.size <= index) {
            times.add(0L)
        }

        if (times[index] == 0L || ticks < times[index]) {
            times[index] = ticks
        }
    }

    fun formattedTime(ticks: Long, alwaysShowMinutes: Boolean = false): String {
        require(ticks >= 0L)

        val totalSeconds = ticks / 20L
        val milliseconds = (ticks % 20L) * 50L
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L

        val time = if (alwaysShowMinutes || minutes > 0L) {
            "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        } else {
            totalSeconds.toString().padStart(2, '0')
        }

        return "$time.${milliseconds.toString().dropLast(1).padStart(2, '0')}"
    }

    fun snapshot(): SavedCourseSplits? {
        val run = currentRun ?: return null

        return SavedCourseSplits(
            id = run.id,
            type = run.type,
            expiresAt = run.expiresAt,
            sectionTimes = run.sectionTimes.mapValues { it.value.toList() })
    }

    fun saveRun() {
        val snapshot = snapshot() ?: return

        nonCriticalIO().launch {
            val now = Instant.now().epochSecond

            val activeEntries =
                readSavedSplits().savedCourseSplits.filter { it.expiresAt > now }.toMutableList()

            val matchingEntries = activeEntries.filter {
                it.id == snapshot.id && it.type == snapshot.type
            }

            activeEntries.removeAll {
                it.id == snapshot.id && it.type == snapshot.type
            }

            if (snapshot.expiresAt > now) {
                val previous = matchingEntries.reduceOrNull(::mergeBestSplits)

                activeEntries += previous?.let {
                    mergeBestSplits(it, snapshot)
                } ?: snapshot
            }

            val text = json.encodeToString(SavedDojoSplits(activeEntries))
            ConfigUtil.writeToConfig(savedSplitsPath, text)
        }
    }

    private fun calculateExpiryEpoch(type: DojoCourseType, now: Instant): Long {
        val utcNow = LocalDateTime.ofInstant(now, ZoneOffset.UTC)

        val nextReset = when (type) {
            DojoCourseType.DAILY -> {
                val todayAtTen = utcNow.toLocalDate().atTime(10, 0)
                if (utcNow.isBefore(todayAtTen)) todayAtTen else todayAtTen.plusDays(1)
            }

            DojoCourseType.MONTHLY -> {
                val firstOfMonthAtTen = utcNow.toLocalDate().withDayOfMonth(1).atTime(10, 0)

                // Make sure code doesn't shit itself at the first of the month BEFORE 10am UTC ;-;
                if (utcNow.isBefore(firstOfMonthAtTen)) {
                    firstOfMonthAtTen
                } else {
                    firstOfMonthAtTen.plusMonths(1)
                }
            }
        }

        return nextReset.toEpochSecond(ZoneOffset.UTC)
    }

    private fun mergeBestSplits(
        previous: SavedCourseSplits, current: SavedCourseSplits
    ): SavedCourseSplits {
        val sections =
            (previous.sectionTimes.keys + current.sectionTimes.keys).associateWith { section ->
                val previousTimes = previous.sectionTimes[section].orEmpty()
                val currentTimes = current.sectionTimes[section].orEmpty()
                val count = maxOf(previousTimes.size, currentTimes.size).coerceAtMost(3)

                (0 until count).map { index ->
                    val previousTime = previousTimes.getOrNull(index) ?: 0L
                    val currentTime = currentTimes.getOrNull(index) ?: 0L

                    when {
                        previousTime <= 0L -> currentTime.coerceAtLeast(0L)
                        currentTime <= 0L -> previousTime
                        else -> minOf(previousTime, currentTime)
                    }
                }
            }

        return current.copy(sectionTimes = sections)
    }

    private fun readSavedSplits(): SavedDojoSplits {
        if (!Files.exists(savedSplitsPath)) return SavedDojoSplits()

        val text = ConfigUtil.readFromConfig(savedSplitsPath) ?: return SavedDojoSplits()

        return runCatching {
            json.decodeFromString<SavedDojoSplits>(text)
        }.onFailure {
            Logger.error("Failed to read saved Dojo splits", it)
        }.getOrDefault(SavedDojoSplits())
    }

    private fun emptySectionTimes() = linkedMapOf<String, MutableList<Long>>(
        "B1" to mutableListOf(),
        "M1" to mutableListOf(),
        "B2" to mutableListOf(),
        "M2" to mutableListOf(),
        "B3" to mutableListOf(),
        "M3" to mutableListOf(),
        "B4" to mutableListOf()
    )

    private fun parseCourseId(courseId: String): Pair<String, Int>? {
        val split = courseId.split('-')
        if (split.size != 2) return null

        val section = split[0]
        val course = split[1].toIntOrNull() ?: return null

        if (section !in emptySectionTimes().keys || course !in 1..3) {
            return null
        }

        return section to (course - 1)
    }

    fun clear() {
        currentRun?.sectionTimes?.values?.forEach { it.clear() }
        currentRun = null
        bestSplits = null
    }
}

data class Run(
    val id: String,
    val type: DojoCourseType,
    val expiresAt: Long,
    val collectedMedals: Int,
    val sectionTimes: MutableMap<String, MutableList<Long>>,
    val courseState: CourseState?
)

@Serializable
enum class DojoCourseType {
    DAILY, MONTHLY
}

@Serializable
data class SavedDojoSplits(
    val savedCourseSplits: List<SavedCourseSplits> = emptyList()
)

@Serializable
data class SavedCourseSplits(
    val id: String,
    val type: DojoCourseType,
    val expiresAt: Long,
    val sectionTimes: Map<String, List<Long>>
)

enum class CompletionType {
    NONE, STANDARD, ADVANCED, EXPERT;
}

enum class CourseDifficulty {
    EASY, MEDIUM, HARD;

    fun textColor(): TextColor = when (this) {
        EASY -> TextColor.GREEN
        MEDIUM -> TextColor.YELLOW
        HARD -> TextColor.RED
    }

    companion object {
        fun fromTextColor(color: TextColor): CourseDifficulty = when (color) {
            TextColor.YELLOW -> MEDIUM
            TextColor.RED -> HARD
            else -> EASY
        }
    }
}