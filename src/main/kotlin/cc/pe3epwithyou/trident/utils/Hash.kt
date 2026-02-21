package cc.pe3epwithyou.trident.utils

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

fun sha1(input: String): String = hashString(input, "SHA-1")
fun sha256(input: String): String = hashString(input, "SHA-256")

private fun hashString(input: String, algorithm: String): String {
    val bytes = input.toByteArray(StandardCharsets.UTF_8)
    val md = MessageDigest.getInstance(algorithm)
    val digest = md.digest(bytes)
    return buildString {
        digest.forEach { append("%02x".format(it)) }
    }
}