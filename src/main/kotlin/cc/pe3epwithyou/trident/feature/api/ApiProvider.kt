package cc.pe3epwithyou.trident.feature.api

import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.network.chat.Component

enum class ApiProvider : NameableEnum {
    TRIDENT, SELF_TOKEN;

    override fun getDisplayName(): Component =
        Component.translatable("config.trident.global.api_provider.provider.${name.lowercase()}")

    val fetchUrl: String
        get() = when (this) {
//            TRIDENT -> "https://trident.pe3epwithyou.cc/api/query"
            TRIDENT -> "http://localhost:3000/api/query"
            SELF_TOKEN -> "https://api.mccisland.net/graphql"
        }
}