package cc.pe3epwithyou.trident.utils

object Testing {

    fun getMessage(username: String): String {
        return when (username) {
            "TheMysterys" -> "It's a mystery"
            else -> "Thanks for using Trident"
        }
    }
}