package cc.pe3epwithyou.trident.utils

import com.noxcrew.noxesium.core.util.OffsetStringFormatter.ComponentOffset

object OffsetFormatter {
    const val PREFIX_LENGTH: Int = "%trident_offset%".length

    /**
     * Serializes the given offset into a string.
     */
    fun write(offset: ComponentOffset): String {
        return "%trident_offset%" + offset.x + "," + offset.y
    }

    /**
     * Parses the given input string and returns its X value.
     */
    fun parseX(input: String?): Float? {
        if (input != null && input.startsWith("%trident_offset%")) {
            val base = input.substring(PREFIX_LENGTH)
            val commaIndex = base.indexOf(",")
            if (commaIndex != -1) {
                val number = base.substring(0, commaIndex)
                try {
                    return number.toFloat()
                } catch (x: NumberFormatException) {
                }
            }
        }
        return null
    }

    /**
     * Parses the given input string and returns its Y value.
     */
    fun parseY(input: String?): Float? {
        if (input != null && input.startsWith("%trident_offset%")) {
            val base = input.substring(PREFIX_LENGTH)
            val commaIndex = base.indexOf(",")
            if (commaIndex != -1) {
                val number = base.substring(commaIndex + 1)
                try {
                    return number.toFloat()
                } catch (x: NumberFormatException) {
                }
            }
        }
        return null
    }
}