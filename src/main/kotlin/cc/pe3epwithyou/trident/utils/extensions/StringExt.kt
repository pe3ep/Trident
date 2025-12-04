package cc.pe3epwithyou.trident.utils.extensions

object StringExt {
    fun String.parseFormattedInt(): Int? {
        return this.replace(",", "").toIntOrNull()
    }
}