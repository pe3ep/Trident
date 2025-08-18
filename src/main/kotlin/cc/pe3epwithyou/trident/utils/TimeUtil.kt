package cc.pe3epwithyou.trident.utils

import java.util.concurrent.TimeUnit

object TimeUtil {
    fun parseMmSsMmmToTimeUnit(input: String, targetUnit: TimeUnit): Long {
        val regex = Regex("""^(\d{1,2}):(\d{1,2})\.(\d{3})$""")
        val match = regex.matchEntire(input)
            ?: throw IllegalArgumentException("Invalid format, expected MM:ss.mmm (got: $input)")

        val (minStr, secStr, msStr) = match.destructured
        val minutes = minStr.toLong()
        val seconds = secStr.toLong()
        val millis = msStr.toLong()

        require(seconds in 0..59) { "Seconds must be 0..59 (got: $seconds)" }
        require(millis in 0..999) { "Milliseconds must be 0..999 (got: $millis)" }

        val totalMillis = minutes * 60_000L + seconds * 1_000L + millis
        return targetUnit.convert(totalMillis, TimeUnit.MILLISECONDS)
    }
}