package cc.pe3epwithyou.trident.feature.discord

import cc.pe3epwithyou.trident.utils.sha256

object ProdCheck {
    private val HASHES = buildList {
        add("e927084bb931f83eece6780afd9046f121a798bf3ff3c78a9399b08c1dfb1aec")
        add("0c932ffaa687c756c4616a24eb49389213519ea8d18e0d9bdfd2d335771c35c7")
        add("7f0d15bbb2ffaee1bbf0d23e5746afb753333d590f71ff8a5a186d86c3e79dda")
        add("09445264a9c515c83fc5a0159bda82e25d70d499f80df4a2d1c2f7e2ae6af997")
    }

    fun isProd(ip: String): Boolean = !HASHES.contains(sha256(ip.lowercase()))
}