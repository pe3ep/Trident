package cc.pe3epwithyou.trident.feature.doll.chroma

import cc.pe3epwithyou.trident.utils.Resources
import net.minecraft.resources.Identifier

enum class Chroma(
    val colors: List<Int>,
    val chromaName: String
) {
    BASIC(listOf(10581067, 4869991, 15460827, 11365047), "Basic"),
    STEEL_GOLD(listOf(15769882, 13227747, 6636596, 16764249), "Steel Gold"),
    RED_HUNTER(listOf(4409441, 8751521, 11025982, 14718792), "Red Hunter"),
    ANCIENT_GOLD(listOf(14063401, 4870505, 12765647, 12058457), "Ancient Gold"),
    GHOSTLY_BUSINESS(listOf(15917529, 5265274, 7882849, 9306051), "Ghostly Business"),
    COLD_VELVET(listOf(10560084, 2983816, 12312543, 4784083), "Cold Velvet"),
    PURPLE_PHANTOM(listOf(4342366, 9805997, 13418141, 15036671), "Purple Phantom"),
    DARK_SCIFI(listOf(3882572, 15900208, 9068203, 16734231), "Dark Scifi"),
    CROWS_SPIRIT(listOf(6899868, 7876146, 12692710, 8060907), "Crows Spirit"),
    EMBER(listOf(14579019, 5064837, 12289753, 16759362), "Ember"),
    SCIFI_TECH(listOf(5393513, 13883358, 14712390, 3932152), "High Tech"),
    GOLDEN_GUARDIAN(listOf(6379130, 15896902, 9520201, 16731521), "Golden Guardian"),
    BLUE_POSEIDONS(listOf(3706313, 13407047, 8867406, 12451803), "Blue Poseidon"),
    CRYSTAL_ICE(listOf(7008255, 6444114, 16777215, 7805183), "Crystal Ice"),
    SENTINEL_GOLD(listOf(16758587, 5190949, 9053477, 15158783), "Golden Sentinel"),
    DRAGON_RED(listOf(13712394, 16769357, 15722940, 5103725), "Dragon Red"),
    APEX_BLUE(listOf(4096440, 13096665, 12219682, 9240575), "Apex Blue"),
    MECH_GREEN(listOf(5871699, 15576891, 3161675, 16746288), "Mech Green"),

    ;
    val itemTexture: Identifier
        get() = Resources.mcc("textures/island_items/infinibag/chroma_set/${this.name.lowercase()}.png")

}